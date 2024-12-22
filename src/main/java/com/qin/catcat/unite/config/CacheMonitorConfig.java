package com.qin.catcat.unite.config;

import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;

/**
 * 缓存监控配置类 - 用于配置 Micrometer 监控指标的通用标签
 */
@Configuration
public class CacheMonitorConfig {

    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        // 为所有的监控指标添加通用标签
        return registry -> registry.config()
                .commonTags(Tags.of(
                    Tag.of("application", "catcat"),  // 应用名称标签
                    Tag.of("env", "prod")));         // 环境标签
    }
} 