package com.qin.catcat.unite.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * SSE编码过滤器
 * 确保SSE响应使用UTF-8编码
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class SseEncodingFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        
        // 检查是否是SSE请求
        if (path.contains("/api/chat/sse")) {
            log.debug("检测到SSE请求: {}", path);
            
            // 设置响应头，确保使用UTF-8编码
            exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, 
                    "text/event-stream;charset=UTF-8");
        }
        
        return chain.filter(exchange);
    }
} 