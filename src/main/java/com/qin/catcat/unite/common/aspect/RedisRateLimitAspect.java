package com.qin.catcat.unite.common.aspect;

import com.qin.catcat.unite.common.annotation.RedisRateLimit;
import com.qin.catcat.unite.common.enumclass.CatcatEnumClass;
import com.qin.catcat.unite.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Description Redis限流切面
 * 使用Redis实现分布式限流功能
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-04 00:16
 */
@Aspect
@Component
@RequiredArgsConstructor
public class RedisRateLimitAspect {

    private final StringRedisTemplate redisTemplate;

    @Around("@annotation(com.qin.catcat.unite.common.annotation.RedisRateLimit)")
    public Object rateLimit(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RedisRateLimit rateLimitAnnotation = method.getAnnotation(RedisRateLimit.class);
        
        // 获取限流的key（前缀+IP+方法名）
        String key = rateLimitAnnotation.prefix() + request.getRemoteAddr() + ":" + method.getName();
        
        // 获取限流时间
        int time = rateLimitAnnotation.time();
        TimeUnit timeUnit = rateLimitAnnotation.timeUnit();
        int count = rateLimitAnnotation.count();
        
        // 获取当前计数
        String countString = redisTemplate.opsForValue().get(key);
        int currentCount = countString == null ? 0 : Integer.parseInt(countString);
        
        if (currentCount >= count) {
            throw new BusinessException(CatcatEnumClass.StatusCode.REQUEST_TOO_FREQUENT.getCode(), CatcatEnumClass.StatusCode.REQUEST_TOO_FREQUENT.getMessage());
        }
        
        // 如果是第一次请求，设置过期时间
        if (currentCount == 0) {
            redisTemplate.opsForValue().increment(key);
            redisTemplate.expire(key, time, timeUnit);
        } else {
            redisTemplate.opsForValue().increment(key);
        }
        
        return point.proceed();
    }
} 