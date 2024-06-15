package com.qin.catcat.unite.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qin.catcat.unite.exception.PasswordIncorrectException;
import com.qin.catcat.unite.exception.UserNotExistException;
import com.qin.catcat.unite.mapper.UserMapper;
import com.qin.catcat.unite.popo.dto.UserLoginDTO;
import com.qin.catcat.unite.popo.entity.User;
import com.qin.catcat.unite.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService{
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
    * 登录
    * @param 
    * @return 1验证通过 2密码错误 3用户名不存在
    */
    public int loginUser(UserLoginDTO userLoginDTO){
        int result=1;

        //1.查询用户是否存在且合法
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();// 使用QueryWrapper构建查询条件
        queryWrapper
            .eq("username", userLoginDTO.getUsername())//用户名是否存在
            .eq("status",1);//账号状态是否正常
        // Long count = userMapper.selectCount(queryWrapper);
        User user = userMapper.selectOne(queryWrapper);
        // System.out.println(count);

        if(user!=null){
            //2.用户如果存在，验证密码
            QueryWrapper<User> queryWrapper2 = new QueryWrapper<>();// 使用QueryWrapper构建查询条件
            queryWrapper2
                //TODO 实际上全部字段都查了出来
                .select("password")
                .eq("username", userLoginDTO.getUsername());

            List<User> userList = userMapper.selectList(queryWrapper);//查询数据库
            if(!userList.isEmpty()){
                User user2 = userList.get(0);
                String storePassword = user2.getPassword();//数据库中存储的密码
                String enteredPassword = userLoginDTO.getPassword();//用户输入的密码
                log.info("输入密码:"+enteredPassword);
                boolean passwordMatches = passwordEncoder.matches(enteredPassword, storePassword);//密码加密比对,两个参数不能反，前面用户输入明文，后面数据库密文
                if(passwordMatches){
                    //密码验证通过
                    result=1;
                    log.info("密码验证通过");
                    //生成Token
                    return 1;
                }else{
                    //密码验证失败
                    log.info("密码验证失败");
                    throw new PasswordIncorrectException("密码错误");
                }
            }else{
                //没有找到对应的用户记录
                //TODO 冗余操作
                log.info("用户不存在");
                throw new UserNotExistException("用户不存在");
            }
        }else{
            //用户不存在或账号状态不正常
            log.info("用户不存在或账号状态不正常");
            throw new UserNotExistException("用户不存在或账号状态不正常");

        }
        // 返回查询结果
    }

    /**
    * 用户注册
    * @param 
    * @return 
    */
    public Boolean registerUser(UserLoginDTO userLoginDTO){
        QueryWrapper<User> wrapper = new QueryWrapper<>();//创建条件构造器
        wrapper.eq("usename", userLoginDTO.getUsername());//条件构造
        User storeUser = userMapper.selectOne(wrapper);//条件查询数据库

        if(storeUser!=null){
            //数据库已经存在此用户名，注册失败
            return false;
        }
        //创建新用户
        User user = new User();
        user.setUsername(userLoginDTO.getUsername());
        //使用BCryptPasswordEncoder 加密密码
        String encodedPassword = passwordEncoder.encode(userLoginDTO.getPassword());
        user.setPassword(encodedPassword);
        user.setStatus(1);

        //保存用户到数据库
        int result = userMapper.insert(user);
        return true;
    }

    /**
    * 更新用户密码
    * @param 
    * @return 
    */
    public void updatePassword(Long userId,String newPassword){
        //根据用户ID获取用户信息
        User user = userMapper.selectById(userId);

        if(user!=null){
            //使用 BCryptPasswordEncoder 加密新密码
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);

            //更新用户密码到数据库
            userMapper.updateById(user);
        }
    }
}
