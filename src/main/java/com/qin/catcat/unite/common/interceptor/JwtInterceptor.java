package com.qin.catcat.unite.common.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.qin.catcat.unite.common.utils.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

//Jwt拦截器
@Component
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("JwtInterceptor >>> preHandle");
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("token")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String token = header.substring(7);

        //验证不通过
        if (!tokenProvider.validateToken(token)) {
            log.info("拦截请求，验证不通过");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        String username = tokenProvider.getUsernameFromToken(token);//获取Token中的username
        String userId = tokenProvider.getUserIdFromJWT(token);//获取Token中的userId
        request.setAttribute("username", username);
        request.setAttribute("userId", userId);
        log.info("拦截请求，验证通过"+" uerId:"+userId);
        return true;
    }
}
