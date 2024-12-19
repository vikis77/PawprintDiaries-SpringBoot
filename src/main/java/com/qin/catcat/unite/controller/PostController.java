package com.qin.catcat.unite.controller;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.common.utils.GeneratorIdUtil;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.common.utils.TokenHolder;
import com.qin.catcat.unite.param.PassApproveParam;
import com.qin.catcat.unite.param.RefuseApproveParam;
import com.qin.catcat.unite.popo.dto.PostDTO;
import com.qin.catcat.unite.popo.entity.Post;
import com.qin.catcat.unite.popo.vo.ApplyPostVO;
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
        log.info("Received request to add post");
        try {
            postService.add(postDTO);
            return Result.success();
        } catch (Exception e) {
            log.error("Error adding post: {}", e.getMessage(), e);
            throw e;
        }
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
    @GetMapping("/getPostByPostId")
    public Result<SinglePostVO> getPostByPostId(@RequestParam String postId){
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
    public Result<List<HomePostVO>> getPostBySendtime(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue="10") int pageSize){
        List<HomePostVO> posts = postService.getPostBySendtime(page,pageSize);
        log.info("查询到的帖子：{}",JSON.toJSONString(posts));
        return Result.success(posts);
    }

    @Operation(summary = "权重随机推送帖子")
    @HasPermission("system:post:view")
    @GetMapping("/getRandomWeightedPosts")
    public Result<List<HomePostVO>> getRandomWeightedPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        // 实现思路：基础权重随机适合一般场景
        // 1. 计算帖子权重分数：
        //    权重 = 点赞数 * 0.4 + 收藏数 * 0.3 + 评论数 * 0.2 + 浏览量 * 0.1
        // 2. 按权重排序后随机扰动
        //    最终分数 = 权重 * (0.8 + Random.nextDouble() * 0.4)
        
        List<HomePostVO> posts = postService.getRandomWeightedPosts(page, pageSize);
        return Result.success(posts);
    }

    @Operation(summary = "时间衰减权重推送帖子")
    @HasPermission("system:post:view")
    @GetMapping("/getTimeDecayWeightedPosts")
    public Result<List<HomePostVO>> getTimeDecayWeightedPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        // 实现思路：时间衰减权重适合新内容为主的场景
        // 1. 计算时间衰减权重：
        //    权重 = (基础分 + 互动分) * 时间衰减因子
        //    时间衰减因子 = 1 / (1 + α * 发布时间距今天数)
        //    α为衰减系数，可以根据需求调整
        
        List<HomePostVO> posts = postService.getTimeDecayWeightedPosts(page, pageSize);
        return Result.success(posts);
    }

    @Operation(summary = "基于用户兴趣推送帖子")
    @HasPermission("system:post:view")
    @GetMapping("/getRecommendedPosts")
    public Result<List<HomePostVO>> getRecommendedPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        // 实现思路：协同过滤推荐适合个性化推荐场景
        // 1. 分析用户历史行为（点赞、收藏、评论等）
        // 2. 找到相似用户群体
        // 3. 推荐相似用户喜欢的内容
        // 4. 加入随机因素避免推荐单一
        
        List<HomePostVO> posts = postService.getRecommendedPosts(page, pageSize);
        return Result.success(posts);
    }

    // 根据发布时间分页查询待审核帖子
    @Operation(summary = "分页查询待审核帖子")
    @HasPermission("system:post:view")
    @GetMapping("/getApplyPostBySendtimeForPage")
    public Result<List<ApplyPostVO>> getApplyPostBySendtimeForPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue="10") int pageSize){
        List<ApplyPostVO> posts = postService.getApplyPostBySendtimeForPage(page,pageSize);
        return Result.success(posts);
    }

    // 帖子通过审核
    @Operation(summary = "帖子通过审核")
    @HasPermission("system:post:audit")
    @PostMapping("/passApprove")
    public Result<String> passApprove(@RequestBody PassApproveParam passApproveParam){
        postService.passApprove(passApproveParam.getPostId());
        return Result.success();
    }

    // 帖子审核拒绝通过
    @Operation(summary = "帖子审核拒绝通过")
    @HasPermission("system:post:audit")
    @PostMapping("/refuseApprove")
    public Result<String> refuseApprove(@RequestBody RefuseApproveParam refuseApproveParam){
        postService.refuseApprove(refuseApproveParam.getPostId());
        return Result.success();
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
    public Result<List<Post>> getPostByLikecount(
            @RequestHeader("Authorization") String Token,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue="10") int pageSize) {
        String username = jwtTokenProvider.getUsernameFromToken(Token);
        log.info("用户{}请求根据点赞数分页查询前十条帖子,第{}页，每页{}条",username,page,pageSize);

        List<Post> posts = postService.getPostByLikecount(page,pageSize);
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
    public Result<String> likePost(@RequestParam Long postId){
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
    @PostMapping("/unLikePost")
    public Result<String> unlikePost(@RequestParam Long postId){
        postService.unlikePost(postId);
        return Result.success();
    }

    // 收藏帖子
    @Operation(summary = "收藏帖子")
    @HasPermission("system:post:collect")
    @PostMapping("/collectPost")
    public Result<String> collectPost(@RequestParam Long postId){
        postService.collectPost(postId);
        return Result.success();
    }

    // 取消收藏帖子
    @Operation(summary = "取消收藏帖子")
    @HasPermission("system:post:collect")
    @PostMapping("/unCollectPost")
    public Result<String> unCollectPost(@RequestParam Long postId){
        postService.unCollectPost(postId);
        return Result.success();
    }
}
