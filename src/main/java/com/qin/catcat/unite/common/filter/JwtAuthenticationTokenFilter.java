package com.qin.catcat.unite.common.filter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.alibaba.fastjson.JSON;
import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.common.enumclass.CatcatEnumClass;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.common.utils.TokenHolder;
import com.qin.catcat.unite.exception.BusinessException;
import com.qin.catcat.unite.exception.JWTIdentityVerificationFailedException;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by qin on 2024//.
 *  <p>该Filter的作用是，
 *  1.从客户端的Request Header中拿到Token
 *  2.对Token进行合法性验证
 *  3.从Token中提取出用户名和用户ID
 *  4.与SecurityContext里存的登陆用户进行对比
 *  5.如果一致，则放行，否则抛出JWTIdentityVerificationFailedException
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityContextRepository securityContextRepository;

    @Autowired
    public JwtAuthenticationTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
        // 直接创建SecurityContextRepository，不依赖注入
        this.securityContextRepository = new DelegatingSecurityContextRepository(
            new RequestAttributeSecurityContextRepository(),
            new HttpSessionSecurityContextRepository()
        );
    }

    /**
     * 在每次HTTP请求时执行该过滤器
     * @param request
     * @param response
     * @param chain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        log.info("JwtAuthenticationTokenFilter->doFilterInternal()");
        // log.info("过滤器开始前的认证信息: {}", SecurityContextHolder.getContext().getAuthentication());

        // 对OPTIONS请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String tokenHeader = request.getHeader("Authorization");
        log.info("请求头：{}", tokenHeader);
        
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);
            try {
                if (jwtTokenProvider.validateToken(token)) {
                    log.info("Token格式检验通过");

                    String tokenUsername = jwtTokenProvider.getUsernameFromToken(token);
                    String tokenUserId = jwtTokenProvider.getUserIdFromJWT(token);
                    Date tokenExpiration = jwtTokenProvider.getExpirationDateFromToken(token);
                    log.info("Token解析->tokenUsername:{} tokenUserId:{} 过期时间：{}", tokenUsername, tokenUserId, tokenExpiration);

                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication == null || !authentication.isAuthenticated() || 
                        authentication instanceof AnonymousAuthenticationToken) {
                        UserDetails userDetails = jwtTokenProvider.getUserDetailsFromToken(token);
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        // 创建新的SecurityContext并设置认证信息
                        SecurityContext context = SecurityContextHolder.createEmptyContext();
                        context.setAuthentication(authToken);
                        SecurityContextHolder.setContext(context);
                        
                        // 显式保存SecurityContext
                        securityContextRepository.saveContext(context, request, response);
                        
                        TokenHolder.setToken(token);
                        log.info("JWT用户认证成功，已将认证令牌设置到 SecurityContext");
                        // log.info("SecurityContext中的认证信息：{}", SecurityContextHolder.getContext().getAuthentication());
                        // log.info("当前线程：{}", Thread.currentThread().getName());
                    }
                } else {
                    log.warn("Token验证失败");
                }
            } catch (ExpiredJwtException ex) {
                log.error("Token已过期: {}", ex.getMessage());

                // 清空TokenHolder
                TokenHolder.clear();

                // 设置响应头
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                response.setHeader("Access-Control-Allow-Headers", "*");
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_OK);
                
                Result<?> result = Result.error(CatcatEnumClass.StatusCode.TOKEN_EXPIRED.getCode(), CatcatEnumClass.StatusCode.TOKEN_EXPIRED.getMessage());
                response.getWriter().write(JSON.toJSONString(result));
                response.getWriter().flush();
                return;
            } catch (Exception ex) {
                log.error("Token解析失败: {}", ex.getMessage());

                // 清空TokenHolder
                TokenHolder.clear();

                // 设置响应头
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                response.setHeader("Access-Control-Allow-Headers", "*");
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_OK);
                
                Result<?> result = Result.fail(CatcatEnumClass.StatusCode.TOKEN_INVALID.getCode(), CatcatEnumClass.StatusCode.TOKEN_INVALID.getMessage());
                response.getWriter().write(JSON.toJSONString(result));
                response.getWriter().flush();
                return;
            }
        } else {
            log.info("请求头为空或不是Bearer token格式，设置为游客身份");
            UserDetails userDetails = jwtTokenProvider.getUserDetailsFromToken(null);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authToken);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);
            
            log.info("已设置为游客身份");
        }

        try {
            chain.doFilter(request, response);
        } finally {
            // log.info("过滤器执行完成后的认证信息: {}", SecurityContextHolder.getContext().getAuthentication());
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return request.getMethod().equals("OPTIONS") || 
               Arrays.asList("/api/user/login", "/api/user/register").contains(path);
    }

}
