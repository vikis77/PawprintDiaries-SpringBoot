package com.qin.catcat.unite.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qin.catcat.unite.mapper.RolePermissionMapper;
import com.qin.catcat.unite.popo.entity.RolePermission;
import com.qin.catcat.unite.service.RolePermissionService;

/**
 * 角色权限关联服务实现类
 */
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignPermissions(Long roleId, List<Long> permissionIds) {
        // 先删除角色原有的权限
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        remove(wrapper);
        
        // 批量保存新的权限关联
        List<RolePermission> rolePermissions = permissionIds.stream()
                .map(permissionId -> {
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setRoleId(roleId);
                    rolePermission.setPermissionId(permissionId);
                    return rolePermission;
                })
                .collect(Collectors.toList());
        
        return saveBatch(rolePermissions);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removePermission(Long roleId, Long permissionId) {
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId)
               .eq(RolePermission::getPermissionId, permissionId);
        return remove(wrapper);
    }
    
    @Override
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        return list(wrapper).stream()
                          .map(RolePermission::getPermissionId)
                          .collect(Collectors.toList());
    }
    
    @Override
    public boolean hasPermission(Long roleId, Long permissionId) {
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId)
               .eq(RolePermission::getPermissionId, permissionId);
        return count(wrapper) > 0;
    }
} 