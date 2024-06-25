package com.qin.catcat.unite.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.qin.catcat.unite.common.enumclass.enumStatusCode;
import com.qin.catcat.unite.common.result.Result;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

//认证失败后的处理
// @Slf4j
// @Component
// public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler{

//     @Override
//     public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
//             AuthenticationException exception) throws IOException, ServletException {

//         log.info(exception.getMessage()+"用户认证失败");


//         Result result = Result.error("认证失败，请重新登录！", enumStatusCode.INTERNAL_SERVER_ERROR);

//         String json = JSON.toJSONString(result);

//         //返回json数据给前端
//         response.setContentType("application/json;charset=UTF-8");
//         response.getWriter().println(json);
//     }
    
// }
