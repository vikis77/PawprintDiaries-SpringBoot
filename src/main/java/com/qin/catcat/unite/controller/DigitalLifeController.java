package com.qin.catcat.unite.controller;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.common.utils.TokenHolder;
import com.qin.catcat.unite.mapper.PostMapper;
import com.qin.catcat.unite.mapper.PostPicsMapper;
import com.qin.catcat.unite.mapper.UserMapper;
import com.qin.catcat.unite.param.AddPostCommentParam;
import com.qin.catcat.unite.popo.dto.AddPostCommentDTO;
import com.qin.catcat.unite.popo.dto.PostDTO;
import com.qin.catcat.unite.popo.entity.CatComment;
import com.qin.catcat.unite.popo.entity.Post;
import com.qin.catcat.unite.popo.entity.PostComment;
import com.qin.catcat.unite.popo.entity.PostPics;
import com.qin.catcat.unite.popo.entity.User;
import com.qin.catcat.unite.popo.vo.AddPostVO;
import com.qin.catcat.unite.popo.vo.ApplyPostVO;
import com.qin.catcat.unite.popo.vo.AuditCommentVO;
import com.qin.catcat.unite.popo.vo.CatCommentVO;
import com.qin.catcat.unite.popo.vo.PostCommentVO;
import com.qin.catcat.unite.popo.vo.SinglePostVO;
import com.qin.catcat.unite.security.HasPermission;
import com.qin.catcat.unite.service.CommentService;
import com.qin.catcat.unite.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/digital")
@Slf4j
public class DigitalLifeController {
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private PostPicsMapper postPicsMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 发一个帖子
     * @return
     */
    @PostMapping("/addpost")
    public Result<AddPostVO> addPost(@RequestBody PostDTO postDto) { 
        log.info("Creating new post with title: {}", postDto.getTitle());
        Map<String, String> fileNameConvertMap = new HashMap<>();
        // 遍历图片列表，将图片名转换为新的文件名
        for(String imageName:postDto.getPictrueList()){
            fileNameConvertMap.put(imageName, imageName);
        }
        Post post = new Post();
        String userId = jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken());
        // 插入帖子基本信息
        post.setTitle(postDto.getTitle()); // 设置标题
        post.setArticle(postDto.getArticle()); // 设置文章
        post.setAuthorId(Integer.parseInt(userId));//设置作者ID（即用户本ID）
        post.setLikeCount(0);//设置点赞数 初始化0
        post.setCollectingCount(0); // 设置收藏数 初始化0
        post.setCommentCount(0);//设置评论数 初始化0
        post.setSendTime(LocalDateTime.now());//设置发帖时间
        post.setUpdateTime(LocalDateTime.now());//设置更新时间
        post.setCoverPicture(fileNameConvertMap.get(postDto.getPictrueList().get(0)));//设置封面(默认为第一张图片)
        post.setIsDeleted(0); // 设置是否删除 初始化0
        post.setIsAdopted(0); // 设置是否通过审核 初始化0
        postMapper.insert(post);

        // 插入帖子图片关联表
        int signal = 1;
        for(String imageName:postDto.getPictrueList()){
            PostPics postPics = new PostPics();
            postPics.setPostId(post.getPostId());
            postPics.setPicture(fileNameConvertMap.get(imageName));
            postPics.setPicNumber(signal++);
            postPicsMapper.insert(postPics);
        }

        // 更新用户发帖数
        User user = userMapper.selectById(userId);
        user.setPostCount(user.getPostCount() + 1);
        userMapper.updateById(user);
        return Result.success();
    }

    /**
     * 获取一个帖子
     */
    @PostMapping("/getOnePost")
    public Result<SinglePostVO> getOnePost() {
        return Result.success(postService.getRandomPost());
    }

    /**
     * 评论帖子
     */
    @PostMapping("/comment")
    public Result<?> comment(@RequestBody AddPostCommentParam addPostCommentParam) {
        AddPostCommentDTO addPostCommentDTO = new AddPostCommentDTO();
        BeanUtils.copyProperties(addPostCommentParam, addPostCommentDTO);
        commentService.addComment(addPostCommentDTO);
        return Result.success();
    }

    /**
     * 获取一个待审核的帖子
     */
    @Operation(summary = "获取一个待审核的帖子")
    @PostMapping("/getUnAuditedPost")
    public Result<ApplyPostVO> getUnAuditedPost() {
        log.info("获取待审核帖子请求");

        // 获取第一页第一条待审核帖子
        List<ApplyPostVO> unAuditedPosts = postService.getApplyPostBySendtimeForPage(1, 1);

        if (unAuditedPosts.isEmpty()) {
            log.info("当前没有待审核的帖子");
            return Result.success(null);
        }

        ApplyPostVO post = unAuditedPosts.get(0);
        log.info("获取到待审核帖子: postId={}, title={}", post.getPostId(), post.getTitle());

        return Result.success(post);
    }

    /**
     * 审核通过帖子
     */
    @Operation(summary = "审核通过帖子")
    @PostMapping("/auditPost")
    public Result<?> auditPost(@RequestBody AuditPostParam auditPostParam) {
        log.info("审核通过帖子请求: postId={}", auditPostParam.getPostId());

        if (auditPostParam.getPostId() == null) {
            log.warn("帖子ID不能为空");
            return Result.error("帖子ID不能为空");
        }

        Boolean result = postService.passApprove(auditPostParam.getPostId());

        if (result) {
            log.info("帖子审核通过成功: postId={}", auditPostParam.getPostId());
            return Result.success("帖子审核通过");
        } else {
            log.error("帖子审核通过失败: postId={}", auditPostParam.getPostId());
            return Result.error("帖子审核失败");
        }
    }

    /**
     * 拒绝审核帖子
     */
    @Operation(summary = "拒绝审核帖子")
    @PostMapping("/rejectPost")
    public Result<?> rejectPost(@RequestBody AuditPostParam auditPostParam) {
        log.info("拒绝审核帖子请求: postId={}", auditPostParam.getPostId());

        if (auditPostParam.getPostId() == null) {
            log.warn("帖子ID不能为空");
            return Result.error("帖子ID不能为空");
        }

        Boolean result = postService.refuseApprove(auditPostParam.getPostId());

        if (result) {
            log.info("帖子审核拒绝成功: postId={}", auditPostParam.getPostId());
            return Result.success("帖子审核已拒绝");
        } else {
            log.error("帖子审核拒绝失败: postId={}", auditPostParam.getPostId());
            return Result.error("帖子审核拒绝失败");
        }
    }

    /**
     * 获取一个待审核的评论
     */
    @Operation(summary = "获取一个待审核的评论")
    @PostMapping("/getUnAuditedComment")
    public Result<?> getUnAuditedComment() {
        log.info("获取待审核评论请求");

        // 获取第一页第一条待审核评论（包括帖子评论和小猫评论）
        AuditCommentVO auditCommentVO = commentService.getAuditCommentByDescTime(1, 1, "all", "desc");

        if (auditCommentVO == null) {
            log.info("当前没有待审核的评论");
            return Result.success(null);
        }

        // 优先返回帖子评论，如果没有则返回小猫评论
        if (auditCommentVO.getPostComments() != null && !auditCommentVO.getPostComments().isEmpty()) {
            PostCommentVO comment1 = auditCommentVO.getPostComments().get(0);
            PostCommentDigitalLifeVO comment = new PostCommentDigitalLifeVO();
            BeanUtils.copyProperties(comment1, comment);
            comment.setCommentId(comment1.getId());
            log.info("获取到待审核帖子评论: commentId={}, content={}", comment.getCommentId(), comment.getCommentContext());
            return Result.success(comment);
        } else if (auditCommentVO.getCatComments() != null && !auditCommentVO.getCatComments().isEmpty()) {
            CatCommentVO comment1 = auditCommentVO.getCatComments().get(0);
            CatCommentDigitalLifeVO comment = new CatCommentDigitalLifeVO();
            BeanUtils.copyProperties(comment1, comment);
            comment.setCommentId(comment1.getId());
            log.info("获取到待审核小猫评论: commentId={}, content={}", comment.getCommentId(), comment.getCommentContext());
            return Result.success(comment);
        }

        log.info("当前没有待审核的评论");
        return Result.success(null);
    }

    /**
     * 审核通过评论
     */
    @Operation(summary = "审核通过评论")
    @PostMapping("/auditComment")
    public Result<?> auditComment(@RequestBody AuditCommentParam auditCommentParam) {
        log.info("审核通过评论请求: commentId={}, type={}", auditCommentParam.getCommentId(), auditCommentParam.getType());

        if (auditCommentParam.getCommentId() == null) {
            log.warn("评论ID不能为空");
            return Result.error("评论ID不能为空");
        }

        if (auditCommentParam.getType() == null) {
            log.warn("评论类型不能为空");
            return Result.error("评论类型不能为空");
        }

        try {
            commentService.reviewComment(auditCommentParam.getCommentId(), auditCommentParam.getType(), "approve");
            log.info("评论审核通过成功: commentId={}, type={}", auditCommentParam.getCommentId(), auditCommentParam.getType());
            return Result.success("评论审核通过");
        } catch (Exception e) {
            log.error("评论审核通过失败: commentId={}, type={}, error={}",
                auditCommentParam.getCommentId(), auditCommentParam.getType(), e.getMessage());
            return Result.error("评论审核失败：" + e.getMessage());
        }
    }

    /**
     * 拒绝审核评论
     */
    @Operation(summary = "拒绝审核评论")
    @PostMapping("/rejectComment")
    public Result<?> rejectComment(@RequestBody AuditCommentParam auditCommentParam) {
        log.info("拒绝审核评论请求: commentId={}, type={}", auditCommentParam.getCommentId(), auditCommentParam.getType());

        if (auditCommentParam.getCommentId() == null) {
            log.warn("评论ID不能为空");
            return Result.error("评论ID不能为空");
        }

        if (auditCommentParam.getType() == null) {
            log.warn("评论类型不能为空");
            return Result.error("评论类型不能为空");
        }

        try {
            commentService.reviewComment(auditCommentParam.getCommentId(), auditCommentParam.getType(), "reject");
            log.info("评论审核拒绝成功: commentId={}, type={}", auditCommentParam.getCommentId(), auditCommentParam.getType());
            return Result.success("评论审核已拒绝");
        } catch (Exception e) {
            log.error("评论审核拒绝失败: commentId={}, type={}, error={}",
                auditCommentParam.getCommentId(), auditCommentParam.getType(), e.getMessage());
            return Result.error("评论审核拒绝失败：" + e.getMessage());
        }
    }

    /**
     * 审核帖子的请求参数
     */
    @Data
    public static class AuditPostParam {
        private Integer postId;
    }

    /**
     * 审核评论的请求参数
     */
    @Data
    public static class AuditCommentParam {
        private Integer commentId;
        private Integer type; // 评论类型：10小猫评论 20帖子评论
    }

    /**
     * @Description 帖子评论VO.
     *
     * @Author liuyun
     * @Version 1.0
     */
    @Data
    public static class PostCommentDigitalLifeVO{
        /**
         * 评论者头像
         */
        private String avatar;
        /**
         * 评论者昵称
         */
        private String nickName;
        /**
         * 是否点赞
         */
        private Boolean Liked;
        //主键ID
        private Integer commentId;
        //帖子ID
        private Integer postId;
        //评论类型：10小猫评论 20帖子评论
        private Integer type;
        //创建时间
        @TableField(fill = FieldFill.INSERT)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private LocalDateTime createTime;
        //更新时间
        @TableField(fill = FieldFill.INSERT_UPDATE)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private LocalDateTime updateTime;
        //评论状态：10待审核 20通过 30未通过
        private Integer status;
        //是否删除：0否 1是
        private Integer isDeleted;
        //是否置顶：0否 1是
        private Integer isTop;
        //评论内容
        private String commentContext;
        //评论点赞数
        private Integer likeCount;
        // 评论者ID
        private Integer commentUserId;
    }

    /**
     * @Description 小猫评论VO.
     *
     * @Author liuyun
     * @Version 1.0
     */
    @Data
    public class CatCommentDigitalLifeVO{
        /**
         * 评论者头像
         */
        private String avatar;
        /**
         * 评论者昵称
         */
        private String nickName;
        /**
         * 是否点赞
         */
        private Boolean liked;
        private Integer commentId;
        
        @Schema(description = "小猫ID")
        private Integer catId;
        
        @Schema(description = "评论内容")
        private String commentContext;
        
        @Schema(description = "评论用户ID")
        private Integer commentUserId;
        
        @Schema(description = "评论状态：10未审核 20通过 30不通过")
        private Integer status;
        
        @Schema(description = "是否删除：0否 1是")
        private Integer isDeleted;
        
        @Schema(description = "创建时间")
        @TableField(fill = FieldFill.INSERT)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private LocalDateTime createTime;
        
        @Schema(description = "更新时间")
        @TableField(fill = FieldFill.INSERT_UPDATE)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private LocalDateTime updateTime;
        
        @Schema(description = "点赞数")
        private Integer likeCount;
        
        @Schema(description = "是否置顶：0否 1是")
        private Integer isTop;

        @Schema(description = "评论类型：10小猫评论 20帖子评论")
        private Integer type;
    }


}
