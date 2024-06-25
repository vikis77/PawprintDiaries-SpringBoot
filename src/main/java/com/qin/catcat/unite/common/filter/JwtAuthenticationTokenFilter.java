package com.qin.catcat.unite.common.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alibaba.fastjson.JSON;
import com.qin.catcat.unite.common.enumclass.enumStatusCode;
import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.exception.JWTIdentityVerificationFailedException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
 
    @Autowired
    JwtTokenProvider jwtTokenProvider;
 
    @Override
    //自定义过滤器方法，用于在每次HTTP请求时执行
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        log.info("JwtAuthenticationTokenFilter->doFilterInternal()");

        // 从客户端请求中获取 JWT
        String tokenHeader = request.getHeader("Authorization");//从请求头中获取JWT
        log.info("tokenHeader:{}",tokenHeader);

        if (tokenHeader != null) {//检查是否存在Token
            try {
                //验证token合法性
                jwtTokenProvider.validateToken(tokenHeader);
                log.info("Token格式检验通过");

                String tokenUsername = jwtTokenProvider.getUsernameFromToken(tokenHeader);//从Token中提取用户名
                String tokenUserId = jwtTokenProvider.getUserIdFromJWT(tokenHeader);//从Token中提取用户ID
                log.info("Token解析->tokenUsername:{} tokenUserId:{}",tokenUsername,tokenUserId);

                // 读取SecurityContext里存的登陆用户,与token解析出来的用户进行判断
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated()) {
                    Map<String, String> details = (Map<String, String>) authentication.getDetails();
                    String contextUserId = (String) details.get("userId");
                    String contextUsername = (String) authentication.getName();
                    if(contextUserId.equals(tokenUserId) && contextUsername.equals(tokenUsername)){
                        log.info("JWT内容身份检验通过");
                    }else{
                        throw new JWTIdentityVerificationFailedException("JWT内容身份检验失败");
                    }
                    // log.info("@@userId:{}",contextUserId);
                }

            }catch (Exception ex){
                //解析token报错，防止乱传token
                log.info(ex.toString());
                logger.warn("解析token报错!!");
            }
        }
        //放通、进入security自带的过滤器
        chain.doFilter(request, response);
    }
}