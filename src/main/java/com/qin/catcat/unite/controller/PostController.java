package com.qin.catcat.unite.controller;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.common.utils.GeneratorIdUtil;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.common.utils.TokenHolder;
import com.qin.catcat.unite.popo.dto.PostDTO;
import com.qin.catcat.unite.popo.entity.Post;
import com.qin.catcat.unite.popo.vo.HomePostVO;
import com.qin.catcat.unite.popo.vo.SinglePostVO;
import com.qin.catcat.unite.security.HasPermission;
import com.qin.catcat.unite.service.PostService;
import com.qin.catcat.unite.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "帖子接口")
@RestController
@RequestMapping("/api/post")
@Slf4j
public class PostController {
    @Autowired private PostService postService;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private UserService userService;
    @Autowired private GeneratorIdUtil generatorIdUtil;

    /**
     * 新增帖子
     * @param postDTO 帖子信息数据传输对象
     * @return 操作结果
     */
    @Operation(summary = "新增帖子")
    @HasPermission("system:post:add")
    @PostMapping("/addpost")
    public Result<?> addPost(@RequestBody PostDTO postDTO) {
        postService.add(postDTO);
        return Result.success();
    }

    /**
     * 删除帖子
     * @param postId 帖子ID
     * @return 操作结果
     */
    @Operation(summary = "删除帖子")
    @HasPermission("system:post:delete")
    @DeleteMapping("/deletepost")
    public Result<?> deletePost(@RequestParam String postId){
        // 鉴权：当前用户ID必须等于帖子发布者ID
        if (postService.isLegalDelete(postId)) {
            Boolean signal = postService.delete(postId);
            if(!signal){
                log.info("删除帖子失败");
                return Result.error("删除帖子失败");
            }
            log.info("删除帖子成功");
            return Result.success();
        } else {
            log.info("无权删除帖子");
            return Result.error("无权删除帖子");
        }
    }

    /**
     * 查询全部帖子
     * @return 所有帖子列表
     */
    @Operation(summary = "查询全部帖子")
    @HasPermission("system:post:view")
    @GetMapping("/getAllPost")
    public Result<List<Post>> getAllPost(){
        if (TokenHolder.getToken() == null) {
            log.info("未登录用户请求查询全部帖子");
        }
        else{
           String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
           log.info("用户{}请求查询全部帖子",username);
        }

        List<Post> posts = postService.getAllPost();
        log.info("查询到的帖子：{}",JSON.toJSONString(posts));
        return Result.success(posts);
    }

    /**
     * 根据帖子ID查询帖子
     * @param postId 帖子ID
     * @return 帖子详细信息
     */
    @Operation(summary = "根据ID查询帖子")
    @HasPermission("system:post:view")
    @GetMapping("/getPostByPostid")
    public Result<SinglePostVO> getPostByPostid(@RequestParam String postId){
        if (TokenHolder.getToken() == null) {
            log.info("未登录用户请求根据帖子ID查询帖子");
        }
        else{
            String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
            log.info("用户{}请求根据帖子ID查询帖子{}",username,postId);
        }

        SinglePostVO post = postService.getPostByPostId(postId);
        return Result.success(post);
    }

    /**
     * 根据发布时间分页查询帖子
     * @param page 页码
     * @param pageSize 每页大小
     * @return 帖子列表（分页）
     */
    @Operation(summary = "分页查询帖子")
    @HasPermission("system:post:view")
    @GetMapping("/getPostBySendtimeForPage")
    public Result<IPage<HomePostVO>> getPostBySendtime(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue="10") int pageSize){
        if (TokenHolder.getToken() == null) {
            log.info("未登录用户请求根据发布时间分页查询前十条帖子");
        }
        else{
            String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
            log.info("用户{}请求根据发布时间分页查询前十条帖子,第{}页，每页{}条",username,page,pageSize);
        }

        IPage<HomePostVO> posts = postService.getPostBySendtime(page,pageSize);
        log.info("查询到的帖子：{}",JSON.toJSONString(posts));
        return Result.success(posts);
    }

    /**
     * 根据点赞数分页查询帖子
     * @param Token 用户认证token
     * @param page 页码
     * @param pageSize 每页大小
     * @return 帖子列表（分页）
     */
    @Operation(summary = "按点赞数查询帖子")
    @HasPermission("system:post:view")
    @GetMapping("/getPostByLikeForPage")
    public Result<IPage<Post>> getPostByLikecount(
            @RequestHeader("Authorization") String Token,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue="10") int pageSize) {
        String username = jwtTokenProvider.getUsernameFromToken(Token);
        log.info("用户{}请求根据点赞数分页查询前十条帖子,第{}页，每页{}条",username,page,pageSize);

        IPage<Post> posts = postService.getPostByLikecount(page,pageSize);
        return Result.success(posts);
    }

    /**
     * 根据标题搜索相关帖子
     * @param Token 用户认证token
     * @param title 搜索标题
     * @param page 页码
     * @param pageSize 每页大小
     * @return 帖子列表（分页）
     */
    @Operation(summary = "按标题搜索帖子")
    @HasPermission("system:post:view")
    @GetMapping("/searchPostByTitle")
    public Result<IPage<Post>> getPostByTitle(
            @RequestHeader("Authorization") String Token,
            @RequestParam(defaultValue = "") String title,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        String username = jwtTokenProvider.getUsernameFromToken(Token);
        log.info("用户{}请求根据标题 {} 搜索相关帖子（匹配标题、匹配文章内容）,第{}页，每页{}条",username,title,page,pageSize);

        IPage<Post> posts = postService.getPostByTitle(title,page,pageSize);
        return Result.success(posts);
    }

    /**
     * 根据作者昵称搜索相关帖子
     * @param Token 用户认证token
     * @param nickName 作者昵称
     * @param page 页码
     * @param pageSize 每页大小
     * @return 帖子列表（分页）
     */
    @Operation(summary = "按作者搜索帖子")
    @HasPermission("system:post:view")
    @GetMapping("/searchPostByNickname")
    public Result<IPage<Post>> getPostByNickname(
            @RequestHeader("Authorization") String Token,
            @RequestParam String nickName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        String username = jwtTokenProvider.getUsernameFromToken(Token);
        log.info("用户{}请求根根据作者昵称 {} 搜索相关帖子,第{}页，每页{}条",username,nickName,page,pageSize);

        if(nickName==null){
            return Result.error("昵称不能为空");
        }

        IPage<Post> posts = postService.getPostByNickname(nickName,page,pageSize);
        return Result.success(posts);
    }

    /**
     * 点赞帖子
     * @param postId 帖子ID
     * @return 操作结果
     */
    @Operation(summary = "点赞帖子")
    @HasPermission("system:post:like")
    @PostMapping("/likePost")
    public Result<String> likePost(@RequestParam String postId){
        String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
        log.info("用户{}请求点赞帖子{}",username,postId);

        postService.likePost(postId);
        return Result.success();
    }

    /**
     * 取消点赞
     * @param postId 帖子ID
     * @return 操作结果
     */
    @Operation(summary = "取消点赞")
    @HasPermission("system:post:like")
    @PostMapping("/unlikePost")
    public Result<String> unlikePost(@RequestParam String postId){
        String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
        log.info("用户{}请求取消点赞帖子{}",username,postId);

        postService.unlikePost(postId);
        return Result.success();
    }
}
