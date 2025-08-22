package com.qin.catcat.unite.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * SSE消息转换器配置
 * 专门处理SSE响应的编码问题，确保中文字符正确显示
 */
@Configuration
@Slf4j
public class SseMessageConverterConfig implements BeanPostProcessor {
    
    /**
     * 专用于SSE的字符串消息转换器
     */
    @Bean
    @Primary
    public StringHttpMessageConverter sseStringHttpMessageConverter() {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        
        // 设置支持的媒体类型，重点是text/event-stream
        MediaType textEventStream = new MediaType("text", "event-stream", StandardCharsets.UTF_8);
        converter.setSupportedMediaTypes(Arrays.asList(
            textEventStream,
            new MediaType("application", "json", StandardCharsets.UTF_8),
            new MediaType("text", "plain", StandardCharsets.UTF_8)
        ));
        
        log.info("创建SSE专用StringHttpMessageConverter，支持媒体类型: {}", converter.getSupportedMediaTypes());
        return converter;
    }
    
    /**
     * 使用BeanPostProcessor处理RequestMappingHandlerAdapter，避免循环依赖
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RequestMappingHandlerAdapter) {
            RequestMappingHandlerAdapter adapter = (RequestMappingHandlerAdapter) bean;
            
            // 设置所有StringHttpMessageConverter使用UTF-8编码
            adapter.getMessageConverters().stream()
                .filter(converter -> converter instanceof StringHttpMessageConverter)
                .map(converter -> (StringHttpMessageConverter) converter)
                .forEach(converter -> {
                    converter.setDefaultCharset(StandardCharsets.UTF_8);
                    log.info("设置现有StringHttpMessageConverter默认字符集为UTF-8: {}", converter);
                });
            
            log.info("SSE消息转换器配置完成");
        }
        return bean;
    }
} 