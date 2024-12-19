package com.qin.catcat.unite.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qin.catcat.unite.popo.entity.Permission;

/**
 * 权限服务接口
 */
public interface PermissionService extends IService<Permission> {
    
    /**
     * 创建权限
     * @param permission 权限信息
     * @return 是否创建成功
     */
    boolean createPermission(Permission permission);
    
    /**
     * 更新权限
     * @param permission 权限信息
     * @return 是否更新成功
     */
    boolean updatePermission(Permission permission);
    
    /**
     * 删除权限
     * @param permissionId 权限ID
     * @return 是否删除成功
     */
    boolean deletePermission(Long permissionId);
    
    /**
     * 获取角色的所有权限
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> getPermissionsByRoleId(Integer roleId);
    
    /**
     * 获取用户的所有权限
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> getPermissionsByUserId(Integer userId);
    
    /**
     * 检查权限编码是否已存在
     * @param permissionCode 权限编码
     * @return 是否存在
     */
    boolean checkPermissionCodeExists(String permissionCode);
    
    /**
     * 获取权限树
     * @return 权限树列表
     */
    List<Permission> getPermissionTree();
    
    /**
     * 获取所有权限编码
     * @param code 权限编码
     * @return 权限编码列表
     */
    List<String> getAllPermissionsByCode(String code);
} 