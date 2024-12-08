package com.qin.catcat.unite.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qin.catcat.unite.mapper.RoleMapper;
import com.qin.catcat.unite.mapper.UserRoleMapper;
import com.qin.catcat.unite.popo.entity.Role;
import com.qin.catcat.unite.popo.entity.UserRole;
import com.qin.catcat.unite.service.RoleService;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRole(Role role) {
        // 检查角色编码是否已存在
        if (checkRoleCodeExists(role.getRoleCode())) {
            return false;
        }
        return save(role);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(Role role) {
        // 检查角色编码是否已存在（排除自身）
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, role.getRoleCode())
               .ne(Role::getId, role.getId());
        if (count(wrapper) > 0) {
            return false;
        }
        return updateById(role);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long roleId) {
        // 删除角色与用户的关联关系
        LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(UserRole::getRoleId, roleId);
        userRoleMapper.delete(userRoleWrapper);
        
        // 删除角色
        return removeById(roleId);
    }
    
    @Override
    public List<Role> getRolesByUserId(Long userId) {
        // 查询用户角色关联表
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(wrapper);
        
        // 获取角色ID列表
        List<Long> roleIds = userRoles.stream()
                                    .map(UserRole::getRoleId)
                                    .collect(Collectors.toList());
        
        // 查询角色信息
        if (roleIds.isEmpty()) {
            return List.of();
        }
        return listByIds(roleIds);
    }
    
    @Override
    public boolean checkRoleCodeExists(String roleCode) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, roleCode);
        return count(wrapper) > 0;
    }
} 