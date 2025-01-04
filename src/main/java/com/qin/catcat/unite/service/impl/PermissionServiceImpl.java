package com.qin.catcat.unite.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qin.catcat.unite.mapper.PermissionMapper;
import com.qin.catcat.unite.mapper.RolePermissionMapper;
import com.qin.catcat.unite.mapper.UserRoleMapper;
import com.qin.catcat.unite.popo.entity.Permission;
import com.qin.catcat.unite.popo.entity.RolePermission;
import com.qin.catcat.unite.popo.entity.UserRole;
import com.qin.catcat.unite.service.PermissionService;

/**
 * 权限服务实现类
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
    
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createPermission(Permission permission) {
        // 检查权限编码是否已存在
        if (checkPermissionCodeExists(permission.getPermissionCode())) {
            return false;
        }
        return save(permission);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePermission(Permission permission) {
        // 检查权限编码是否已存在（排除自身）
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPermissionCode, permission.getPermissionCode())
               .ne(Permission::getId, permission.getId());
        if (count(wrapper) > 0) {
            return false;
        }
        return updateById(permission);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePermission(Long permissionId) {
        // 删除权限与角色的关联关系
        LambdaQueryWrapper<RolePermission> rolePermWrapper = new LambdaQueryWrapper<>();
        rolePermWrapper.eq(RolePermission::getPermissionId, permissionId);
        rolePermissionMapper.delete(rolePermWrapper);
        
        // 删除权限
        return removeById(permissionId);
    }
    
    @Override
    public List<Permission> getPermissionsByRoleId(Integer roleId) {
        // 查询角色权限关联表
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(wrapper);
        
        // 获取权限ID列表
        List<Long> permissionIds = rolePermissions.stream()
                                               .map(RolePermission::getPermissionId)
                                               .collect(Collectors.toList());
        
        // 查询权限信息
        if (permissionIds.isEmpty()) {
            return List.of();
        }
        return listByIds(permissionIds);
    }
    
    /**
     * 获取用户的所有权限
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public List<Permission> getPermissionsByUserId(Integer userId) {
        // 如果是游客（userId = 0），返回游客基本权限
        // if (userId == 0) {
        //     LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        //     wrapper.eq(Permission::getPermissionCode, "system:user:login")
        //           .or()
        //           .eq(Permission::getPermissionCode, "system:user:register");
        //     return list(wrapper);
        // }

        // 查询用户角色关联表
        LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);
        
        // 获取角色ID列表
        List<Integer> roleIds = userRoles.stream()
                                    .map(UserRole::getRoleId)
                                    .collect(Collectors.toList());
        
        if (roleIds.isEmpty()) {
            return List.of();
        }
        
        // 查询角色权限关联表
        LambdaQueryWrapper<RolePermission> rolePermWrapper = new LambdaQueryWrapper<>();
        rolePermWrapper.in(RolePermission::getRoleId, roleIds);
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(rolePermWrapper);
        
        // 获取权限ID列表
        List<Long> permissionIds = rolePermissions.stream()
                                               .map(RolePermission::getPermissionId)
                                               .collect(Collectors.toList());
        
        if (permissionIds.isEmpty()) {
            return List.of();
        }
        
        // 查询权限信息
        return listByIds(permissionIds);
    }
    
    @Override
    public boolean checkPermissionCodeExists(String permissionCode) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPermissionCode, permissionCode);
        return count(wrapper) > 0;
    }
    
    @Override
    public List<Permission> getPermissionTree() {
        // 获取所有权限
        List<Permission> allPermissions = list();
        
        // 构建树形结构
        Map<Long, List<Permission>> parentIdMap = allPermissions.stream()
                .collect(Collectors.groupingBy(p -> p.getParentId() != null ? p.getParentId() : 0L));
        
        // 获取顶级权限
        List<Permission> rootPermissions = parentIdMap.getOrDefault(0L, new ArrayList<>());
        
        // 递归设置子权限
        rootPermissions.forEach(root -> setChildren(root, parentIdMap));
        
        return rootPermissions;
    }
    
    private void setChildren(Permission parent, Map<Long, List<Permission>> parentIdMap) {
        List<Permission> children = parentIdMap.get(parent.getId());
        if (children != null) {
            children.forEach(child -> setChildren(child, parentIdMap));
            // 这里需要在Permission类中添加children字段
            // parent.setChildren(children);
        }
    }
    
    /**
     * 获取权限及其所有子权限
     * @param code 权限编码
     * @return 权限及其所有子权限
     */
    @Override
    public List<String> getAllPermissionsByCode(String code) {
        // 1. 根据code查询权限
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPermissionCode, code);
        Permission permission = getOne(wrapper);
        
        if (permission == null) {
            return new ArrayList<>();
        }
        
        // 2. 获取该权限及其所有子权限
        List<String> codes = new ArrayList<>();
        codes.add(permission.getPermissionCode());
        
        // 递归获取子权限
        List<Permission> children = getChildrenPermissions(permission.getId());
        for (Permission child : children) {
            codes.addAll(getAllPermissionsByCode(child.getPermissionCode()));
        }
                            
        return codes;
    }
    
    /**
     * 获取权限的所有子权限
     * @param parentId 父权限ID
     * @return 子权限列表
     */
    private List<Permission> getChildrenPermissions(Long parentId) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getParentId, parentId);
        return list(wrapper);
    }
} 