package com.qin.catcat.unite.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.param.UpdateRoleParam;
import com.qin.catcat.unite.popo.entity.Role;
import com.qin.catcat.unite.popo.vo.RoleListVO;
import com.qin.catcat.unite.security.HasPermission;
import com.qin.catcat.unite.service.RoleService;
import com.qin.catcat.unite.service.RolePermissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "角色管理接口")
@RestController
@RequestMapping("/api/role")
public class RoleController {
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private RolePermissionService rolePermissionService;
    
    @Operation(summary = "创建角色")
    @HasPermission("system:role:add")
    @PostMapping
    public Result<Void> createRole(@RequestBody Role role) {
        if (roleService.createRole(role)) {
            return Result.success();
        }
        return Result.fail("创建角色失败");
    }
    
    @Operation(summary = "更新角色")
    @HasPermission("system:role:edit")
    @PostMapping("/update")
    public Result<Void> updateRole(@RequestBody UpdateRoleParam param) {
        if (roleService.updateRole(param)) {
            return Result.success();
        }
        return Result.fail("更新角色失败");
    }
    
    @Operation(summary = "删除角色")
    @HasPermission("system:role:delete")
    @DeleteMapping("/{roleId}")
    public Result<Void> deleteRole(@PathVariable Long roleId) {
        if (roleService.deleteRole(roleId)) {
            return Result.success();
        }
        return Result.fail("删除角色失败");
    }
    
    @Operation(summary = "分页获取角色列表（目前只支持获取管理员）")
    @HasPermission("system:role:view")
    @GetMapping("/list")
    public Result<List<RoleListVO>> listRoles(@RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(roleService.list(page, pageSize));
    }
    
    @Operation(summary = "获取用户的角色列表")
    @HasPermission("system:role:view")
    @GetMapping("/user/{userId}")
    public Result<List<Role>> getRolesByUserId(@PathVariable Integer userId) {
        return Result.success(roleService.getRolesByUserId(userId));
    }
    
    @Operation(summary = "为角色分配权限")
    @HasPermission("system:role:permission:assign")
    @PostMapping("/{roleId}/permissions")
    public Result<Void> assignPermissions(
            @PathVariable Long roleId,
            @RequestBody List<Long> permissionIds) {
        if (rolePermissionService.assignPermissions(roleId, permissionIds)) {
            return Result.success();
        }
        return Result.fail("分配权限失败");
    }
    
    @Operation(summary = "获取角色的权限ID列表")
    @HasPermission("system:role:permission:view")
    @GetMapping("/{roleId}/permissions")
    public Result<List<Long>> getPermissionsByRoleId(@PathVariable Long roleId) {
        return Result.success(rolePermissionService.getPermissionIdsByRoleId(roleId));
    }
    
    @Operation(summary = "检查角色编码是否存在")
    @HasPermission("system:role:view")
    @GetMapping("/check/{roleCode}")
    public Result<Boolean> checkRoleCodeExists(@PathVariable String roleCode) {
        return Result.success(roleService.checkRoleCodeExists(roleCode));
    }

    @Operation(summary = "搜索用户及其角色（目前只支持搜索用户ID）")
    @HasPermission("system:role:view")
    @GetMapping("/search")
    public Result<List<RoleListVO>> searchUsersAndRoles(@RequestParam String keyword) {
        return Result.success(roleService.searchUsersAndRoles(keyword));
    }
}