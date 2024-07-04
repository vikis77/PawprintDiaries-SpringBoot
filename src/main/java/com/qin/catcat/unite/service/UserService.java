package com.qin.catcat.unite.service;

import com.qin.catcat.unite.popo.dto.UserLoginDTO;
import com.qin.catcat.unite.popo.entity.User;

public interface UserService {
    /**
    * 登录
    * @param 
    * @return 
    */  
    public String loginUser(UserLoginDTO userLoginDTO);

    /**
    * 注册
    * @param 
    * @return 
    */
    public Boolean registerUser(UserLoginDTO userLoginDTO);

    /**
    * 更新用户密码
    * @param 
    * @return 
    */
    public boolean updatePassword(String userId,String newPassword);

    /**
    * 根据userId获取用户信息
    * @param 
    * @return 
    */
    public User getUserProfile(String userId);

    //根据ID获取昵称
    public String getNicknameFromId(String id);
}