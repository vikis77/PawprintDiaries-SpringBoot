package com.qin.catcat.unite.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.common.enumclass.CatcatEnumClass.StatusCode;
import com.qin.catcat.unite.common.filter.JwtAuthenticationTokenFilter;
import com.qin.catcat.unite.common.filter.RateLimitFilter;
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
    @Autowired private RateLimitFilter rateLimitFilter;
    @Autowired private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Autowired private UserDetailsService userDetailsService;

    // 注册过滤器Bean
    // @Bean
    // public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
    //     return new JwtAuthenticationTokenFilter();
    // }

    // @Bean
    // public RateLimitFilter rateLimitFilter() {
    //     return new RateLimitFilter();
    // }

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

        // 1. 先禁用不需要的配置
        http.csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 2. 配置路径权限
        http.authorizeHttpRequests(authorize -> authorize
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers(
                "/doc.html",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/swagger-resources/**",
                "/webjars/**",
                "/knife4j/**",
                "/api/**",
                "/actuator/**"
            ).permitAll()
            .anyRequest()
            .authenticated()
        );

        // 3. 过滤器链：请求 -> rateLimitFilter -> jwtAuthenticationTokenFilter -> BasicAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationTokenFilter, BasicAuthenticationFilter.class)
            .addFilterBefore(rateLimitFilter, JwtAuthenticationTokenFilter.class)
            .securityContext(AbstractHttpConfigurer::disable);  // 禁用默认的SecurityContext配置，因为我们在过滤器中自己管理

        // 4. 配置CORS
        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.addAllowedOriginPattern("*");
            configuration.addAllowedMethod("*");
            configuration.addAllowedHeader("*");
            configuration.setAllowCredentials(true);
            configuration.setMaxAge(3600L);
            configuration.addExposedHeader("Authorization");
            return configuration;
        }));

        // 5. 配置异常处理
        http.exceptionHandling(handling -> handling
            .authenticationEntryPoint((request, response, authException) -> {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_OK);
                Result<String> result = Result.error(StatusCode.UNAUTHORIZED.getMessage(), StatusCode.UNAUTHORIZED.getCode());
                response.getWriter().write(new ObjectMapper().writeValueAsString(result));
            })
            .accessDeniedHandler((request, response, accessDeniedException) -> {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_OK);
                Result<String> result = Result.error(StatusCode.ACCESS_DENIED.getMessage(), StatusCode.ACCESS_DENIED.getCode());
                response.getWriter().write(new ObjectMapper().writeValueAsString(result));
            })
        );

        return http.build();
    }

    //配置了 CORS 的基本设置，允许所有的来源、方法和头部的跨域请求
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.addExposedHeader("Authorization");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}