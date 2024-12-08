package com.qin.catcat.unite.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qin.catcat.unite.popo.entity.Role;

/**
 * 角色服务接口
 */
public interface RoleService extends IService<Role> {
    
    /**
     * 创建角色
     * @param role 角色信息
     * @return 是否创建成功
     */
    boolean createRole(Role role);
    
    /**
     * 更新角色
     * @param role 角色信息
     * @return 是否更新成功
     */
    boolean updateRole(Role role);
    
    /**
     * 删除角色
     * @param roleId 角色ID
     * @return 是否删除成功
     */
    boolean deleteRole(Long roleId);
    
    /**
     * 获取用户的所有角色
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> getRolesByUserId(Long userId);
    
    /**
     * 检查角色编码是否已存在
     * @param roleCode 角色编码
     * @return 是否存在
     */
    boolean checkRoleCodeExists(String roleCode);
} 