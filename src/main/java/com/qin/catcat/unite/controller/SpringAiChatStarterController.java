package com.qin.catcat.unite.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.randb.springaichatstarter.core.ChatModelFactory;
import com.randb.springaichatstarter.core.ChatService;
import com.randb.springaichatstarter.dto.ChatRequest;
import com.randb.springaichatstarter.dto.ChatResponse;
import com.randb.springaichatstarter.mq.ChatMessageService;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.springframework.context.ApplicationContext;

@RestController
@RequestMapping("/api/chat")
@Slf4j
public class SpringAiChatStarterController {

    private final ChatModelFactory chatModelFactory;
    private final ApplicationContext applicationContext;
    private final ChatMessageService chatMessageService;

    @Autowired
    public SpringAiChatStarterController(ChatModelFactory chatModelFactory, ApplicationContext applicationContext, ChatMessageService chatMessageService) {
        this.chatModelFactory = chatModelFactory;
        this.applicationContext = applicationContext;
        this.chatMessageService = chatMessageService;
        log.info("ChatModelFactory注入成功");
        
        // 检查Spring容器中是否有ChatClient
        log.info("检查Spring容器中是否有ChatClient:");
        try {
            String[] chatClientBeans = applicationContext.getBeanNamesForType(org.springframework.ai.chat.client.ChatClient.class);
            log.info("找到 ChatClient 类型的Bean数量: {}", chatClientBeans.length);
            for (String beanName : chatClientBeans) {
                log.info("ChatClient Bean名称: {}, 类型: {}", beanName, 
                    applicationContext.getBean(beanName).getClass().getName());
            }
        } catch (Exception e) {
            log.error("获取ChatClient时出错", e);
        }
        
        // 检查是否有com.randb.springaichatstarter.core.ChatService类型的bean
        try {
            String[] chatServiceBeans = applicationContext.getBeanNamesForType(com.randb.springaichatstarter.core.ChatService.class);
            log.info("找到 ChatService 类型的Bean数量: {}", chatServiceBeans.length);
            for (String beanName : chatServiceBeans) {
                log.info("ChatService Bean名称: {}, 类型: {}", beanName, 
                    applicationContext.getBean(beanName).getClass().getName());
            }
        } catch (Exception e) {
            log.error("获取ChatService时出错", e);
        }
    }
    
    /**
     * SSE流式接口 - 直接返回文本（模拟starter中的实现）
     */
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> sse(@RequestParam String userId, 
                            @RequestParam String prompt, 
                            @RequestParam(defaultValue = "qwen") String model,
                            @RequestParam(required = false) String requestId) {
        ChatRequest req = new ChatRequest();
        req.setUserId(userId);
        req.setPrompt(prompt);
        req.setModel(model);
        req.setRequestId(requestId != null ? requestId : UUID.randomUUID().toString());
        
        log.info("SSE2请求接收: userId={}, prompt={}, model={}", userId, prompt, model);
        
        // 根据model参数动态选择聊天服务实现
        ChatService chatService = chatModelFactory.get(model);
        
        // 直接返回Flux<String>，这是starter中的实现方式
        return chatService.streamReply(req);
    }

    /**
     * 同步问答接口
     */
    @PostMapping("/sync")
    public ChatResponse sync(@RequestBody ChatRequest request) {
        if (request.getRequestId() == null) {
            request.setRequestId(UUID.randomUUID().toString());
        }
        
        log.info("同步请求接收: userId={}, prompt={}, model={}", request.getUserId(), request.getPrompt(), request.getModel());
        
        ChatService chatService = chatModelFactory.get(request.getModel());
        ChatResponse reply = chatService.syncReply(request);
        
        ChatResponse response = new ChatResponse();
        response.setContent(reply.getContent());
        response.setUserId(request.getUserId());
        response.setRequestId(request.getRequestId());
        
        log.info("同步响应发送: requestId={}, contentLength={}", 
                response.getRequestId(), reply.getContent().length());
        
        return response;
    }

    /**
     * MQ异步问答接口
     * 发送聊天请求到队列
     * @param request 聊天请求
     * @return 请求ID，可用于跟踪请求
     */
    @PostMapping("/mq/async")
    public String mq(@RequestBody ChatRequest request) {
        return chatMessageService.sendChatRequest(request);
    }
    
    /**
     * MQ同步问答接口
     * 发送聊天请求到队列并等待响应
     * @param request 聊天请求
     * @return 聊天响应
     */
    // @PostMapping("/mq/sync")
    // public ResponseEntity<ChatResponse> mqSync(@RequestBody ChatRequest request) {
    //     if (request.getRequestId() == null) {
    //         request.setRequestId(UUID.randomUUID().toString());
    //     }
        
    //     log.info("MQ同步请求接收: userId={}, prompt={}, model={}", request.getUserId(), request.getPrompt(), request.getModel());
        
    //     ChatResponse response = chatMessageService.sendAndWaitForResponse(request, 10000); // 等待10秒
        
    //     if (response != null) {
    //         log.info("MQ同步响应发送: requestId={}, contentLength={}", 
    //                 response.getRequestId(), response.getContent().length());
    //         return ResponseEntity.ok(response);
    //     } else {
    //         log.warn("MQ同步响应超时: requestId={}", request.getRequestId());
    //         return ResponseEntity.status(408).body(null); // 408 Request Timeout
    //     }
    // }
}