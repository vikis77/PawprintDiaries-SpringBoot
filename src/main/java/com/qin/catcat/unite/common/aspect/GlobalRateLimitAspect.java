package com.qin.catcat.unite.common.aspect;

import com.qin.catcat.unite.common.enumclass.CatcatEnumClass;
import com.qin.catcat.unite.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Description 全局接口限流切面
 * 对所有Controller接口实现统一的限流控制
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-04
 */
@Aspect
@Component
@RequiredArgsConstructor
public class GlobalRateLimitAspect {

    // private final StringRedisTemplate redisTemplate;
    
    // // 定义切点：所有controller包下的所有方法
    // @Pointcut("execution(* com.qin.catcat.unite.controller..*.*(..))")
    // public void controllerMethods() {}
    
    // @Around("controllerMethods()")
    // public Object rateLimit(ProceedingJoinPoint point) throws Throwable {
    //     HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        
    //     // 获取请求的URI和IP作为限流key
    //     String uri = request.getRequestURI();
    //     String ip = request.getRemoteAddr();
    //     String key = "global:rate:limit:" + ip + ":" + uri;
        
    //     // 限流参数设置：1秒内最多3次请求
    //     int time = 1;
    //     TimeUnit timeUnit = TimeUnit.SECONDS;
    //     int maxCount = 1;
        
    //     // 获取当前计数
    //     String countString = redisTemplate.opsForValue().get(key);
    //     int currentCount = countString == null ? 0 : Integer.parseInt(countString);
        
    //     if (currentCount >= maxCount) {
    //         throw new BusinessException(CatcatEnumClass.StatusCode.REQUEST_TOO_FREQUENT.getCode(), CatcatEnumClass.StatusCode.REQUEST_TOO_FREQUENT.getMessage());
    //     }
        
    //     // 如果是第一次请求，设置过期时间
    //     if (currentCount == 0) {
    //         redisTemplate.opsForValue().increment(key);
    //         redisTemplate.expire(key, time, timeUnit);
    //     } else {
    //         redisTemplate.opsForValue().increment(key);
    //     }
        
    //     return point.proceed();
    // }
} 