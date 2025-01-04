package com.qin.catcat.unite.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

/**
 * 性能监控配置
 * 集成Micrometer和Prometheus实现系统监控
 * @author qin
 * @date 2025-01-03 22:24
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class MonitorConfig {
    
    @Bean
    public MeterRegistry meterRegistry() {
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        
        // 初始化监控指标
        registry.timer("api.response.time");
        
        // 系统内存使用监控
        registry.gauge("jvm.memory.used", Runtime.getRuntime(), runtime -> 
            runtime.totalMemory() - runtime.freeMemory());
        
        // 接口调用次数监控
        registry.counter("api.calls.total");
        
        // 错误率监控
        registry.counter("api.errors.total");
        
        // 并发用户数监控
        registry.gauge("users.concurrent", 0.0);
        
        return registry;
    }
} 