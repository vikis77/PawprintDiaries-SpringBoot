package com.qin.catcat.unite.service;

import com.qin.catcat.unite.popo.dto.UserLoginDTO;

public interface UserService {
    /**
    * 登录
    * @param 
    * @return 
    */  
    public int loginUser(UserLoginDTO userLoginDTO);

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
    public void updatePassword(Long userId,String newPassword);

}