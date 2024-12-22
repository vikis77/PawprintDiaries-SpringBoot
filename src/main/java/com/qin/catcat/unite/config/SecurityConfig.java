package com.qin.catcat.unite.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.qin.catcat.unite.common.filter.JwtAuthenticationTokenFilter;
import com.qin.catcat.unite.common.interceptor.JwtInterceptor;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

//Spring Boot 配置类
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {
    @Autowired private JwtInterceptor jwtInterceptor;
    @Autowired private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;
    @Autowired private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Autowired private UserDetailsService userDetailsService;

    // 配置了 BCryptPasswordEncoder 用于密码加密
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 配置认证管理器
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

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
                    "/knife4j/**",            // Knife4j resources
                    // 游客可以访问的端点，*表示所有端点
                    "/api/**",
                    "/actuator/**" // 开放Actuator缓存监控端点
                    // "/api/user/login",       // 放行登录端点
                    // "/api/user/register",    // 放行注册端点
                    // "/api/upload/**",        // 放行上传端点
                    // "/api/upload/catImage",  // 放行特定上传端点
                    // "/api/catLocation",      // 放行 WebSocket 端点
                    // "/api/cat/list",      // 放行查询猫猫端点
                    // "/api/cat/findById",     // 放行按ID查询猫猫端点
                    // "/api/cat/findCoordinate", // 放行查询全部猫猫坐标端点
                    // "/api/cat/findCoordinateByDate", // 放行按日期查询猫猫坐标端点
                    // "/api/cat/findCoordinateByPage", // 放行查询单只猫猫坐标端点
                    // "/api/cat/findPhotoByIdforPage", // 放行查询猫猫照片端点
                    // "/api/cat/findCoordinateByDateAndCat", // 放行按日期和猫猫ID查询坐标端点
                    // "/api/cat/analysis",     // 放行数据分析端点
                    // "/api/post/getAllPost",  // 放行首页帖子端点
                    // "/api/post/getPostByPostid",
                    // "/api/post/getPostBySendtimeForPage"
                ).permitAll()
                //对所有请求开启授权保护
                .anyRequest()
                //已认证的请求会被自动授权
                .authenticated()
            );
        
        // 配置 API 登录端点
        // 作用：如果拦截到请求，且处于未登录状态，跳转到API登录端点。认证成功时，调用myAuthenticationSuccessHandler处理
        http.formLogin(form->form
                .loginProcessingUrl("http://localhost:8080/login") // 使用 API 登录端点
                .successHandler(myAuthenticationSuccessHandler) // 认证成功时的处理
                .permitAll() // 允许所有用户访问登录端点
            );
        
        // 禁用csrf，因为通常 API 不需要 CSRF 保护
        http.csrf(csrf->csrf
            .ignoringRequestMatchers("/ws/**") // 忽略 WebSocket 端点的 CSRF 保护
            .disable()
        );
        
        // 配置 CORS，允许所有来源、方法和头部的跨域请求
		http.cors(conf->conf.configurationSource(corsConfigurationSource()));

        //这里涉及到两个过滤器： JwtAuthenticationTokenFilter 和 UsernamePasswordAuthenticationFilter
        //JwtAuthenticationTokenFilter 是自定义的过滤器，用于处理JWT认证；
        //UsernamePasswordAuthenticationFilter 是Spring Security提供的内置的过滤器，用于处理表单登录；
        //addFilterBefore 方法用于将自定义的过滤器添加到Spring Security的过滤器链中，
        // 并指定在UsernamePasswordAuthenticationFilter过滤器之前执行。
        // 因为 JWT 是无状态的认证方式，不需要走用户名密码认证。
        // 如果 JWT 验证通过，就可以直接放行，不需要后续的表单认证。
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        // 禁用默认的登录页面，配置异常处理
        http.exceptionHandling(handling ->handling
            .authenticationEntryPoint((request, response, authException) -> {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                // 提示：未授权
                response.getWriter().write("{\"message\":\"Unauthorized\"}");
            })
            .accessDeniedHandler((request, response, accessDeniedException) -> {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                // 提示：访问被拒绝
                response.getWriter().write("{\"message\":\"Access Denied\"}");
            })
        );

        return http.build();
    }

    //配置了 CORS 的基本设置，允许所有的来源、方法和头部的跨域请求
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