package com.qin.catcat.unite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import jakarta.annotation.PostConstruct;

/* 
 * 初始化数据，在应用启动时，提前写入缓存，做数据预热
 */
@Service
public class DataInitializationService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate; // Redis 模板

    @Autowired CatService catService;

    @Autowired PostService postService;

    // @PostConstruct // 使用 @PostConstruct 标注的方法会在服务器启动时运行
    @EventListener(ApplicationReadyEvent.class) // 监听应用启动事件
    public void init() {
        // 查询全部猫猫数据，提前写入缓存
        catService.list();

        // 查询前十条帖子数据，提前写入缓存
        postService.getPostBySendtime(1, 10);
        postService.getPostByLikecount(1, 10);
    }


}
