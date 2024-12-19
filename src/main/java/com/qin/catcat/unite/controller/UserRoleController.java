package com.qin.catcat.unite.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.security.HasPermission;
import com.qin.catcat.unite.service.UserRoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "用户角色关联接口")
@RestController
@RequestMapping("/api/user-role")
public class UserRoleController {
    
    @Autowired
    private UserRoleService userRoleService;
    
    @Operation(summary = "为用户分配角色")
    @HasPermission("system:user:role:assign")
    @PostMapping("/{userId}/roles")
    public Result<Void> assignRoles(
            @PathVariable Integer userId,
            @RequestBody List<Integer> roleIds) {
        if (userRoleService.assignRoles(userId, roleIds)) {
            return Result.success();
        }
        return Result.fail("分配角色失败");
    }
    
    @Operation(summary = "移除用户的角色")
    @HasPermission("system:user:role:remove")
    @DeleteMapping("/{userId}/role/{roleId}")
    public Result<Void> removeRole(
            @PathVariable Integer userId,
            @PathVariable Integer roleId) {
        if (userRoleService.removeRole(userId, roleId)) {
            return Result.success();
        }
        return Result.fail("移除角色失败");
    }
    
    @Operation(summary = "获取用户的角色ID列表")
    @HasPermission("system:user:role:view")
    @GetMapping("/{userId}/roles")
    public Result<List<Integer>> getRoleIdsByUserId(@PathVariable Integer userId) {
        return Result.success(userRoleService.getRoleIdsByUserId(userId));
    }
    
    @Operation(summary = "检查用户是否有指定角色")
    @HasPermission("system:user:role:view")
    @GetMapping("/{userId}/has-role/{roleId}")
    public Result<Boolean> hasRole(
            @PathVariable Integer userId,
            @PathVariable Integer roleId) {
        return Result.success(userRoleService.hasRole(userId, roleId));
    }
} 