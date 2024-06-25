package com.qin.catcat.unite.config;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.mapper.UserMapper;
import com.qin.catcat.unite.popo.entity.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

//认证成功后的处理
@Slf4j
@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler{

    // private JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    // private final JwtTokenProvider jwtTokenProvider;
    @Autowired JwtTokenProvider jwtTokenProvider;
    @Autowired UserMapper userMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        log.info(authentication.getName()+"用户认证成功");

        Object principal = authentication.getPrincipal();//获取用户身份信息
        String username = authentication.getName();
        // Object credentials = authentication.getCredentials();//获取用户凭证信息
        // Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();//获取用户权限信息

        System.out.println("Username: " + username);
        // System.out.println("Authorities: " + authorities);

        //根据用户名查询userId
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("user_id").eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        String userId = user.getUserId();


        // 创建一个包含用户认证信息的 UsernamePasswordAuthenticationToken 对象
        ////字段说明：认证的主体、密码、权限
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,null,null);

        Map<String, String> details = new HashMap<>();
        details.put("userId", userId);
        authenticationToken.setDetails(details);

        // 设置用户请求的额外的详细信息
        // authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // 将认证信息存储到 SecurityContext 中，以便后续的请求处理中能够获取到用户的认证信息
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);


        //认证成功，根据username, userId生成token，构建返回json
        String JwtToken = jwtTokenProvider.generateToken(username, userId);
        log.info("生成JwtToken：{}",JwtToken);
        HashMap<String,String> map = new HashMap<>();
        map.put("token",JwtToken);
        Result<Object> result = Result.success(map);
        String json = JSON.toJSONString(result);

        //构建响应头 Token放响应头
        response.setHeader("Authorization", "Bearer " + JwtToken);
        response.getWriter().write("登录成功");
        response.getWriter().flush();
        //构建响应体 Token放响应体
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(json);
    }
    
}
