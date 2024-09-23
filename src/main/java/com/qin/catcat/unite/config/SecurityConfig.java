package com.qin.catcat.unite.config;

import org.apache.ibatis.javassist.tools.framedump;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.qin.catcat.unite.common.filter.JwtAuthenticationTokenFilter;
import com.qin.catcat.unite.common.interceptor.JwtInterceptor;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

// import com.qin.catcat.unite.common.jwt.JwtTokenFilter;
// import com.qin.catcat.unite.common.utils.JwtTokenProviderUtils;

//Spring Boot 配置类
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {
    @Autowired private JwtInterceptor jwtInterceptor;
    @Autowired private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;
    @Autowired private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    // 配置了 BCryptPasswordEncoder 用于密码加密
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //！！写这段代码和直接在DBUserDetailsManager.class中加@Component注解是一样的
    // @Bean
    // public UserDetailsService userDetailsService(){
    //     //创建基于数据库的用户信息管理器
    //     DBUserDetailsManager manager = new DBUserDetailsManager();
    //     return manager;        
    // }

    //过滤器链
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("SecurityConfig >>> 进入Security过滤器配置");

        //开启授权保护
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    "/doc.html",            //放行/doc.html
                    "/v3/api-docs/**",       // Springdoc OpenAPI 3
                    "/swagger-ui/**",        // Swagger UI
                    "/swagger-ui.html",      // Swagger UI
                    "/swagger-resources/**", // Swagger resources
                    "/webjars/**",           // Webjars for Swagger UI
                    "/knife4j/**",            // Knife4j resources)
                    "/user/login", // 放行登录端点
                    "/user/register", // 放行注册端点
                    "/upload/**", // 放行上传端点
                    "/upload/catImage", // 放行特定上传端点
                    "/catLocation", // // 放行 WebSocket 端点
                    "/cat/findAll", // 放行查询猫猫端点
                    "/cat/findCoordinate", // 放行查询全部猫猫坐标端点
                    "/cat/findCoordinateByPage", // 放行查询单只猫猫坐标端点
                    "/cat/findPhotoByIdforPage", // 放行查询猫猫照片端点
                    "/cat/analysis", // 放行数据分析端点
                    "/post/getAllPost", // 放行首页帖子端点
                    "post/getPostByPostid",
                    "post/getPostBySendtimeForPage"
                ).permitAll()
                //对所有请求开启授权保护
                .anyRequest()
                //已认证的请求会被自动授权
                .authenticated()
            );
        
        // 配置 API 登录端点
        http.formLogin(form->{form
                .loginProcessingUrl("http://localhost:8080/login") // 使用 API 登录端点
                .successHandler(myAuthenticationSuccessHandler) // 认证成功时的处理
                // .failureHandler(myAuthenticationFailureHandler) // 认证失败时的处理
                // .loginProcessingUrl("/login")
                .permitAll();
            }
        );
        
        // 禁用csrf，因为通常 API 不需要 CSRF 保护
        http.csrf(csrf->csrf.
            ignoringRequestMatchers("/ws/**"). // 忽略 WebSocket 端点的 CSRF 保护
            disable()
        );
        
        // 配置 CORS，允许所有来源、方法和头部的跨域请求
		http.cors(conf->conf.configurationSource(corsConfigurationSource()));

        //添加 JwtAuthenticationTokenFilter 过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        // 禁用默认的登录页面，确保所有 API 请求返回 JSON 格式的错误响应
        http.exceptionHandling(handling ->handling
        .authenticationEntryPoint((request, response, authException) -> {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\":\"Unauthorized\"}");
        }));
        return http.build();
    }

    //配置了 CORS 的基本设置，允许所有的来源、方法和头部的跨域请求。
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*"); // 允许所有来源
        configuration.addAllowedMethod("*"); // 允许所有方法
        configuration.addAllowedHeader("*"); // 允许所有头部
        configuration.addExposedHeader("Authorization"); // 允许前端获取Authorization头
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}