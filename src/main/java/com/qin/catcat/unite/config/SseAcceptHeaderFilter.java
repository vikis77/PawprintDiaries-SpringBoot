package com.qin.catcat.unite.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * SSE Accept头过滤器
 * 确保SSE请求的Accept头正确设置为text/event-stream
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Slf4j
public class SseAcceptHeaderFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();
        
        // 检查是否是SSE请求
        if (path.contains("/api/chat/sse")) {
            log.debug("检测到SSE请求: {}", path);
            
            // 包装请求，修改Accept头
            HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(httpRequest) {
                private final Map<String, String> headerMap = new HashMap<>();
                
                {
                    // 设置Accept头为text/event-stream
                    headerMap.put("Accept", MediaType.TEXT_EVENT_STREAM_VALUE);
                }
                
                @Override
                public String getHeader(String name) {
                    String headerValue = headerMap.get(name);
                    if (headerValue != null) {
                        return headerValue;
                    }
                    return super.getHeader(name);
                }
                
                @Override
                public Enumeration<String> getHeaders(String name) {
                    if (headerMap.containsKey(name)) {
                        Vector<String> values = new Vector<>();
                        values.add(headerMap.get(name));
                        return values.elements();
                    }
                    return super.getHeaders(name);
                }
                
                @Override
                public Enumeration<String> getHeaderNames() {
                    // 合并原始头和新增的头
                    Vector<String> names = new Vector<>();
                    Enumeration<String> originalNames = super.getHeaderNames();
                    while (originalNames.hasMoreElements()) {
                        names.add(originalNames.nextElement());
                    }
                    for (String name : headerMap.keySet()) {
                        if (!names.contains(name)) {
                            names.add(name);
                        }
                    }
                    return names.elements();
                }
            };
            
            // 使用包装后的请求继续处理
            chain.doFilter(wrappedRequest, response);
        } else {
            // 非SSE请求，直接继续处理
            chain.doFilter(request, response);
        }
    }
} 