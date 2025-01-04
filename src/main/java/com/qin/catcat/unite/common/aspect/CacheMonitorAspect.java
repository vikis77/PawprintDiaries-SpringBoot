package com.qin.catcat.unite.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;

/**
 * 缓存监控切面
 */
@Aspect
@Component
@Slf4j
public class CacheMonitorAspect {

    // 注入MeterRegistry，作用：用于记录缓存访问的指标
    @Autowired
    private MeterRegistry meterRegistry;

    // 环绕通知，用于监控缓存访问
    @Around("@annotation(org.springframework.cache.annotation.Cacheable)")
    public Object monitorCacheables(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法名
        String methodName = joinPoint.getSignature().getName();
        // 开始计时
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            // 执行方法
            Object result = joinPoint.proceed();
            // 停止计时，并记录成功访问
            sample.stop(Timer.builder("cache.access")
                    .tag("method", methodName)
                    .tag("result", "success")
                    .register(meterRegistry));
            return result;
        } catch (Exception e) {
            // 停止计时，并记录错误访问
            sample.stop(Timer.builder("cache.access")
                    .tag("method", methodName)
                    .tag("result", "error")
                    .register(meterRegistry));
            throw e;
        }
    }
}