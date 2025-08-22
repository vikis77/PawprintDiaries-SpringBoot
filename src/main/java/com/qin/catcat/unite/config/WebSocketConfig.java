package com.qin.catcat.unite.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.qin.catcat.unite.controller.handle.CatWebSocketHandler;
import com.randb.springaichatstarter.websocket.ChatWebSocketHandler;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{

    // 注入 CatWebSocketHandler
    private final CatWebSocketHandler catWebSocketHandler;

    // 构造器注入
    public WebSocketConfig(CatWebSocketHandler catWebSocketHandler) {
        this.catWebSocketHandler = catWebSocketHandler;
    }

    /* 注册WebSocket处理器 */
    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        
        // 指定处理类和访问地址
        registry.addHandler(catWebSocketHandler, "/catLocation")// 设置访问地址：ws://localhost:8080/catLocation
            .setAllowedOrigins("*");// 允许跨域
    }
}
