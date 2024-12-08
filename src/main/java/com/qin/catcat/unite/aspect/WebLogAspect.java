package com.qin.catcat.unite.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class WebLogAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("execution(* com.qin.catcat.unite.controller..*.*(..))")
    public void webLog() {}

    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取当前请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        
        // 记录请求信息
        log.info("========================================== Start ==========================================");
        log.info("URL            : {}", request.getRequestURL().toString());
        log.info("HTTP Method    : {}", request.getMethod());
        log.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        log.info("IP            : {}", getIpAddress(request));
        log.info("Request Args   : {}", getRequestArgs(joinPoint.getArgs()));
        
        // 执行目标方法
        Object result = joinPoint.proceed();
        
        // 记录响应信息
        log.info("Response       : {}", result instanceof byte[] ? "byte array" : objectMapper.writeValueAsString(result));
        log.info("Time-Consuming : {} ms", System.currentTimeMillis() - startTime);
        log.info("=========================================== End ===========================================");
        
        return result;
    }
    
    /**
     * 获取请求参数
     */
    private String getRequestArgs(Object[] args) {
        try {
            return Arrays.stream(args)
                .map(arg -> {
                    if (arg instanceof MultipartFile) {
                        MultipartFile file = (MultipartFile) arg;
                        return "MultipartFile: " + file.getOriginalFilename();
                    }
                    try {
                        return objectMapper.writeValueAsString(arg);
                    } catch (Exception e) {
                        return arg.toString();
                    }
                })
                .collect(Collectors.joining(", "));
        } catch (Exception e) {
            return "Unable to parse request args";
        }
    }
    
    /**
     * 获取真实IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
} 