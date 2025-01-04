// package com.qin.catcat.unite.config;

// import com.google.common.util.concurrent.RateLimiter;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// /**
//  * 接口限流配置
//  * 使用Guava的RateLimiter实现令牌桶算法
//  */
// @Configuration
// public class RateLimiterConfig {
    
//     @Bean
//     public RateLimiter globalRateLimiter() {
//         // 每秒生成100个令牌
//         return RateLimiter.create(100.0);
//     }
    
//     @Bean(name = "uploadRateLimiter")
//     public RateLimiter uploadRateLimiter() {
//         // 上传接口每秒限制10个请求
//         return RateLimiter.create(10.0);
//     }
    
//     @Bean(name = "predictionRateLimiter")
//     public RateLimiter predictionRateLimiter() {
//         // 预测接口每秒限制5个请求
//         return RateLimiter.create(5.0);
//     }
// } 