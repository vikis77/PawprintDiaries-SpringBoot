package com.qin.catcat.unite.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig{

  /**
   * 创建Redis模板，用于操作Redis数据库
   * @param connectionFactory Redis连接工厂
   * @return Redis模板
   */
  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    // 设置连接工厂
    template.setConnectionFactory(connectionFactory);

    // 使用StringRedisSerializer来序列化和反序列化redis的key值
    template.setKeySerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());

    // 使用GenericJackson2JsonRedisSerializer来序列化和反序列化redis的value值
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

    template.afterPropertiesSet();
    return template;
  }

  @Bean
  public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
      RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
              .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

      // 定义一个特定缓存的配置
      Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
      cacheConfigurations.put("allCats", defaultCacheConfig.entryTtl(Duration.ofMinutes(1))); // 为 allCats 设置过期时间为 30 分钟

      return RedisCacheManager.builder(connectionFactory)
              .cacheDefaults(defaultCacheConfig.entryTtl(Duration.ofMinutes(60))) // 默认过期时间 60 分钟
              .withInitialCacheConfigurations(cacheConfigurations)
              .build();
    }
}
