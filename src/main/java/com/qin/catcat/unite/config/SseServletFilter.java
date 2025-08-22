package com.qin.catcat.unite.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * SSE Servlet过滤器
 * 确保SSE响应使用UTF-8编码
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class SseServletFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();
        
        // 检查是否是SSE请求
        if (path.contains("/api/chat/sse")) {
            log.debug("检测到SSE Servlet请求: {}", path);
            
            // 设置响应编码为UTF-8
            response.setCharacterEncoding("UTF-8");
            
            // 如果是HTTP响应，设置内容类型
            if (response instanceof HttpServletResponse) {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                
                // 不要修改原始的Content-Type，只添加charset参数
                String contentType = httpResponse.getContentType();
                if (contentType == null || !contentType.contains("charset")) {
                    httpResponse.setContentType("text/event-stream;charset=UTF-8");
                }
                
                httpResponse.setHeader("Cache-Control", "no-cache");
                httpResponse.setHeader("Connection", "keep-alive");
                
                // 添加CORS头，允许跨域请求
                httpResponse.setHeader("Access-Control-Allow-Origin", "*");
                httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                httpResponse.setHeader("Access-Control-Allow-Headers", "*");
                
                log.debug("设置SSE响应头: Content-Type={}, charset={}", 
                        httpResponse.getContentType(), response.getCharacterEncoding());
            }
        }
        
        chain.doFilter(request, response);
    }
} 