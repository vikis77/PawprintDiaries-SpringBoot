package com.qin.catcat.unite.controller;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.common.utils.GeneratorIdUtil;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.param.AddPostCommentParam;
import com.qin.catcat.unite.param.ReviewCommentParam;
import com.qin.catcat.unite.popo.dto.AddPostCommentDTO;
import com.qin.catcat.unite.popo.entity.PostComment;
import com.qin.catcat.unite.popo.vo.AuditCommentVO;
import com.qin.catcat.unite.popo.vo.PostCommentVO;
import com.qin.catcat.unite.security.HasPermission;
import com.qin.catcat.unite.service.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description 评论管理控制器
 *
 * @Author qrb
 * @Version 1.0
 * @Since 
 */

@Tag(name = "评论接口")
@RestController
@RequestMapping("/api/comment")
@Slf4j
public class CommentController {
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private CommentService commentService;
    @Autowired private GeneratorIdUtil generatorIdUtil;

    /**
     * 新增帖子评论
     * @param Token 用户认证token
     * @param context 评论内容
     * @param postId 帖子ID
     * @return 操作结果
     */ 
    @Operation(summary = "新增帖子评论")
    @HasPermission("system:comment:add")
    @PostMapping("/add")
    public Result<?> addComment(@RequestBody AddPostCommentParam addPostCommentParam) {
        AddPostCommentDTO addPostCommentDTO = new AddPostCommentDTO();
        BeanUtils.copyProperties(addPostCommentParam, addPostCommentDTO);
        commentService.addComment(addPostCommentDTO);
        return Result.success();
    }

    /**
     * 删除评论
     * @param Token 用户认证token
     * @param commentId 评论ID
     * @return 操作结果
     */
    @Operation(summary = "删除评论")
    @HasPermission("system:comment:delete")
    @DeleteMapping("/deleteComment")
    public Result<?> deleteComment(
            @RequestHeader("Authorization") String Token,
            @RequestParam Long commentId) {
        String username = jwtTokenProvider.getUsernameFromToken(Token);
        String userId = jwtTokenProvider.getUserIdFromJWT(Token);
        log.info("用户{}请求删除评论",username);

        commentService.deleteComment(commentId);
        return Result.success();
    }
    
    /**
     * 根据帖子ID分页查询评论(按评论时间降序)
     * @param Token 用户认证token
     * @param postId 帖子ID
     * @param page 页码
     * @param size 每页大小
     * @return 评论列表（分页）
     */
    @Operation(summary = "按时间查询帖子评论")
    @HasPermission("system:comment:view")
    @GetMapping("/getCommentByPostidByDescTime")
    public Result<IPage<PostComment>> getCommentByPostidByDescTime(
            @RequestHeader("Authorization") String Token,
            @RequestParam Long postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        String username = jwtTokenProvider.getUsernameFromToken(Token);
        String userId = jwtTokenProvider.getUserIdFromJWT(Token);
        log.info("用户{}请求根据帖子ID分页查询评论(按评论时间降序)",username);

        IPage<PostComment> comments = commentService.getCommentByPostidOrderByDescTime(postId,page,size);
        return Result.success(comments);
    }

    /**
     * 根据帖子ID分页查询评论(按点赞数降序)
     * @param Token 用户认证token
     * @param postId 帖子ID
     * @param page 页码
     * @param size 每页大小
     * @return 评论列表（分页）
     */
    @Operation(summary = "按点赞数查询帖子评论")
    @HasPermission("system:comment:view")
    @GetMapping("/getCommentByPostidByDescLikecount")
    public Result<List<PostCommentVO>> getCommentByPostidByDescLikecount(
            @RequestParam Integer postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<PostCommentVO> comments = commentService.getCommentByPostidOrderByDescLikecount(postId,page,size);
        return Result.success(comments);
    }

    /**
     * 根据父评论ID分页查询子评论(按评论时间降序)
     * @param Token 用户认证token
     * @param fatherId 父评论ID
     * @param page 页码
     * @param size 每页大小
     * @return 子评论列表（分页）
     */
    @Operation(summary = "查询帖子评论")
    @HasPermission("system:comment:view")
    @GetMapping("/getCommentByFatheridByDescTime")
    public Result<IPage<PostComment>> getCommentByFatheridByDescTime(
            @RequestHeader("Authorization") String Token,
            @RequestParam Long fatherId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        String username = jwtTokenProvider.getUsernameFromToken(Token);
        String userId = jwtTokenProvider.getUserIdFromJWT(Token);
        log.info("用户{}请求根据父评论ID分页查询子评论(按评论时间降序)",username);

        IPage<PostComment> comments = commentService.getCommentByFatheridByDescTime(fatherId,page,size);
        return Result.success(comments);
    }

    /**
     * 按时间分页查询待审核评论
     * @param page 页码
     * @param pageSize 每页大小
     * @param type 评论类型：all、post、cat
     * @param sort 排序方式：desc、asc
     * @return 待审核评论列表（分页）
     */
    @Operation(summary = "按时间分页查询待审核评论")
    @HasPermission("system:comment:view")
    @GetMapping("/getAuditCommentByDescTime")
    public Result<AuditCommentVO> getAuditCommentByDescTime(@RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam String type,
        @RequestParam String sort) {
        AuditCommentVO auditCommentVO = commentService.getAuditCommentByDescTime(page, pageSize, type, sort);
        return Result.success(auditCommentVO);
    }

    /**
     * 审核评论
     * @param id 评论ID
     * @param type 评论类型：10小猫评论 20帖子评论
     * @param action 审核操作：approve 通过 reject 拒绝
     * @return 操作结果
     */
    @Operation(summary = "审核评论")
    @HasPermission("system:comment:audit")
    @PostMapping("/review")
    public Result<?> reviewComment(
            @RequestBody ReviewCommentParam reviewCommentParam) {
        commentService.reviewComment(reviewCommentParam.getCommentId(), reviewCommentParam.getType(), reviewCommentParam.getAction());
        return Result.success();
    }
}
