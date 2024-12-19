package com.qin.catcat.unite.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.common.utils.TokenHolder;
import com.qin.catcat.unite.mapper.RoleMapper;
import com.qin.catcat.unite.mapper.UserMapper;
import com.qin.catcat.unite.mapper.UserRoleMapper;
import com.qin.catcat.unite.param.UpdateRoleParam;
import com.qin.catcat.unite.popo.entity.Role;
import com.qin.catcat.unite.popo.entity.User;
import com.qin.catcat.unite.popo.entity.UserRole;
import com.qin.catcat.unite.popo.vo.RoleListVO;
import com.qin.catcat.unite.service.RoleService;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private UserMapper userMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRole(Role role) {
        // 检查角色编码是否已存在
        if (checkRoleCodeExists(role.getRoleCode())) {
            return false;
        }
        return save(role);
    }
    
    /**
     * @Description 更新角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(UpdateRoleParam param) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, param.getUserId());
        wrapper.eq(UserRole::getRoleId, param.getRole().equals("ADMIN") ? 3 : 2);
        UserRole userRole = userRoleMapper.selectOne(wrapper);
        if (userRole == null) {
            return false;
        }
        userRole.setRoleId(param.getRole().equals("ADMIN") ? 2 : 3);
        Integer updateUserId = Integer.parseInt(jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken()));
        userRole.setUpdateUserId(updateUserId);
        return userRoleMapper.updateById(userRole) > 0;
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
    public List<Role> getRolesByUserId(Integer userId) {
        // 查询用户角色关联表
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(wrapper);
        
        // 获取角色ID列表
        List<Integer> roleIds = userRoles.stream()
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

    /**
     * @Description 分页获取角色列表（目前只支持获取管理员）
     */
    @Override
    public List<RoleListVO> list(int page, int pageSize) {
        // 查询管理员
        LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(UserRole::getRoleId, 2);
        List<UserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);

        if (userRoles.isEmpty()) {
            return List.of();
        }
        // 查询用户信息
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.in(User::getUserId, userRoles.stream().map(UserRole::getUserId).collect(Collectors.toList()));
        List<User> users = userMapper.selectList(userWrapper);

        List<RoleListVO> roleListVOs = users.stream().map(user -> {
            RoleListVO vo = new RoleListVO();
            vo.setUserId(user.getUserId());
            vo.setUserName(user.getUsername());
            vo.setNickName(user.getNickName());
            vo.setAvatar(user.getAvatar());
            vo.setRegisterTime(user.getCreateTime().toLocalDateTime());
            vo.setRole("ADMIN");
            return vo;
        }).collect(Collectors.toList());
        return roleListVOs;
    }

    /**
     * @Description 搜索用户及其角色（目前只支持搜索用户ID）
     */
    @Override
    public List<RoleListVO> searchUsersAndRoles(String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserId, Integer.parseInt(keyword));
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            return List.of();
        }
        // 查询用户角色
        LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(UserRole::getUserId, user.getUserId());
        List<UserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);
        List<RoleListVO> roleListVOs = userRoles.stream().map(userRole -> {
            RoleListVO vo = new RoleListVO();
            vo.setUserId(user.getUserId());
            vo.setUserName(user.getUsername());
            vo.setNickName(user.getNickName());
            vo.setAvatar(user.getAvatar());
            vo.setRegisterTime(user.getCreateTime().toLocalDateTime());
            vo.setRole(userRole.getRoleId() == 2 ? "ADMIN" : "USER");
            return vo;
        }).collect(Collectors.toList());
        return roleListVOs;
    }
} 