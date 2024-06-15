package com.qin.catcat.unite.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.popo.dto.UserLoginDTO;
import com.qin.catcat.unite.popo.vo.UserLoginVO;
import com.qin.catcat.unite.service.UserService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
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
    /**
    * 用户登录
    * @param 
    * @return 
    */
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录：{}",userLoginDTO.getUsername());

        //1.查询数据库，是否存在用户 1验证通过 2密码错误 3用户名不存在
        int result = userService.loginUser(userLoginDTO);

        if(result==1){
            //用户存在，账号状态正常，密码验证通过
            //封装结果返回
            UserLoginVO userLoginVO = UserLoginVO.builder()
            .token("1")
            .build();

            return Result.success(userLoginVO,"登录成功");
        }else if(result==2){
            //密码错误
            return Result.error("密码错误");
        }else if(result==3){
            //用户名不存在或账号状态异常
            return Result.error("用户名不存在或账号状态异常");
        }else{
            return Result.error("登录出错了,请联系管理员");
        }
        //2.用户存在 && 账号状态正常，设置token
        // try {
        //     //创建token
        //     String token = Jwts.builder().setSubject(userLoginDTO.getUsername())//主题，可以放用户的详细信息(用户角色)
        //     .setId(null)//用户ID
        //     .setIssuedAt(new Date())//token创建时间
        //     .setExpiration(new Date(System.currentTimeMillis()+600000))//toekn过期时间
        //     .signWith(SignatureAlgorithm.HS256, "CATCAT")//加密方式和加密密码
        //     .compact();

        // } catch (Exception e) {
            
        // }
        
        
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
}
