package com.qin.catcat.unite.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashSet;

import com.qin.catcat.unite.popo.entity.Permission;
import com.qin.catcat.unite.popo.entity.User;
import com.qin.catcat.unite.service.PermissionService;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 权限切面
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-04 16:01
 */
@Aspect
@Component
@Slf4j
public class PermissionAspect {
    
    @Autowired
    private PermissionService permissionService;
    
    @Around("@annotation(com.qin.catcat.unite.security.HasPermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法上的权限注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        HasPermission hasPermission = signature.getMethod().getAnnotation(HasPermission.class);
        String requiredPermission = hasPermission.value();
        
        // 获取当前用户的权限信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // 权限编码列表
        List<String> permissionCodes = new ArrayList<>();
        
        // 如果是游客
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            // 查询游客全部角色直接权限，游客userId为0
            List<Permission> permissions = permissionService.getPermissionsByUserId(0);
            permissionCodes = permissions.stream()
                    .map(Permission::getPermissionCode)
                    .collect(Collectors.toList());
        } 
        // 普通用户、管理员、超级管理员
        else {
            // 获取当前用户权限
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            permissionCodes = loginUser.getPermissions();
        }

        // 根据权限编码获取用户所有权限(包括子权限)
        Set<String> userPermissions = new HashSet<>();
        for (String permission : permissionCodes) {
            userPermissions.addAll(permissionService.getAllPermissionsByCode(permission));
        }
        // 打印用户权限
        log.info("用户所拥有的权限: {}", userPermissions);
        log.info("请求所需要权限: {}", requiredPermission);
        if (userPermissions.contains(requiredPermission)) {
            return joinPoint.proceed();
        }
            
        throw new RuntimeException("没有操作权限");
    }
} 