package com.qin.catcat.unite.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.qin.catcat.unite.common.interceptor.JwtInterceptor;

import lombok.extern.slf4j.Slf4j;

//不过这里？？
@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    // @Autowired
    // private JwtInterceptor jwtInterceptor;

    // @Override
    // public void addInterceptors(InterceptorRegistry registry) {
    //     log.info("WebConfig->addInterceptors()");
    //     registry.addInterceptor(jwtInterceptor)
    //             .addPathPatterns("/**")
    //             .excludePathPatterns("/login","/register"); // 登录接口不需要 JWT 验证
    // }

    // @Override
    // public void addCorsMappings(CorsRegistry registry) {
    //     registry.addMapping("/**") // 对所有请求路径生效
    //             .allowedOrigins("https://pawprintdiaries.luckyiur.com") // 允许的来源
    //             .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的请求方法
    //             .allowCredentials(true) // 是否允许凭证
    //             .allowedHeaders("*") // 允许的请求头
    //             .exposedHeaders("*") // 允许暴露的响应头
    //             .maxAge(3600); // 预检请求的有效期
    // }
}
