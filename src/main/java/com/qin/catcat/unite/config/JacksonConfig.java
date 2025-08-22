package com.qin.catcat.unite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import java.time.format.DateTimeFormatter;

/**
 * @Description 全局的 Jackson 配置.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-13 19:35
 */
@Configuration
public class JacksonConfig {
    
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    
    // Spring MVC 的 JSON 序列化配置
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // 配置 LocalDateTime 序列化和反序列化格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        javaTimeModule.addSerializer(java.time.LocalDateTime.class,
            new LocalDateTimeSerializer(formatter));
        javaTimeModule.addDeserializer(java.time.LocalDateTime.class,
            new LocalDateTimeDeserializer(formatter));

        objectMapper.registerModule(javaTimeModule);
        // 禁用将日期写为时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
} 