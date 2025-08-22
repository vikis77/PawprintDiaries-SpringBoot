package com.qin.catcat.unite.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * HTTP消息转换器配置
 * 专门处理SSE响应的编码问题
 */
@Configuration
public class HttpMessageConverterConfig implements WebMvcConfigurer {
    
    /**
     * 配置全局字符编码过滤器
     */
    @Bean
    public FilterRegistrationBean<CharacterEncodingFilter> characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true); // 强制使用指定的编码
        
        FilterRegistrationBean<CharacterEncodingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/*"); // 应用到所有URL
        registrationBean.setOrder(0); // 最高优先级
        
        return registrationBean;
    }
    
    /**
     * 配置消息转换器
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建专门用于SSE的StringHttpMessageConverter
        StringHttpMessageConverter sseConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        sseConverter.setSupportedMediaTypes(Arrays.asList(
            new MediaType("text", "event-stream", StandardCharsets.UTF_8)
        ));
        
        // 将SSE转换器添加到最前面，确保它有最高优先级处理SSE响应
        converters.add(0, sseConverter);
    }
} 