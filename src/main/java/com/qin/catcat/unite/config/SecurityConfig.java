package com.qin.catcat.unite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//Spring Boot 配置类
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    //定义一个 BCryptPasswordEncoder 的 Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /**
     *  Spring Security 配置 放行接口
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(
                (authz)->authz
                        .requestMatchers(
                            "/v3/api-docs/**",       // Springdoc OpenAPI 3
                            "/swagger-ui/**",        // Swagger UI
                            "/swagger-ui.html",      // Swagger UI
                            "/swagger-resources/**", // Swagger resources
                            "/webjars/**",           // Webjars for Swagger UI
                            "/knife4j/**",            // Knife4j resources
                            "/doc.html",               //放行文档
                            "/login",
                            "register"
                            ).permitAll()  
                        .anyRequest().authenticated()) // 其他请求必须经过身份验证
                        // .anyRequest().permitAll()) // 允许所有请求无需身份验证
		                .csrf(AbstractHttpConfigurer::disable) // 禁用csrf，因为通常 API 不需要 CSRF 保护
		                // .cors(conf->conf.configurationSource(corsConfigurationSource())) // 配置跨域
		                // .sessionManagement(conf->conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 禁用session
		                // .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)//指定过滤器
		                .build();
    }
}