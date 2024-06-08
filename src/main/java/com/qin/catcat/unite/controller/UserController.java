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

        //1.查询数据库，是否存在用户
        Boolean result = userService.Login(userLoginDTO);
        // log.info(String.valueOf(result));
        // System.out.println(String.valueOf(result));
        //2.用户存在 && 账号状态正常，设置token
        try {
            //创建token
            String token = Jwts.builder().setSubject(userLoginDTO.getUsername())//主题，可以放用户的详细信息(用户角色)
            .setId(null)//用户ID
            .setIssuedAt(new Date())//token创建时间
            .setExpiration(new Date(System.currentTimeMillis()+600000))//toekn过期时间
            .signWith(SignatureAlgorithm.HS256, "CATCAT")//加密方式和加密密码
            .compact();

        } catch (Exception e) {
            
        }
        //封装结果返回
        UserLoginVO userLoginVO = UserLoginVO.builder()
        .token("1")
        .build();
        return Result.success(userLoginVO);
    }
    
}
