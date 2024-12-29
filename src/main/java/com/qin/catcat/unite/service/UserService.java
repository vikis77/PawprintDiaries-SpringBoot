package com.qin.catcat.unite.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qin.catcat.unite.popo.dto.RegisterDTO;
import com.qin.catcat.unite.popo.dto.UpdateProfileDTO;
import com.qin.catcat.unite.popo.dto.UserLoginDTO;
import com.qin.catcat.unite.popo.entity.User;
import com.qin.catcat.unite.popo.vo.MyPageVO;
import com.qin.catcat.unite.popo.vo.UpdateProfileVO;

public interface UserService extends IService<User>{
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
    public Boolean registerUser(RegisterDTO registerDTO);

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
    public MyPageVO getUserProfile(String userId);

    /**
    * 更新用户信息
    * @param 
    * @return 
    */
    public UpdateProfileVO updateProfile(UpdateProfileDTO updateProfileDTO);

    //根据ID获取昵称
    public String getNicknameFromId(String id);

    /**
     * 关注用户
     * @param userId 用户ID
     * @return 操作结果
     */
    public boolean followUser(Long userId);

    /**
     * 取消关注用户
     * @param userId 用户ID
     * @return 操作结果
     */
    public boolean unfollowUser(Long userId);
}