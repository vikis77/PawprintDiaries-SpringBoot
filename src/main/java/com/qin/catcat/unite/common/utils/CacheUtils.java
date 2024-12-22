package com.qin.catcat.unite.common.utils;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.extern.slf4j.Slf4j;

/**
 * 缓存工具类，实现多级缓存
 */
@Component
@Slf4j
public class CacheUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Caffeine本地缓存
    private final Cache<String, Object> localCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES) // 设置缓存过期时间
            .maximumSize(1000) // 设置缓存最大容量
            .build();

    /**
     * 多级缓存获取
     * @param key 缓存键
     * @param type 返回值类型
     * @param loader 加载数据的函数
     * @return 缓存值
     */
    @SuppressWarnings("unchecked")
    public <T> T getWithMultiLevel(String key, Class<T> type, CacheLoader<T> loader) {
        // 1. 先查本地缓存
        Object localValue = localCache.getIfPresent(key);
        if (localValue != null) {
            log.info("从本地缓存中获取数据, key: {}", key);
            return (T) localValue;
        }

        // 2. 查Redis缓存
        Object redisValue = redisTemplate.opsForValue().get(key);
        if (redisValue != null) {
            log.info("从Redis缓存中获取数据, key: {}", key);
            // 将Redis中的数据放入本地缓存
            localCache.put(key, redisValue);
            return (T) redisValue;
        }

        // 3. 查数据库
        try {
            log.info("从数据库中获取数据, key: {}", key);
            T value = loader.load();
            if (value != null) {
                // 放入Redis缓存
                redisTemplate.opsForValue().set(key, value, 30, TimeUnit.MINUTES);
                // 放入本地缓存
                localCache.put(key, value);
                return value;
            }
        } catch (Exception e) {
            log.error("加载缓存数据失败, key: {}", key, e);
        }

        return null;
    }

    /**
     * 更新缓存
     * @param key 缓存键
     * @param value 缓存值
     */
    public void put(String key, Object value) {
        // 更新Redis缓存
        redisTemplate.opsForValue().set(key, value, 30, TimeUnit.MINUTES);
        // 更新本地缓存
        localCache.put(key, value);
    }

    /**
     * 删除缓存
     * @param key 缓存键
     */
    public void remove(String key) {
        // 删除Redis缓存
        redisTemplate.delete(key);
        // 删除本地缓存
        localCache.invalidate(key);
    }

    /**
     * 缓存加载器接口
     */
    @FunctionalInterface
    public interface CacheLoader<T> {
        T load() throws Exception;
    }
} 