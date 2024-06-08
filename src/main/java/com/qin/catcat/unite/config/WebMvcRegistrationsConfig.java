package com.qin.catcat.unite.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
 
import java.util.ArrayList;
import java.util.List;
 
@Configuration
public class WebMvcRegistrationsConfig extends WebMvcConfigurationSupport {
 
 
    // @Resource
    // private LicenseInterceptor licenseInterceptor;
 
    // public WebMvcRegistrationsConfig(LicenseInterceptor licenseInterceptor) {
    //     this.licenseInterceptor = licenseInterceptor;
    // }
 
    // /**
    //  * 拦截器配置
    //  * @param registry
    //  */
    // @Override
    // public void addInterceptors(InterceptorRegistry registry) {
    //     // 无需拦截的接口集合
    //     List<String> ignorePath = new ArrayList<>();
    //     // swagger
    //     ignorePath.add("/swagger-resources/**");
    //     ignorePath.add("/doc.html");
    //     ignorePath.add("/v3/**");
    //     ignorePath.add("/webjars/**");
    //     ignorePath.add("/springdoc/**");
    //     ignorePath.add("/static/**");
    //     ignorePath.add("/templates/**");
    //     ignorePath.add("/error");
    //     ignorePath.add("/cipher/check");
    //     ignorePath.add("/manager/login");
    //     ignorePath.add("/swagger-ui.html");
    //     //先拦截认证，再拦截授权
    //     registry.addInterceptor(licenseInterceptor).addPathPatterns("/**").excludePathPatterns(ignorePath);
    // }
 
    /**
     * 静态资源配置（使用swagger knife4j 配置，不然404）
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html")//放行？
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}