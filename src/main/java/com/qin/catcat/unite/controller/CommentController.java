package com.qin.catcat.unite.controller;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.common.utils.GeneratorIdUtil;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.popo.entity.Comment;
import com.qin.catcat.unite.service.CommentService;

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
    @Autowired GeneratorIdUtil generatorIdUtil;

    /**
    * 新增评论
    * @param 
    * @return 
    */ 
    @PostMapping("/add")
    public Result<?> addComment(@RequestHeader("Authorization") String Token,@RequestParam String context,@RequestParam Long postId){
        String username = jwtTokenProvider.getUsernameFromToken(Token);
        String userId = jwtTokenProvider.getUserIdFromJWT(Token);
        log.info("用户{}请求新增评论",username);

        //构建评论实体对象
        Comment comment = Comment.builder()
        .commentId(Long.valueOf(generatorIdUtil.GeneratorRandomId()))//评论ID
        .commentTime(Timestamp.from(Instant.now()))//评论时间
        .likeCount(0)//点赞数
        .commentatorId(Long.valueOf(userId))//评论者ID
        .commentType("普通评论")//评论类型 TODO
        .postId(postId)//评论的帖子的ID
        .commentContext(context)//评论内容
        .build();

        Boolean signal = commentService.addComment(comment);

        //TODO
        return Result.success();
    }

    /**
    * 删除评论
    * @param 
    * @return 
    */
    @DeleteMapping("/deleteComment")
    public Result<?> deleteComent(@RequestHeader("Authorization") String Token,@RequestParam Long commentId){
        String username = jwtTokenProvider.getUsernameFromToken(Token);
        String userId = jwtTokenProvider.getUserIdFromJWT(Token);
        log.info("用户{}请求删除评论",username);

        Boolean signal = commentService.deleteComment(commentId);
        //TODO
        return Result.success();

    }
    
    /**
    * 根据帖子ID分页查询前十条评论(按评论时间 最新)
    * @param 
    * @return 
    */
    @GetMapping("/getCommentByPostidByDescTime")
    public Result<IPage<Comment>> getCommentByPostidByDescTime(@RequestHeader("Authorization") String Token,@RequestParam Long postId,@RequestParam(defaultValue = "1") int page,@RequestParam(defaultValue = "10") int size){
        String username = jwtTokenProvider.getUsernameFromToken(Token);
        String userId = jwtTokenProvider.getUserIdFromJWT(Token);
        log.info("用户{}请求根据帖子ID分页查询前十条评论(按评论时间 最新)",username);

        IPage<Comment> comments = commentService.getCommentByPostidOrderByDescTime(postId,page,size);
        return Result.success(comments);
    }

    /**
    * 根据帖子ID分页查询前十条评论(按点赞数 最多)
    * @param 
    * @return 
    */
    @GetMapping("/getCommentByPostidByDescLikecount")
    public Result<IPage<Comment>> getCommentByPostidByDescLikecount(@RequestHeader("Authorization") String Token,@RequestParam Long postId,@RequestParam(defaultValue = "1") int page,@RequestParam(defaultValue = "10") int size){
        String username = jwtTokenProvider.getUsernameFromToken(Token);
        String userId = jwtTokenProvider.getUserIdFromJWT(Token);
        log.info("用户{}请求根据帖子ID分页查询前十条评论(按点赞数 最多)",username);

        IPage<Comment> comments = commentService.getCommentByPostidOrderByDescLikecount(postId,page,size);
        return Result.success(comments);
    }

    /**
    * 根据父评论Id分页查询前十条子评论 (按评论时间 最新)
    * @param 
    * @return 
    */
    @GetMapping("/getCommentByFatheridByDescTime")
    public Result<IPage<Comment>> getCommentByFatheridByDescTime(@RequestHeader("Authorization") String Token,@RequestParam Long father_id,@RequestParam(defaultValue = "1") int page,@RequestParam(defaultValue = "10") int size){
        String username = jwtTokenProvider.getUsernameFromToken(Token);
        String userId = jwtTokenProvider.getUserIdFromJWT(Token);
        log.info("用户{}请求根据父评论Id分页查询前十条子评论 (按评论时间 最新)",username);

        IPage<Comment> comments = commentService.getCommentByFatheridByDescTime(father_id,page,size);
        return Result.success(comments);
    }
}
