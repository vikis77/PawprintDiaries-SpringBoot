package com.qin.catcat.unite.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qin.catcat.unite.popo.entity.RolePermission;

/**
 * 角色权限关联服务接口
 */
public interface RolePermissionService extends IService<RolePermission> {
    
    /**
     * 为角色分配权限
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 是否分配成功
     */
    boolean assignPermissions(Long roleId, List<Long> permissionIds);
    
    /**
     * 移除角色的权限
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 是否移除成功
     */
    boolean removePermission(Long roleId, Long permissionId);
    
    /**
     * 获取角色的权限ID列表
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getPermissionIdsByRoleId(Long roleId);
    
    /**
     * 检查角色是否有指定权限
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 是否有该权限
     */
    boolean hasPermission(Long roleId, Long permissionId);
} 