package com.qin.catcat.unite.controller;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.randb.springaichatstarter.core.ChatService;
import com.randb.springaichatstarter.dto.ChatRequest;
import com.randb.springaichatstarter.dto.ChatResponse;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@RestController
@RequestMapping("/api/test")
@Slf4j
public class TestController {

    private final ChatService chatService;

    @Autowired
    public TestController(@Qualifier("qwen") ChatService chatService) {
        this.chatService = chatService;
        log.info("TestController初始化成功，使用的ChatService实现: {}", 
                chatService != null ? chatService.getClass().getName() : "null");
    }

    @PostMapping("/chat")
    public ChatResponse testChat(@RequestBody ChatRequest request) {
        log.info("收到测试聊天请求: {}", request);
        
        // 确保请求有ID
        if (request.getRequestId() == null) {
            request.setRequestId(UUID.randomUUID().toString());
        }
        
        // 确保有模型名称
        if (request.getModel() == null || request.getModel().isEmpty()) {
            request.setModel("qwen");
        }

        ChatResponse reply = chatService.syncReply(request);

        ChatResponse response = new ChatResponse();
        response.setContent(reply.getContent());
        response.setUserId(request.getUserId());
        response.setRequestId(request.getRequestId());
        response.setTimestamp(System.currentTimeMillis());
        
        log.info("测试聊天响应: requestId={}, contentLength={}", 
                response.getRequestId(), reply.getContent().length());
        
        return response;
    }

    @Data
    public static class SendPostParam {
        String content;
        String title;
        String tag;
    }

    /**
     * 打开冰箱门
     */
    @PostMapping("/opendoor")
    public String opendoor() {
        return "打开冰箱门成功";
    }

    /**
     * 放入大象
     */
    @PostMapping("/put")
    public String put() {
        return "放入大象成功";
    }

    /**
     * 关冰箱
     */
    @PostMapping("/closedoor")
    public String closedoor() {
        return "关冰箱成功";
    }
} 