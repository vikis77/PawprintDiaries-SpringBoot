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

        // 从客户端请求中获取 JWT
        String tokenHeader = request.getHeader("Authorization");
        log.info("请求头：{}",tokenHeader);

        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) { // 确保是 Bearer Token 格式
            String token = tokenHeader.substring(7);  // 提取 token 部分
            try {
                //验证token合法性
                // jwtTokenProvider.validateToken(tokenHeader);
                // log.info("Token格式检验通过");

                // String tokenUsername = jwtTokenProvider.getUsernameFromToken(tokenHeader);//从Token中提取用户名
                // String tokenUserId = jwtTokenProvider.getUserIdFromJWT(tokenHeader);//从Token中提取用户ID
                // Date tokenExpiration = jwtTokenProvider.getExpirationDateFromToken(tokenHeader); // 从Token中提取过期时间
                // log.info("Token解析->tokenUsername:{} tokenUserId:{} 过期时间：{}",tokenUsername,tokenUserId,tokenExpiration);

                // 读取SecurityContext里存的登陆用户,与token解析出来的用户进行判断
                // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                // if (authentication != null && authentication.isAuthenticated()) {
                //     Map<String, String> details = (Map<String, String>) authentication.getDetails();
                //     String contextUserId = (String) details.get("userId");
                //     String contextUsername = (String) authentication.getName();
                //     if(contextUserId.equals(tokenUserId) && contextUsername.equals(tokenUsername)){
                //         log.info("JWT内容身份检验通过");
                //         TokenHolder.setToken(tokenHeader); //将token放入ThreadLocal
                //     }else{
                //         throw new JWTIdentityVerificationFailedException("JWT内容身份检验失败");
                //     }
                // }

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
                        // 加载用户详情信息并设置到 SecurityContext
                        UserDetails userDetails = jwtTokenProvider.getUserDetailsFromToken(token);
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);  // 设置认证信息
                        TokenHolder.setToken(token); //将token放入ThreadLocal
                        log.info("JWT用户认证成功，设置到 SecurityContext");
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
        //放通、进入security自带的过滤器
        try {
            chain.doFilter(request, response); //放行
        } finally {
            TokenHolder.clear(); //清除ThreadLocal
        }
    }


}
