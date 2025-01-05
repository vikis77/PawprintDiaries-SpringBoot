package com.qin.catcat.unite.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 布隆过滤器配置
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-14 23:13
 */
@Configuration
@Slf4j
public class RBloomFilterConfig {

    @Autowired 
    private RedissonClient redissonClient;

    @Value("${bloom.filter.cat-like.capacity:10000}")
    private long capacity;

    @Value("${bloom.filter.cat-like.error-rate:0.01}")
    private double errorRate;

    @Value("${bloom.filter.cat-like.name:cat:like:bloom}")
    private String filterName;

    @Value("${bloom.filter.cat-like.reset-cron:59 59 23 * * ?}")
    private String resetCron;

    /**
     * 初始化小猫点赞布隆过滤器
     */
    @Bean
    public RBloomFilter<String> likeBloomFilter() {
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(filterName);
        bloomFilter.tryInit(capacity, errorRate);
        log.info("布隆过滤器初始化完成 - name: {}, capacity: {}, errorRate: {}", filterName, capacity, errorRate);
        return bloomFilter;
    }

    /**
     * 定时重置小猫点赞布隆过滤器
     */
    @Scheduled(cron = "${bloom.filter.cat-like.reset-cron:59 59 23 * * ?}")
    public void resetBloomFilter() {
        try {
            RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(filterName);
            bloomFilter.delete();
            bloomFilter.tryInit(capacity, errorRate);
            log.info("布隆过滤器已重置 - name: {}", filterName);
        } catch (Exception e) {
            log.error("重置布隆过滤器时发生错误: ", e);
        }
    }
}
