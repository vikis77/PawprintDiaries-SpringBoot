package com.qin.catcat.unite.controller;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.common.utils.GeneratorIdUtil;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.popo.dto.PostDTO;
import com.qin.catcat.unite.popo.entity.Post;
import com.qin.catcat.unite.service.PostService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


//帖子控制器
@RestController
@RequestMapping("/Post")
@Tag(name = "帖子接口")
@Slf4j
public class PostController {
    @Autowired PostService postService;
    @Autowired JwtTokenProvider jwtTokenProvider;
    @Autowired GeneratorIdUtil generatorIdUtil;
    /**
    * 新增帖子
    * @param 
    * @return 
    */
    @PostMapping("/addpost")
    public Result<?> addPost(@RequestHeader("Authorization") String Token,@RequestBody PostDTO postDTO) {
        String username = jwtTokenProvider.getUsernameFromToken(Token);
        log.info("用户{}请求新增帖子",username);

        Post post = new Post();
        //属性拷贝DTO to entity
        BeanUtils.copyProperties(postDTO, post);

        
        post.setPostId(Long.parseLong(generatorIdUtil.GeneratorRandomId()));
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setSendTime(Timestamp.from(Instant.now()));
        Boolean singal = postService.add(post);
        return Result.success();
    }
    
    /**
    * 查询全部帖子
    * @param 
    * @return 
    */
    @GetMapping("/getAllPost")
    public Result<?> getAllPost(){
        return Result.success();
    }
}
