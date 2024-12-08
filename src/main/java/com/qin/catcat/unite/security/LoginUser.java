package com.qin.catcat.unite.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.qin.catcat.unite.popo.entity.Permission;
import com.qin.catcat.unite.popo.entity.User;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 登录用户信息
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-04 16:00
 */
@Data
@NoArgsConstructor
public class LoginUser implements UserDetails {
    
    private static final long serialVersionUID = 1L;
    
    private User user;
    
    private List<String> permissions;
    
    public LoginUser(User user, List<String> permissions) {
        this.user = user;
        this.permissions = permissions;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 将permissions转换成GrantedAuthority对象
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    
    @Override
    public String getUsername() {
        return user.getUsername();
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return user.getStatus() == 1;
    }
} 