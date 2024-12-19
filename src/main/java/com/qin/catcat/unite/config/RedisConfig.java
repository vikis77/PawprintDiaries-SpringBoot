package com.qin.catcat.unite.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.beans.factory.annotation.Qualifier;

@Configuration
public class RedisConfig {

  @Value("${spring.redis.host:localhost}")
  private String host;
  
  @Value("${spring.redis.port:6379}")
  private String port;
  
  @Value("${spring.redis.password:}")
  private String password;

  @Bean
  public RedissonClient redissonClient() {
    Config config = new Config();
    String address = String.format("redis://%s:%s", host, port);
    config.useSingleServer()
        .setAddress(address)
        .setPassword(password.isEmpty() ? null : password);
    return Redisson.create(config);
  }

  // 解决LocalDateTime类型在JSON序列化时的问题
  @Bean("redisObjectMapper")
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  // 配置Redis模板 
  //@Qualifier注解用于指定bean的名称
  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory,
      @Qualifier("redisObjectMapper") ObjectMapper objectMapper) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // 使用StringRedisSerializer来序列化和反序列化redis的key值
    template.setKeySerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());

    // 使用配置好的ObjectMapper创建GenericJackson2JsonRedisSerializer
    GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
    template.setValueSerializer(jsonRedisSerializer);
    template.setHashValueSerializer(jsonRedisSerializer);

    template.afterPropertiesSet();
    return template;
  }

  // 配置Redis缓存管理器
  @Bean
  public CacheManager cacheManager(RedisConnectionFactory connectionFactory,
      @Qualifier("redisObjectMapper") ObjectMapper objectMapper) {
    RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
        .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));

    // 定义一个特定缓存的配置
    Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
    cacheConfigurations.put("allCats", defaultCacheConfig.entryTtl(Duration.ofMinutes(1))); // 为 allCats 设置过期时间为 10 分钟
    cacheConfigurations.put("post_like", defaultCacheConfig.entryTtl(Duration.ofMinutes(1)));
    cacheConfigurations.put("postForSendtime", defaultCacheConfig.entryTtl(Duration.ofMinutes(1)));
    cacheConfigurations.put("postForLikecount", defaultCacheConfig.entryTtl(Duration.ofMinutes(1)));

    return RedisCacheManager.builder(connectionFactory)
        .cacheDefaults(defaultCacheConfig.entryTtl(Duration.ofMinutes(60))) // 默认过期时间 60 分钟
        .withInitialCacheConfigurations(cacheConfigurations)
        .build();
  }
}
