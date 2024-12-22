package com.qin.catcat.unite.config;

import java.time.Duration;
import java.util.Arrays;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * 一级本地缓存配置，使用Caffeine作为本地缓存
 * 
 * 一级缓存：Caffeine（本地内存）
 * 配置本地缓存（Caffeine）
 * 主要用于热点数据的快速访问
 * 访问速度更快
 * 使用 @Primary 注解标记为默认缓存管理器
 */
@Configuration
public class LocalCacheConfig {

    // 配置本地缓存管理器
    @Bean(name = "caffeineCacheManager")
    @Primary // 设置为默认缓存管理器
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(Arrays.asList(
            "hotPosts", // 热门帖子缓存
            "hotCats", // 热门猫猫缓存
            "allCats", // 所有猫猫缓存
            "post_like", // 帖子点赞缓存
            "postForSendtime", // 根据发布时间分页查询前十条帖子
            "postForLikecount" // 根据点赞数分页查询前十条帖子
        ));

        // 配置 Caffeine 缓存属性
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterAccess(Duration.ofMinutes(5))
                // 初始的缓存空间大小
                .initialCapacity(100)
                // 缓存的最大条数
                .maximumSize(1000));
        return cacheManager;
    }
}