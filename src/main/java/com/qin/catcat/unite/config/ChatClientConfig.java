//package com.qin.catcat.unite.config;
//
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.model.ChatModel;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//
//import lombok.extern.slf4j.Slf4j;
//
///**
// * 聊天客户端配置
// * 为确保ChatClient正常初始化
// */
//@Configuration
//@Slf4j
//public class ChatClientConfig {
//
//    /**
//     * 创建ChatClient，基于ChatModel
//     */
//    @Bean
//    @Primary
//    @ConditionalOnMissingBean(ChatClient.class)
//    @ConditionalOnProperty(prefix = "spring.ai.dashscope", name = "api-key")
//    public ChatClient chatClient(ChatModel chatModel) {
//        log.info("创建ChatClient，使用ChatModel: {}", chatModel);
//
//        return ChatClient.builder(chatModel)
//                .defaultSystem("你是一个友好的AI助手，能够提供有用的信息和帮助。")
//                .build();
//    }
//}