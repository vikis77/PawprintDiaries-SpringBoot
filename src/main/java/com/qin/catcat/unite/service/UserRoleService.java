package com.qin.catcat.unite.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qin.catcat.unite.popo.entity.UserRole;

/**
 * 用户角色关联服务接口
 */
public interface UserRoleService extends IService<UserRole> {
    
    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 是否分配成功
     */
    boolean assignRoles(Long userId, List<Long> roleIds);
    
    /**
     * 移除用户的角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否移除成功
     */
    boolean removeRole(Long userId, Long roleId);
    
    /**
     * 获取用户的角色ID列表
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> getRoleIdsByUserId(Long userId);
    
    /**
     * 检查用户是否有指定角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否有该角色
     */
    boolean hasRole(Long userId, Long roleId);
} 