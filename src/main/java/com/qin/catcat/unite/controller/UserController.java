package com.qin.catcat.unite.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qin.catcat.unite.common.result.Result;
// import com.qin.catcat.unite.common.utils.JwtTokenProviderUtils;
import com.qin.catcat.unite.popo.dto.UserLoginDTO;
import com.qin.catcat.unite.popo.entity.User;
import com.qin.catcat.unite.popo.vo.UserLoginVO;
import com.qin.catcat.unite.service.UserService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("")
@Tag(name = "通用接口")//swagger接口文档
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    // @Autowired
    // private JwtTokenProviderUtils jwtTokenProviderUtils;

    /**
    * 用户登录
    * @param 
    * @return 
    */
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录：{}",userLoginDTO.getUsername());

        //查询数据库,登录成功返回jwt Token
        String jwt = userService.loginUser(userLoginDTO);
        log.info(jwt);

        //封装结果返回
        UserLoginVO userLoginVO = UserLoginVO.builder()
        .token(jwt)
        .build();

        return Result.success(userLoginVO,"登录成功"); 
    }
    
    /**
    * 用户注册
    * @param 
    * @return 
    */
    @PostMapping("/register")
    public Result<UserLoginVO> register(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户注册：{}",userLoginDTO.getUsername());

        //1.查询数据库，是否存在用户 true注册成功，false注册失败（用户已存在）
        Boolean result = userService.registerUser(userLoginDTO); 
        if(result){
            //注册成功
            return Result.success("注册成功");
        }else{
            //注册失败，用户名已存在
            return Result.error("注册失败，用户已存在");
        }
    }

    @GetMapping("/profile")
    public Result<User> getUserProfile(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        log.info("用户:"+username+" userId:"+userId+" 获取个人信息");
        User user = userService.getUserProfile(userId);
        return Result.success(user);
    }
}
