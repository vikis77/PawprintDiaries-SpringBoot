package com.qin.catcat.unite.common.filter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alibaba.fastjson.JSON;
import com.qin.catcat.unite.common.enumclass.enumStatusCode;
import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.common.utils.TokenHolder;
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
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

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

        // 如果请求头不为空
        // if (request.getHeader("Authorization") != null) {
            String tokenHeader = request.getHeader("Authorization");
            log.info("请求头：{}",tokenHeader);
            // 如果请求头中包含Token
            if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) { // 确保是 Bearer Token 格式
                String token = tokenHeader.substring(7);  // 提取 token 部分
                try {
                    // 验证 Token 的合法性
                    if (jwtTokenProvider.validateToken(token)) {
                        log.info("Token格式检验通过");

                        // 从 Token 中提取用户名、用户ID和过期时间
                        String tokenUsername = jwtTokenProvider.getUsernameFromToken(token);
                        String tokenUserId = jwtTokenProvider.getUserIdFromJWT(token);
                        Date tokenExpiration = jwtTokenProvider.getExpirationDateFromToken(token);
                        log.info("Token解析->tokenUsername:{} tokenUserId:{} 过期时间：{}", tokenUsername, tokenUserId, tokenExpiration);

                        // 检查 SecurityContext 中是否已经有用户认证信息
                        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                        if (authentication == null || !authentication.isAuthenticated()) {
                            // 从token中获取核心用户信息（用户名、权限等）并设置到 SecurityContext
                            // userDetails 通常包含：
                            // - username：用户名
                            // - password：密码（可能已加密）
                            // - authorities：权限列表
                            // - accountNonExpired：账户是否过期
                            // - accountNonLocked：账户是否锁定
                            // - credentialsNonExpired：凭证是否过期
                            // - enabled：账户是否启用
                            UserDetails userDetails = jwtTokenProvider.getUserDetailsFromToken(token); // 目前 userDetails 只存储了用户名
                            // 创建认证令牌，包含核心用户信息。参数说明：
                            // 1. userDetails：通常是 UserDetails 对象，用户详情信息，包含用户名、密码、权限等
                            // 2. null：通常是密码，已认证后设为 null
                            // 3. userDetails.getAuthorities()：用户的权限集合
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            // 设置请求相关的额外信息（如IP地址、session ID等）
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);  // 将认证令牌设置到 SecurityContext
                            TokenHolder.setToken(token); //将token放入ThreadLocal
                            log.info("JWT用户认证成功，已将认证令牌设置到 SecurityContext，已将token放入ThreadLocal");
                        }
                    } else {
                        log.warn("Token验证失败");
                    }
                } catch (ExpiredJwtException ex) {
                    log.error("Token已过期: {}", ex.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 返回 401 错误
                    response.getWriter().write(JSON.toJSONString(Result.fail(enumStatusCode.TOKEN_EXPIRED.getCode(), "Token已过期")));
                    return;
                } catch (Exception ex) {
                    log.error("Token解析失败: {}", ex.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 返回 401 错误
                    response.getWriter().write(JSON.toJSONString(Result.fail(enumStatusCode.TOKEN_INVALID.getCode(), "Token无效")));
                    return;
                }
            }
        // } 
        // else {
        //     log.info("请求头为空，设置为游客身份");
        //     // 设置为游客身份
        //     UserDetails userDetails = jwtTokenProvider.getUserDetailsFromToken(null);
        //     UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
        //             userDetails, null, userDetails.getAuthorities());
        //     SecurityContextHolder.getContext().setAuthentication(authToken);
        // }
        //放通、进入security自带的过滤器
        try {
            chain.doFilter(request, response); //放行
        } finally {
            TokenHolder.clear(); //清除ThreadLocal
        }
    }


}
