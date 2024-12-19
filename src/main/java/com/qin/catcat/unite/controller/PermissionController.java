package com.qin.catcat.unite.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.popo.entity.Permission;
import com.qin.catcat.unite.security.HasPermission;
import com.qin.catcat.unite.service.PermissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "权限管理接口")
@RestController
@RequestMapping("/api/permission")
public class PermissionController {
    
    @Autowired
    private PermissionService permissionService;
    
    @Operation(summary = "创建权限")
    @HasPermission("system:permission:add")
    @PostMapping
    public Result<Void> createPermission(@RequestBody Permission permission) {
        if (permissionService.createPermission(permission)) {
            return Result.success();
        }
        return Result.fail("创建权限失败");
    }
    
    @Operation(summary = "更新权限")
    @HasPermission("system:permission:edit")
    @PutMapping
    public Result<Void> updatePermission(@RequestBody Permission permission) {
        if (permissionService.updatePermission(permission)) {
            return Result.success();
        }
        return Result.fail("更新权限失败");
    }
    
    @Operation(summary = "删除权限")
    @HasPermission("system:permission:delete")
    @DeleteMapping("/{permissionId}")
    public Result<Void> deletePermission(@PathVariable Long permissionId) {
        if (permissionService.deletePermission(permissionId)) {
            return Result.success();
        }
        return Result.fail("删除权限失败");
    }
    
    @Operation(summary = "获取权限树")
    @HasPermission("system:permission:view")
    @GetMapping("/tree")
    public Result<List<Permission>> getPermissionTree() {
        return Result.success(permissionService.getPermissionTree());
    }
    
    @Operation(summary = "获取角色的权限列表")
    @HasPermission("system:permission:view")
    @GetMapping("/role/{roleId}")
    public Result<List<Permission>> getPermissionsByRoleId(@PathVariable Integer roleId) {
        return Result.success(permissionService.getPermissionsByRoleId(roleId));
    }
    
    @Operation(summary = "获取用户的权限列表")
    @HasPermission("system:permission:view")
    @GetMapping("/user/{userId}")
    public Result<List<Permission>> getPermissionsByUserId(@PathVariable Integer userId) {
        return Result.success(permissionService.getPermissionsByUserId(userId));
    }
    
    @Operation(summary = "检查权限编码是否存在")
    @HasPermission("system:permission:view")
    @GetMapping("/check/{permissionCode}")
    public Result<Boolean> checkPermissionCodeExists(@PathVariable String permissionCode) {
        return Result.success(permissionService.checkPermissionCodeExists(permissionCode));
    }
} 