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

    // // 这里是以yml为准的
    // @Value("${bloom.filter.cat-like.reset-cron:59 59 23 * * ?}")
    // private String resetCron;

    /**
     * 初始化小猫点赞布隆过滤器
     */
    @Bean
    public RBloomFilter<String> likeBloomFilter() {
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(filterName);
        try {
            // 如果布隆过滤器不存在或配置不一致，则重新初始化
            if (!bloomFilter.isExists()) {
                boolean initialized = bloomFilter.tryInit(capacity, errorRate);
                if (initialized) {
                    log.info("布隆过滤器初始化完成 - name: {}, capacity: {}, errorRate: {}", filterName, capacity, errorRate);
                } else {
                    log.error("布隆过滤器初始化失败 - name: {}", filterName);
                    // 如果初始化失败，强制删除后重试一次
                    bloomFilter.delete();
                    initialized = bloomFilter.tryInit(capacity, errorRate);
                    if (initialized) {
                        log.info("布隆过滤器重试初始化成功 - name: {}", filterName);
                    } else {
                        log.error("布隆过滤器重试初始化仍然失败 - name: {}", filterName);
                        throw new RuntimeException("布隆过滤器初始化失败");
                    }
                }
            } else {
                // 检查现有布隆过滤器的配置是否一致
                if (!checkBloomFilterConfig(bloomFilter)) {
                    log.info("布隆过滤器配置不一致，重新初始化 - name: {}", filterName);
                    bloomFilter.delete();
                    boolean initialized = bloomFilter.tryInit(capacity, errorRate);
                    if (initialized) {
                        log.info("布隆过滤器重新初始化成功 - name: {}", filterName);
                    } else {
                        log.error("布隆过滤器重新初始化失败 - name: {}", filterName);
                        throw new RuntimeException("布隆过滤器重新初始化失败");
                    }
                } else {
                    log.info("布隆过滤器已存在且配置一致 - name: {}", filterName);
                }
            }
        } catch (Exception e) {
            log.error("布隆过滤器初始化过程中发生错误: ", e);
            throw new RuntimeException("布隆过滤器初始化失败", e);
        }
        return bloomFilter;
    }

    /**
     * 检查布隆过滤器配置是否一致
     */
    private boolean checkBloomFilterConfig(RBloomFilter<String> bloomFilter) {
        try {
            // 获取布隆过滤器的当前配置
            long currentSize = bloomFilter.getSize();
            double currentErrorRate = bloomFilter.getFalseProbability();
            
            // 检查配置是否一致
            boolean isConfigValid = currentSize == capacity && Math.abs(currentErrorRate - errorRate) < 0.0001;
            if (!isConfigValid) {
                log.info("布隆过滤器配置不一致 - 期望容量: {}, 当前容量: {}, 期望错误率: {}, 当前错误率: {}", 
                    capacity, currentSize, errorRate, currentErrorRate);
            }
            return isConfigValid;
        } catch (Exception e) {
            log.error("检查布隆过滤器配置时发生错误: ", e);
            return false;
        }
    }

    /**
     * 定时重置小猫点赞布隆过滤器：24小时后重置
     */
    // @Scheduled(cron = "${bloom.filter.cat-like.reset-cron:2 * * * * ?}")
    @Scheduled(cron = "${bloom.filter.cat-like.reset-cron:59 59 23 * * ?}")
    public void resetBloomFilter() {
        try {
            RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(filterName);
            // 删除旧的布隆过滤器
            bloomFilter.delete();
            // 使用相同的配置重新初始化
            boolean initialized = bloomFilter.tryInit(capacity, errorRate);
            if (initialized) {
                log.info("布隆过滤器已重置 - name: {}, capacity: {}, errorRate: {}", filterName, capacity, errorRate);
            } else {
                log.error("布隆过滤器重置失败 - name: {}", filterName);
            }
        } catch (Exception e) {
            log.error("重置布隆过滤器时发生错误: ", e);
        }
    }
}
