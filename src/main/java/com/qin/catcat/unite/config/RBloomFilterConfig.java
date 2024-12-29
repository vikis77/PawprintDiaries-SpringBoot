package com.qin.catcat.unite.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 布隆过滤器配置.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-14 23:13
 */
@Configuration
@Slf4j
public class RBloomFilterConfig {

    @Autowired RedissonClient redissonClient;

    // 初始化小猫点赞布隆过滤器
    @Bean
    public RBloomFilter<String> likeBloomFilter() {
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("cat:like:bloom");
        bloomFilter.tryInit(10000L, 0.01);
        return bloomFilter;
    }

    // 每天晚上23:59:59执行重置小猫点赞布隆过滤器
    @Scheduled(cron = "59 59 23 * * ?")
    public void resetBloomFilter() {
        try {
            RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("cat:like:bloom");
            bloomFilter.delete();
            bloomFilter.tryInit(10000L, 0.01);
            log.info("布隆过滤器已重置");
        } catch (Exception e) {
            log.error("重置布隆过滤器时发生错误: ", e);
        }
    }
}
