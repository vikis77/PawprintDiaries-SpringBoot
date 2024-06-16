package com.qin.catcat.unite.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// import com.qin.catcat.unite.common.jwt.JwtTokenFilter;
// import com.qin.catcat.unite.common.utils.JwtTokenProviderUtils;

//Spring Boot 配置类
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //配置了 BCryptPasswordEncoder 用于密码加密
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(
                (authz)->authz
                        // .requestMatchers(
                            // "/v3/api-docs/**",       // Springdoc OpenAPI 3
                            // "/swagger-ui/**",        // Swagger UI
                            // "/swagger-ui.html",      // Swagger UI
                            // "/swagger-resources/**", // Swagger resources
                            // "/webjars/**",           // Webjars for Swagger UI
                            // "/knife4j/**",            // Knife4j resources
                            // "/doc.html",               //放行文档
                            // "/login",
                            // "/register"
                            // // "/profile"
                            // ).permitAll()  
                        // .anyRequest().authenticated()) // 其他请求必须经过身份验证
                        .anyRequest().permitAll()) // 允许所有请求无需身份验证
		                .csrf(AbstractHttpConfigurer::disable) // 禁用csrf，因为通常 API 不需要 CSRF 保护
		                .cors(conf->conf.configurationSource(corsConfigurationSource())) // 配置跨域 允许所有来源、方法和头部的跨域请求
		                .sessionManagement(conf->conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 禁用session Spring Security 不会创建或使用 HTTP 会话来存储认证信息，适合 RESTful API 的无状态特性
		                // .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)//指定过滤器
		                .build();
    }

    //配置了 CORS 的基本设置，允许所有的来源、方法和头部的跨域请求。
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}