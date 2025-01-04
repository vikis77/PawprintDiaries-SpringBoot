package com.qin.catcat.unite.common.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description Redis限流注解
 * 用于标记需要进行访问频率限制的方法
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-04 00:14
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisRateLimit {
    /**
     * 限流的key前缀
     */
    String prefix() default "rate:limit:";
    
    /**
     * 限流时间窗口，默认1秒
     */
    int time() default 1;
    
    /**
     * 时间单位，默认秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    
    /**
     * 在时间窗口内允许的最大请求数
     */
    int count() default 10;
    
    /**
     * 限流提示语
     */
    String message() default "请求太频繁，请稍后再试";
} 