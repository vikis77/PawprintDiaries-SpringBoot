package com.qin.catcat.unite.security;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qin.catcat.unite.mapper.UserMapper;
import com.qin.catcat.unite.popo.entity.Permission;
import com.qin.catcat.unite.popo.entity.User;
import com.qin.catcat.unite.service.PermissionService;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 用户详情服务实现类
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-04 16:01
 */
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PermissionService permissionService;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("UserDetailsServiceImpl >>> 进入loadUserByUsername方法");
        log.info("UserDetailsServiceImpl >>> username: {}", username);
        User user = new User();
        
        if (StringUtils.isBlank(username)) {
            // 如果用户不存在,设置为游客身份
            user.setUserId(0);  // 游客ID设为0
            user.setUsername("guest");
            user.setPassword(""); // 游客无密码
            user.setStatus(1); // 设置状态为启用
        } else {
            // 查询用户信息
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, username);
            user = userMapper.selectOne(wrapper);
            
            if (Objects.isNull(user)) {
                throw new UsernameNotFoundException("用户不存在");
            }
        }

        // 查询用户权限信息
        List<Permission> permissions = permissionService.getPermissionsByUserId(user.getUserId());
        List<String> permissionCodes = permissions.stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toList());
        
        // 返回UserDetails对象
        return new LoginUser(user, permissionCodes);
    }
} 