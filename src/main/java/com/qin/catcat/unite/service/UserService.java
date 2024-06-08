package com.qin.catcat.unite.service;

import com.qin.catcat.unite.popo.dto.UserLoginDTO;

public interface UserService {
    /**
    * 登录
    * @param 
    * @return 
    */  
    public Boolean Login(UserLoginDTO userLoginDTO);


}