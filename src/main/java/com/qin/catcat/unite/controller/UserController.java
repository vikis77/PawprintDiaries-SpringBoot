package com.qin.catcat.unite.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qin.catcat.unite.common.annotation.RedisRateLimit;
import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.common.utils.TokenHolder;
import com.qin.catcat.unite.param.UpdateProfileParam;
import com.qin.catcat.unite.popo.dto.RegisterDTO;
import com.qin.catcat.unite.popo.dto.UpdateProfileDTO;
// import com.qin.catcat.unite.common.utils.JwtTokenProviderUtils;
import com.qin.catcat.unite.popo.dto.UserLoginDTO;
import com.qin.catcat.unite.popo.entity.User;
import com.qin.catcat.unite.popo.vo.HomePostVO;
import com.qin.catcat.unite.popo.vo.MyPageVO;
import com.qin.catcat.unite.popo.vo.UpdateProfileVO;
import com.qin.catcat.unite.popo.vo.UserLoginVO;
import com.qin.catcat.unite.security.HasPermission;
import com.qin.catcat.unite.service.UserService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.rmi.registry.Registry;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户接口") // swagger接口文档
@Slf4j
// @CrossOrigin(origins = "https://pawprintdiaries.luckyiur.com") // 允许的来源
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * 用户登录
     * 
     * @param
     * @return
     */
    @PostMapping("/login")
    @HasPermission("system:user:login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录：{}", userLoginDTO.getUsername());
        // 查询数据库,登录成功返回jwt Token
        String jwt = userService.loginUser(userLoginDTO);
        log.info(jwt);
        // 封装结果返回
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .token(jwt)
                .build();
        return Result.success(userLoginVO, "登录成功");
    }

    /**
     * 用户注册
     * 
     * @param
     * @return
     */
    @PostMapping("/register")
    @HasPermission("system:user:register")
    public Result<UserLoginVO> register(@RequestBody RegisterDTO registerDTO) {
        log.info("用户注册：{}", registerDTO.getUsername());
        // 1.查询数据库，是否存在用户 true注册成功，false注册失败（用户已存在）
        Boolean result = userService.registerUser(registerDTO);
        if (result) {
            // 注册成功
            return Result.success("注册成功");
        } else {
            // 注册失败，用户名已存在
            return Result.error("注册失败，用户已存在");
        }
    }

    /**
     * 获取个人信息
     * 
     * @param
     * @return
     */
    @GetMapping("/profile")
    @HasPermission("system:profile:view")
    public Result<MyPageVO> getUserProfile() {
        String userId = jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken());
        MyPageVO user = userService.getUserProfile(userId);
        return Result.success(user);
    }

    // 更新个人信息
    @PostMapping("/updateProfile")
    public Result<UpdateProfileVO> updateProfile(@RequestBody UpdateProfileParam updateProfileParam) {
        log.info("更新个人信息：{}", updateProfileParam);
        UpdateProfileDTO updateProfileDTO = new UpdateProfileDTO();
        BeanUtils.copyProperties(updateProfileParam, updateProfileDTO);
        return Result.success(userService.updateProfile(updateProfileDTO));
    }

    /**
     * 更新密码
     * 
     * @param
     * @return
     */
    @PostMapping("/updatePassword")
    public Result<?> updatePassword(@RequestHeader("Authorization") String token, @RequestParam String password) {
        String userId = jwtTokenProvider.getUserIdFromJWT(token);
        boolean resu = userService.updatePassword(userId, password);
        return Result.success("密码更新成功");
    }

    /**
     * 关注用户
     * @param userId 用户ID
     * @return 操作结果
     */
    @PostMapping("/followUser")
    @HasPermission("system:user:follow")
    public Result<String> followUser(@RequestParam Long userId){
        userService.followUser(userId);
        return Result.success();
    }

    /**
     * 取消关注用户
     * @param userId 用户ID
     * @return 操作结果
     */
    @PostMapping("/unfollowUser")
    @HasPermission("system:user:unfollow")
    public Result<String> unfollowUser(@RequestParam Long userId){
        userService.unfollowUser(userId);
        return Result.success();
    }
}
