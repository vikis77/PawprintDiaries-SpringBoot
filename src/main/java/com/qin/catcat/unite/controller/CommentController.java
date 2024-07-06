package com.qin.catcat.unite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.popo.entity.Comment;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.extern.slf4j.Slf4j;

@RestController
@Tag(name = "评论接口")
@RequestMapping("/comment")
@Slf4j
public class CommentController {
    @Autowired JwtTokenProvider jwtTokenProvider;
    @Autowired CommentService commentService;
    
    /**
    * 根据帖子ID分页查询前十条评论
    * @param 
    * @return 
    */
    @GetMapping("/getCommentByPostid")
    public Result<IPage<Comment>> getCommentByPostid(@RequestHeader("Authorization") String Token,@RequestParam Long postId){
        String username = jwtTokenProvider.getUsernameFromToken(Token);
        String userId = jwtTokenProvider.getUserIdFromJWT(Token);
        log.info("用户{}请求根据帖子ID分页查询前十条评论",username);

        IPage<Comment> comments = commentService.getCommentByPostid(postId);
        return Result.success(comments);
    }
}
