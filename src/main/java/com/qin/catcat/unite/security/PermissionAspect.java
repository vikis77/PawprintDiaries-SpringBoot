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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.security.web.context.SecurityContextRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashSet;

import com.qin.catcat.unite.popo.entity.Permission;
import com.qin.catcat.unite.service.PermissionService;
import com.qin.catcat.unite.common.constant.Constant;
import com.qin.catcat.unite.common.enumclass.CatcatEnumClass;
import com.qin.catcat.unite.common.utils.CacheUtils;
import com.qin.catcat.unite.exception.BusinessException;

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
    
    @Autowired private PermissionService permissionService;
    @Autowired private CacheUtils cacheUtils;
    
    @Around("@annotation(com.qin.catcat.unite.security.HasPermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("进入权限切面");
        log.info("当前处理切面线程：{}", Thread.currentThread().getName());
        
        // 获取当前请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException(CatcatEnumClass.StatusCode.UNAUTHORIZED.getCode(), "无法获取请求上下文");
        }

        // 尝试从SecurityContext中获取认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("权限切面中的认证信息：{}", authentication);
        log.info("认证信息类型：{}", authentication != null ? authentication.getClass().getName() : "null");
        log.info("SecurityContext哈希值：{}", SecurityContextHolder.getContext().hashCode());
        
        // 获取方法上的权限注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        HasPermission hasPermission = signature.getMethod().getAnnotation(HasPermission.class);
        String requiredPermission = hasPermission.value();
        
        // 权限编码列表
        List<String> permissionCodes = new ArrayList<>();
        
        // 如果是游客
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            // 从缓存中获取游客权限：查询游客全部角色"直接权限"，游客userId为0
            String cacheKey = Constant.PERMISSION_KEY_PREFIX + "anonymous";
            @SuppressWarnings("unchecked")
            List<Permission> permissions = (List<Permission>) cacheUtils.getWithMultiLevel(cacheKey, List.class, 
                () -> permissionService.getPermissionsByUserId(0));
            
            permissionCodes = permissions.stream()
                    .map(Permission::getPermissionCode)
                    .collect(Collectors.toList());
            log.info("当前请求为游客权限");
        } 
        // 普通用户、管理员、超级管理员
        else {
            // 获取当前用户权限
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            permissionCodes = loginUser.getPermissions();
            log.info("当前用户角色基类权限: {}", permissionCodes);
        }

        // 根据权限编码获取用户所有权限(包括子权限)
        Set<String> userPermissions = new HashSet<>();
        for (String permission : permissionCodes) {
            // 从缓存中获取子权限
            String subPermissionsCacheKey = Constant.SUB_PERMISSION_KEY_PREFIX + permission;
            @SuppressWarnings("unchecked")
            Set<String> subPermissions = (Set<String>) cacheUtils.getWithMultiLevel(subPermissionsCacheKey, HashSet.class,
                () -> new HashSet<>(permissionService.getAllPermissionsByCode(permission)));
                
            if (subPermissions != null) {
                userPermissions.addAll(subPermissions);
            }
        }
        
        // 打印用户权限
        log.info("用户所拥有的权限: {}", userPermissions);
        log.info("请求所需要权限: {}", requiredPermission);
        
        if (userPermissions.contains(requiredPermission)) {
            return joinPoint.proceed();
        }
            
        throw new BusinessException(CatcatEnumClass.StatusCode.UNAUTHORIZED.getCode(), CatcatEnumClass.StatusCode.UNAUTHORIZED.getMessage());
    }
} 