package com.qin.catcat.unite.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.qin.catcat.unite.common.interceptor.JwtInterceptor;

import lombok.extern.slf4j.Slf4j;
 
 
@Configuration
@Slf4j
public class WebMvcRegistrationsConfig extends WebMvcConfigurationSupport {
    /**
     * 静态资源配置（使用swagger knife4j 配置，不然404）
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("WebMvcRegistrationsConfig >>> addResourceHandlers");
            registry.addResourceHandler("doc.html")//放行？
                    .addResourceLocations("classpath:/META-INF/resources/");
            registry.addResourceHandler("swagger-ui.html")
                    .addResourceLocations("classpath:/META-INF/resources/");
            registry.addResourceHandler("/webjars/**")
                    .addResourceLocations("classpath:/META-INF/resources/webjars/");
        //     registry.addResourceHandler("/upload.html")
        //             .addResourceLocations("classpath:/META-INF/resources/");
    }
}