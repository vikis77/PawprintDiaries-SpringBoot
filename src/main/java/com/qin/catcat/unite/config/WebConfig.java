package com.qin.catcat.unite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Web配置类，处理HTTP请求和响应的编码
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ObjectMapper objectMapper;

    public WebConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 配置内容协商，确保SSE响应使用UTF-8编码
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
            .defaultContentType(MediaType.APPLICATION_JSON)
            .mediaType("json", MediaType.APPLICATION_JSON)
            .mediaType("stream", new MediaType("text", "event-stream", StandardCharsets.UTF_8));
    }

    /**
     * 配置字符串消息转换器，使用UTF-8编码
     */
    @Bean
    public StringHttpMessageConverter stringHttpMessageConverter() {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        converter.setWriteAcceptCharset(false);  // 避免添加'accept-charset'参数
        
        // 设置支持的媒体类型，包括SSE
        List<MediaType> mediaTypes = Arrays.asList(
            new MediaType("text", "plain", StandardCharsets.UTF_8),
            new MediaType("text", "html", StandardCharsets.UTF_8),
            new MediaType("application", "json", StandardCharsets.UTF_8),
            new MediaType("text", "event-stream", StandardCharsets.UTF_8)
        );
        converter.setSupportedMediaTypes(mediaTypes);
        
        return converter;
    }
    
    /**
     * 配置JSON消息转换器，使用UTF-8编码
     */
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
        converter.setSupportedMediaTypes(Arrays.asList(
            MediaType.APPLICATION_JSON,
            MediaType.TEXT_PLAIN,
            new MediaType("text", "event-stream", StandardCharsets.UTF_8)
        ));
        converter.setDefaultCharset(StandardCharsets.UTF_8);
        return converter;
    }
}
