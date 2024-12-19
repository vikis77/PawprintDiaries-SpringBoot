package com.qin.catcat.unite.config;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Description 缓存配置.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-12 23:29
 */
// @Configuration
// public class CacheConfig {
    
//     @Bean
//     public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
//         RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
//                 .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
//                 .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
//                 .entryTtl(Duration.ofHours(1)); // 设置缓存过期时间
                
//         return RedisCacheManager.builder(redisConnectionFactory)
//                 .cacheDefaults(config)
//                 .build();
//     }
// }