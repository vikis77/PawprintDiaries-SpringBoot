package com.qin.catcat.unite.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qin.catcat.unite.mapper.UserMapper;
import com.qin.catcat.unite.popo.dto.UserLoginDTO;
import com.qin.catcat.unite.popo.entity.User;
import com.qin.catcat.unite.service.UserService;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserMapper userMapper;
    /**
    * 登录
    * @param 
    * @return 
    */
    public Boolean Login(UserLoginDTO userLoginDTO){
        //1.查询用户是否存在且合法
        // 使用LambdaQueryWrapper构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
            .eq(User::getUsername, userLoginDTO.getUsername())//用户名是否存在
            .eq(User::getStatus,1);//账号状态是否正常

        // 查询是否存在该用户名
        Long count = userMapper.selectCount(queryWrapper);

        // 返回查询结果
        return count > 0;
    }
}
