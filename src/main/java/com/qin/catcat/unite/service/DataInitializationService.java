package com.qin.catcat.unite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.redisson.api.RedissonClient;
import org.redisson.api.RBloomFilter;
import org.springframework.scheduling.annotation.Scheduled;
import java.util.List;
import org.springframework.context.annotation.Bean;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/* 
 * 初始化数据，在应用启动时
 */
@Service
@Slf4j
public class DataInitializationService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate; // Redis 模板
    @Autowired CatService catService;
    @Autowired PostService postService;

    // 提前写入缓存，做数据预热
    @EventListener(ApplicationReadyEvent.class) // 监听应用启动事件
    public void init() {
        // 查询全部猫猫数据，提前写入缓存
        // catService.CatList();
        // 查询前十条帖子数据，提前写入缓存
        postService.getPostBySendtime(1, 10);
        postService.getPostByLikecount(1, 10);
    }

    
}
