package com.qin.catcat.unite.common.filter;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.common.enumclass.CatcatEnumClass.StatusCode;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description 全局限流过滤器
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-04 11:15
 * 
 * 实现多层限流防护：
 * 1. 单IP限流：每秒最多3次请求
 * 2. 全局限流：所有IP总请求数每秒不超过100次
 * 3. URI限流：单个URI每秒最多50次请求
 * 4. IP黑名单：自动封禁异常IP
 * 5. URI黑名单：自动封禁异常URI
 * 
 *  1.多层限流：
        单IP限流：每个IP每秒最多3次请求
        全局限流：所有IP总请求数每秒不超过100次
    2. IP识别增强：
        支持获取真实IP，包括代理后的IP
        检查多个HTTP头：X-Forwarded-For、Proxy-Client-IP等
    3. 黑名单机制：
        IP黑名单：自动封禁异常IP
        URI黑名单：自动封禁被频繁攻击的URI
        黑名单有效期24小时
    4. 失败计数：
        记录IP失败次数
        记录URI失败次数
        超过阈值（10次）自动加入黑名单
    5.差异化响应：
        全局限流：提示"系统繁忙"
        URI限流：提示"当前接口访问人数过多"
        IP限流：提示"请求过于频繁"
        黑名单：提示"访问已被限制"
        这种机制可以有效防护：
        DDOS攻击：通过全局限流和IP黑名单机制
        CC攻击：通过URI限流和失败计数机制
        恶意爬虫：通过IP限流和黑名单机制
        接口滥用：通过多层限流机制

    这种机制可以有效防护：
        DDOS攻击：通过全局限流和IP黑名单机制
        CC攻击：通过URI限流和失败计数机制
        恶意爬虫：通过IP限流和黑名单机制
        接口滥用：通过多层限流机制
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String RATE_LIMIT_IP_PREFIX = "rate:ip:";           // 单IP限流
    private static final String RATE_LIMIT_GLOBAL = "rate:global";           // 全局限流
    private static final String RATE_LIMIT_URI_PREFIX = "rate:uri:";         // URI限流
    private static final String IP_BLACKLIST_PREFIX = "blacklist:ip:";       // IP黑名单
    private static final String URI_BLACKLIST_PREFIX = "blacklist:uri:";     // URI黑名单
    private static final String IP_FAIL_COUNT_PREFIX = "fail:ip:";           // IP失败次数
    private static final String URI_FAIL_COUNT_PREFIX = "fail:uri:";         // URI失败次数

    private static final int RATE_LIMIT_PER_SECOND_IP = 3;      // 单IP每秒限制
    private static final int RATE_LIMIT_PER_SECOND_GLOBAL = 100; // 全局每秒限制
    private static final int RATE_LIMIT_PER_SECOND_URI = 50;    // 单URI每秒限制
    private static final int FAIL_THRESHOLD = 10;               // 失败次数阈值
    private static final int BLACKLIST_EXPIRE_HOURS = 24;       // 黑名单封禁时间（小时）
    private static final int WINDOW_SECONDS = 1;                // 时间窗口（秒）

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String uri = request.getRequestURI();
        String clientIp = getClientIp(request);
        log.info("RateLimitFilter -> 进入限流过滤器，IP: {}, URI: {}", clientIp, uri);

        // 1. 检查白名单
        if (isWhitelisted(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 检查黑名单
        if (isBlacklisted(clientIp, uri)) {
            handleBlacklistResponse(response, clientIp, uri);
            return;
        }

        // 3. 全局限流检查
        if (!checkGlobalLimit()) {
            handleLimitExceeded(response, "系统繁忙，请稍后重试");
            return;
        }

        // 4. URI限流检查
        if (!checkUriLimit(uri)) {
            handleLimitExceeded(response, "当前接口访问人数过多，请稍后重试");
            incrementFailCount(URI_FAIL_COUNT_PREFIX + uri);
            return;
        }

        // 5. IP限流检查
        if (!checkIpLimit(clientIp, uri)) {
            handleLimitExceeded(response, "请求过于频繁，请稍后重试");
            incrementFailCount(IP_FAIL_COUNT_PREFIX + clientIp);
            return;
        }

        // 6. 检查是否需要加入黑名单
        checkAndAddToBlacklist(clientIp, uri);

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private boolean isWhitelisted(String uri) {
        return uri.contains("/doc.html") || 
               uri.contains("/v3/api-docs") || 
               uri.contains("/swagger-ui") || 
               uri.contains("/knife4j");
    }

    private boolean isBlacklisted(String ip, String uri) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(IP_BLACKLIST_PREFIX + ip)) ||
               Boolean.TRUE.equals(redisTemplate.hasKey(URI_BLACKLIST_PREFIX + uri));
    }

    private boolean checkGlobalLimit() {
        String countStr = redisTemplate.opsForValue().get(RATE_LIMIT_GLOBAL);
        int count = countStr == null ? 0 : Integer.parseInt(countStr);
        if (count >= RATE_LIMIT_PER_SECOND_GLOBAL) {
            return false;
        }
        if (count == 0) {
            redisTemplate.opsForValue().set(RATE_LIMIT_GLOBAL, "1", WINDOW_SECONDS, java.util.concurrent.TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().increment(RATE_LIMIT_GLOBAL);
        }
        return true;
    }

    private boolean checkUriLimit(String uri) {
        String key = RATE_LIMIT_URI_PREFIX + uri;
        String countStr = redisTemplate.opsForValue().get(key);
        int count = countStr == null ? 0 : Integer.parseInt(countStr);
        if (count >= RATE_LIMIT_PER_SECOND_URI) {
            return false;
        }
        if (count == 0) {
            redisTemplate.opsForValue().set(key, "1", WINDOW_SECONDS, java.util.concurrent.TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().increment(key);
        }
        return true;
    }

    private boolean checkIpLimit(String ip, String uri) {
        String key = RATE_LIMIT_IP_PREFIX + ip + ":" + uri;
        String countStr = redisTemplate.opsForValue().get(key);
        int count = countStr == null ? 0 : Integer.parseInt(countStr);
        if (count >= RATE_LIMIT_PER_SECOND_IP) {
            return false;
        }
        if (count == 0) {
            redisTemplate.opsForValue().set(key, "1", WINDOW_SECONDS, java.util.concurrent.TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().increment(key);
        }
        return true;
    }

    private void incrementFailCount(String key) {
        String countStr = redisTemplate.opsForValue().get(key);
        int count = countStr == null ? 1 : Integer.parseInt(countStr) + 1;
        redisTemplate.opsForValue().set(key, String.valueOf(count), 1, java.util.concurrent.TimeUnit.HOURS);
    }

    private void checkAndAddToBlacklist(String ip, String uri) {
        // 检查IP失败次数
        String ipFailKey = IP_FAIL_COUNT_PREFIX + ip;
        String ipFailCountStr = redisTemplate.opsForValue().get(ipFailKey);
        int ipFailCount = ipFailCountStr == null ? 0 : Integer.parseInt(ipFailCountStr);
        if (ipFailCount >= FAIL_THRESHOLD) {
            redisTemplate.opsForValue().set(IP_BLACKLIST_PREFIX + ip, "1", 
                BLACKLIST_EXPIRE_HOURS, java.util.concurrent.TimeUnit.HOURS);
            log.warn("IP: {} 已被加入黑名单", ip);
        }

        // 检查URI失败次数
        String uriFailKey = URI_FAIL_COUNT_PREFIX + uri;
        String uriFailCountStr = redisTemplate.opsForValue().get(uriFailKey);
        int uriFailCount = uriFailCountStr == null ? 0 : Integer.parseInt(uriFailCountStr);
        if (uriFailCount >= FAIL_THRESHOLD) {
            redisTemplate.opsForValue().set(URI_BLACKLIST_PREFIX + uri, "1", 
                BLACKLIST_EXPIRE_HOURS, java.util.concurrent.TimeUnit.HOURS);
            log.warn("URI: {} 已被加入黑名单", uri);
        }
    }

    private void handleBlacklistResponse(HttpServletResponse response, String ip, String uri) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        Result<String> result = Result.error("访问已被限制，请24小时后重试", StatusCode.REQUEST_TOO_FREQUENT.getCode());
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
        log.warn("拦截黑名单访问 -> IP: {}, URI: {}", ip, uri);
    }

    private void handleLimitExceeded(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        Result<String> result = Result.error(message, StatusCode.REQUEST_TOO_FREQUENT.getCode());
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
    }
} 