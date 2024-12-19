package com.qin.catcat.unite.service.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qin.catcat.unite.popo.entity.PostLike;
import com.catcat.entity.UserFollow;
import com.qin.catcat.unite.common.utils.GeneratorIdUtil;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.common.utils.TokenHolder;
// import com.qin.catcat.unite.config.RabbitMQConfig;
import com.qin.catcat.unite.mapper.PostMapper;
import com.qin.catcat.unite.mapper.PostPicsMapper;
import com.qin.catcat.unite.mapper.UserFollowMapper;
import com.qin.catcat.unite.mapper.UserMapper;
import com.qin.catcat.unite.mapper.PostLikeMapper;
import com.qin.catcat.unite.mapper.PostCollectMapper;
import com.qin.catcat.unite.popo.dto.PostDTO;
import com.qin.catcat.unite.popo.entity.Post;
import com.qin.catcat.unite.popo.entity.PostCollect;
import com.qin.catcat.unite.popo.entity.PostPics;
import com.qin.catcat.unite.popo.entity.PostWeight;
import com.qin.catcat.unite.popo.entity.User;
import com.qin.catcat.unite.popo.vo.ApplyPostVO;
import com.qin.catcat.unite.popo.vo.HomePostVO;
import com.qin.catcat.unite.popo.vo.SinglePostVO;
import com.qin.catcat.unite.service.PostService;
import com.qin.catcat.unite.service.QiniuService;
import com.qin.catcat.unite.service.UserService;

import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PostServiceImpl implements PostService{

    private static final String LIKE_KEY = "post_likes";
    private static final String WEIGHTED_POSTS_KEY = "weighted_posts:";  // Redis中权重帖子的key前缀
    private static final long CACHE_EXPIRE_SECONDS = 300;  // 缓存过期时间：5分钟

    @Autowired UserMapper userMapper;
    @Autowired PostMapper postMapper;
    @Autowired PostPicsMapper postPicsMapper;
    @Autowired StringRedisTemplate RedisTemplate; //使用redis
    @Autowired RabbitTemplate rabbitTemplate; //使用rabbitmq
    @Autowired JwtTokenProvider jwtTokenProvider;
    @Autowired UserService userService;
    @Autowired GeneratorIdUtil generatorIdUtil;
    @Autowired QiniuService qiniuService;
    @Autowired UserFollowMapper userFollowMapper;
    @Autowired PostLikeMapper postLikeMapper;
    @Autowired PostCollectMapper postCollectMapper;

    /**
    * 新增帖子
    * @param 
    * @return 
    */
    public Boolean add(PostDTO postDto){
        log.info("Creating new post with title: {}", postDto.getTitle());
        Post post = new Post();
        // BeanUtils.copyProperties(postDto, post);//属性拷贝DTO to entity

        String userId = jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken());
        // String userNickname = userService.getNicknameFromId(userId); //根据用户ID查询用户昵称

        // 插入帖子基本信息
        // post.setPostId(Long.parseLong(generatorIdUtil.GeneratorRandomId()));//设置帖子ID
        post.setTitle(postDto.getTitle()); // 设置标题
        post.setArticle(postDto.getArticle()); // 设置文章
        post.setAuthorId(Long.parseLong(userId));//设置作者ID（即用户本ID）
        post.setLikeCount(0);//设置点赞数 初始化0
        post.setCollectingCount(0); // 设置收藏数 初始化0
        post.setCommentCount(0);//设置评论数 初始化0
        post.setSendTime(Timestamp.from(Instant.now()));//设置发帖时间
        post.setUpdateTime(Timestamp.from(Instant.now()));//设置更新时间
        post.setCoverPicture(postDto.getPictrueList().get(0));//设置封面(默认为第一张图片)
        post.setIsDeleted(0); // 设置是否删除 初始化0
        post.setIsAdopted(0); // 设置是否通过审核 初始化0
        postMapper.insert(post);

        // 插入帖子图片关联表
        int signal = 1;
        for(String imageName:postDto.getPictrueList()){
            PostPics postPics = new PostPics();
            postPics.setPostId(post.getPostId());
            postPics.setPicture(imageName);
            postPics.setPicNumber(signal++);
            postPicsMapper.insert(postPics);
        }
        
        return true;
    }

    /**
    * 查询全部帖子
    * @param 
    * @return 
    */
    public List<Post> getAllPost(){
        // 构建查询条件
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0); // 查询未删除的帖子
        queryWrapper.eq("is_adopted", 1); // 查询通过审核的帖子
        List<Post> posts = postMapper.selectList(queryWrapper);
        return posts;
    }

    /**
    * 根据帖子ID查询帖子全部图片
    * @param 
    * @return 
    */
    public SinglePostVO getPostByPostId(String postId){
        log.debug("Fetching post with id: {}", postId);
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", postId);
        queryWrapper.eq("is_deleted", 0); // 查询未删除的帖子
        queryWrapper.eq("is_adopted", 1); // 查询通过审核的帖子
        Post post = postMapper.selectOne(queryWrapper);
        if (post == null) {
            return null; // 或者抛出一个异常，表示未找到对应的帖子
        }
        SinglePostVO singlePostVO = new SinglePostVO();
        singlePostVO.setPostId(post.getPostId());
        singlePostVO.setTitle(post.getTitle());
        singlePostVO.setArticle(post.getArticle());
        singlePostVO.setAuthorId(post.getAuthorId());
        singlePostVO.setLikeCount(post.getLikeCount());
        singlePostVO.setCollectingCount(post.getCollectingCount());
        singlePostVO.setCommentCount(post.getCommentCount());
        singlePostVO.setSendTime(post.getSendTime());
        singlePostVO.setUpdateTime(post.getUpdateTime());
        // 根据作者ID查询作者昵称、作者头像
        User author = userMapper.selectById(post.getAuthorId());
        if (author != null) {
            singlePostVO.setAuthorNickname(author.getNickName());
            singlePostVO.setAuthorAvatar(author.getAvatar());
        }else{
            throw new RuntimeException("作者不存在");
        }
        // 根据帖子ID查询帖子的全部图片（post_pics表）
        List<PostPics> postPicsList = postPicsMapper.selectByPostId(Long.parseLong(postId));
        singlePostVO.setImages(postPicsList);
        // 当前用户已登录
        if (TokenHolder.getToken() != null) {
            // 当前登录用户是否关注作者
            String currentUserId = jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken());
            QueryWrapper<UserFollow> queryFollowWrapper = new QueryWrapper<>();
            queryFollowWrapper.eq("user_id", currentUserId).eq("followed_user_id", post.getAuthorId()).eq("is_deleted", 0);
            UserFollow userFollow = userFollowMapper.selectOne(queryFollowWrapper);
            singlePostVO.setFollowed(userFollow != null);
            // 当前登录用户是否点赞帖子
            QueryWrapper<PostLike> queryLikeWrapper = new QueryWrapper<>();
            queryLikeWrapper.eq("user_id", currentUserId).eq("post_id", post.getPostId()).eq("status", 1);
            PostLike postLike = postLikeMapper.selectOne(queryLikeWrapper);
            singlePostVO.setLiked(postLike != null);
            // 当前登录用户是否收藏帖子
            QueryWrapper<PostCollect> queryCollectWrapper = new QueryWrapper<>();
            queryCollectWrapper.eq("user_id", currentUserId).eq("post_id", post.getPostId()).eq("status", 1);
            PostCollect postCollect = postCollectMapper.selectOne(queryCollectWrapper);
            singlePostVO.setCollected(postCollect != null);
        } else {
            singlePostVO.setFollowed(false);
            singlePostVO.setLiked(false);
            singlePostVO.setCollected(false);
        }
        return singlePostVO;
    }

    /**
    * 根据发布时间分页查询前十条帖子
    * @param 
    * @return 
    */
    @Cacheable(value = "postForSendtime", key = "#page + '-' + #pageSize")
    public List<HomePostVO> getPostBySendtime(int page, int pageSize) {
        try {
            // 先查询Post数据
            QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("is_deleted", 0)
                    .eq("is_adopted", 1)
                    .orderByDesc("send_time");
            Page<Post> postPage = new Page<>(page, pageSize);
            IPage<Post> postResult = postMapper.selectPage(postPage, queryWrapper);
            
            if (postResult == null || postResult.getRecords() == null) {
                return new ArrayList<>();
            }
            
            // 转换为HomePostVO
            return postResult.getRecords().stream().map(post -> {
                HomePostVO vo = new HomePostVO();
                vo.setPostId(post.getPostId());
                vo.setAuthorId(post.getAuthorId());
                vo.setTitle(post.getTitle());
                vo.setCoverPicture(post.getCoverPicture());
                vo.setLikeCount(post.getLikeCount());
                
                // 获取作者信息
                User author = userMapper.selectById(post.getAuthorId());
                if (author != null) {
                    vo.setAuthorNickname(author.getNickName());
                    vo.setAuthorAvatar(author.getAvatar());
                }
                return vo;
            }).collect(Collectors.toList());
            
        } catch (Exception e) {
            log.error("获取帖子列表失败", e);
            return new ArrayList<>();
        }
    }

    /** 
    * 权重随机推送帖子：
    *   使用 Redis 来缓存一个用户在特定时间段内的推荐结果，
    * 这样就能保证同一用户在短时间内翻页时看到的是同一批经过随机权重排序的帖子。
    * 当请求第一页时，重新计算随机权重排序，保证推荐的新鲜度。
    * @param page 页码
    * @param pageSize 每页大小
    * @return 帖子列表
    */
    public List<HomePostVO> getRandomWeightedPosts(int page, int pageSize) {
        try {
            // 获取当前用户ID，未登录则使用默认值
            String currentUserId = "0";
            try {
                currentUserId = jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken());
            } catch (Exception e) {
                // 用户未登录，使用默认ID
            }

            List<PostWeight> weightedPosts;
            String cacheKey = "";

            if (page == 1) {
                // 第一页：直接重新计算权重和随机排序
                weightedPosts = calculateWeightedPosts();
                if (weightedPosts.isEmpty()) {
                    return new ArrayList<>();
                }

                // 将第一页的排序结果缓存，供后续翻页使用
                cacheKey = WEIGHTED_POSTS_KEY + currentUserId + ":" + System.currentTimeMillis();
                cacheWeightedPosts(weightedPosts, cacheKey);
            } else {
                // 非第一页：尝试获取最近的一次缓存结果
                String latestCacheKey = getLatestCacheKey(currentUserId);
                if (latestCacheKey == null) {
                    // 如果没有缓存，重新计算
                    weightedPosts = calculateWeightedPosts();
                    if (weightedPosts.isEmpty()) {
                        return new ArrayList<>();
                    }
                    // 缓存计算结果
                    cacheKey = WEIGHTED_POSTS_KEY + currentUserId + ":" + System.currentTimeMillis();
                    cacheWeightedPosts(weightedPosts, cacheKey);
                } else {
                    // 使用缓存的结果
                    List<String> cachedPostIds = RedisTemplate.opsForList().range(latestCacheKey, 0, -1);
                    if (cachedPostIds == null || cachedPostIds.isEmpty()) {
                        return new ArrayList<>();
                    }
                    weightedPosts = rebuildWeightedPosts(cachedPostIds);
                }
            }

            // 分页处理
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, weightedPosts.size());
            
            if (start >= weightedPosts.size()) {
                return new ArrayList<>();
            }
            
            return convertToHomePostVOs(weightedPosts.subList(start, end));
        } catch (Exception e) {
            log.error("获取权重随机推送帖子失败", e);
            return new ArrayList<>();
        }
    }

    /**
    * 计算带权重的帖子列表
    */
    private List<PostWeight> calculateWeightedPosts() {
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0)
                .eq("is_adopted", 1);
        List<Post> allPosts = postMapper.selectList(queryWrapper);
        
        if (allPosts.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 1. 先计算基础权重
        List<PostWeight> weightedPosts = allPosts.stream()
            .map(post -> {
                double baseWeight = calculateWeight(post);
                // 添加大范围的随机因子 (0.1 到 2.0 之间)
                double randomFactor = 0.1 + Math.random() * 1.9;
                // 时间衰减权重占30%，随机权重占70%
                double finalWeight = baseWeight * 0.3 + randomFactor * 0.7;
                return new PostWeight(post, finalWeight);
            })
            .collect(Collectors.toList());
        
        // 2. 随机打乱列表
        java.util.Collections.shuffle(weightedPosts);
        
        // 3. 重新排序（但是由于随机因子的权重更大，排序结果会有很大的随机性）
        return weightedPosts.stream()
            .sorted((a, b) -> Double.compare(b.getWeight(), a.getWeight()))
            .collect(Collectors.toList());
    }

    /**
    * 缓存权重排序结果
    */
    private void cacheWeightedPosts(List<PostWeight> weightedPosts, String cacheKey) {
        List<String> postIds = weightedPosts.stream()
            .map(pw -> String.valueOf(pw.getPost().getPostId()))
            .collect(Collectors.toList());
        
        if (!postIds.isEmpty()) {
            // 存入新的排序结果
            RedisTemplate.opsForList().rightPushAll(cacheKey, postIds);
            RedisTemplate.expire(cacheKey, CACHE_EXPIRE_SECONDS, java.util.concurrent.TimeUnit.SECONDS);
        }
    }

    /**
    * 获取最近的缓存key
    */
    private String getLatestCacheKey(String userId) {
        String pattern = WEIGHTED_POSTS_KEY + userId + ":*";
        Set<String> keys = RedisTemplate.keys(pattern);
        if (keys == null || keys.isEmpty()) {
            return null;
        }
        // 返回时间戳最大的key
        return keys.stream()
            .max((k1, k2) -> {
                long t1 = Long.parseLong(k1.split(":")[2]);
                long t2 = Long.parseLong(k2.split(":")[2]);
                return Long.compare(t1, t2);
            })
            .orElse(null);
    }

    /**
    * 根据缓存的ID列表重建权重帖子列表
    */
    private List<PostWeight> rebuildWeightedPosts(List<String> cachedPostIds) {
        return cachedPostIds.stream()
            .map(id -> {
                Post post = postMapper.selectById(Long.parseLong(id));
                return post != null ? new PostWeight(post, 1.0) : null;
            })
            .filter(pw -> pw != null)
            .collect(Collectors.toList());
    }

    /**
    * 转换为HomePostVO对象列表
    */
    private List<HomePostVO> convertToHomePostVOs(List<PostWeight> weightedPosts) {
        return weightedPosts.stream()
            .map(pw -> {
                Post post = pw.getPost();
                HomePostVO vo = new HomePostVO();
                vo.setPostId(post.getPostId());
                vo.setAuthorId(post.getAuthorId());
                vo.setTitle(post.getTitle());
                vo.setCoverPicture(post.getCoverPicture());
                vo.setLikeCount(post.getLikeCount());
                
                // 获取作者信息
                User author = userMapper.selectById(post.getAuthorId());
                if (author != null) {
                    vo.setAuthorNickname(author.getNickName());
                    vo.setAuthorAvatar(author.getAvatar());
                }
                return vo;
            })
            .collect(Collectors.toList());
    }

    /**
    * 计算帖子权重
    * @param post 帖子对象
    * @return 权重值
    */
    private double calculateWeight(Post post) {
        double weight = 0;
        
        // 1. 基础权重：点赞、收藏、评论、浏览量的加权和
        weight += post.getLikeCount() * 0.4;        // 点赞权重 40%
        weight += post.getCollectingCount() * 0.3;  // 收藏权重 30%
        weight += post.getCommentCount() * 0.2;     // 评论权重 20%
        
        // 2. 时间衰减因子：越新的帖子权重越高
        long currentTime = System.currentTimeMillis();
        long postTime = post.getSendTime().getTime();
        double timeDecay = Math.exp(-0.0000001 * (currentTime - postTime)); // 指数衰减
        
        // 3. 最终权重 = 基础权重 * 时间衰减因子
        weight = weight * timeDecay;
        
        // 4. 确保权重非负
        return Math.max(weight, 0.0);
    }

    /**
    * 时间衰减权重推送帖子
    * @param 
    * @return 
    */
    public List<HomePostVO> getTimeDecayWeightedPosts(int page,int pageSize){
        return null;
    }

    /**
    * 基于用户兴趣推送帖子
    * @param 
    * @return 
    */
    public List<HomePostVO> getRecommendedPosts(int page,int pageSize){
        return null;
    }

    /**
    * 根据发布时间分页查询待审核帖子
    * @param 
    * @return 
    */
    public List<ApplyPostVO> getApplyPostBySendtimeForPage(int page,int pageSize){
        Page<Post> postObj = new Page<>(page, pageSize);
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0)
                .eq("is_adopted", 0)
                .orderByDesc("send_time");
        IPage<Post> posts = postMapper.selectPage(postObj, queryWrapper);
        List<Post> postList = posts.getRecords();
        List<ApplyPostVO> applyPostVOList = postList.stream().map(post -> {
            ApplyPostVO vo = new ApplyPostVO();
            BeanUtils.copyProperties(post, vo);
            return vo;
        }).collect(Collectors.toList());

        // 根据作者ID查询作者昵称
        for(ApplyPostVO applyPostVO : applyPostVOList){
            User author = userMapper.selectById(applyPostVO.getAuthorId());
            if (author != null) {
                applyPostVO.setAuthorNickname(author.getNickName());
                applyPostVO.setAuthorAvatar(author.getAvatar());
            }
        }

        // 根据帖子ID查询帖子全部图片
        for(ApplyPostVO applyPostVO : applyPostVOList){
            List<PostPics> postPicsList = postPicsMapper.selectByPostId(applyPostVO.getPostId());
            List<String> imageFileNames = postPicsList.stream().map(PostPics::getPicture).collect(Collectors.toList());
            applyPostVO.setImages(imageFileNames);
        }

        return applyPostVOList;
    }

    /**
    * 帖子通过审核
    * @param 
    * @return 
    */
    public Boolean passApprove(Long postId){
        Post post = postMapper.selectById(postId);
        Long userId = Long.parseLong(jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken()));
        post.setApproveUserId(userId);
        post.setIsAdopted(1);
        postMapper.updateById(post);
        return true;
    }

    /**
    * 帖子审核拒绝通过
    * @param 
    * @return 
    */
    public Boolean refuseApprove(Long postId){
        Post post = postMapper.selectById(postId);
        Long userId = Long.parseLong(jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken()));
        post.setApproveUserId(userId);
        post.setIsAdopted(2);
        postMapper.updateById(post);
        return true;
    }

    /**
    * 根据点赞数分页查询前十条帖子
    * @param 
    * @return 
    */
    @Cacheable(value = "postForLikecount")
    public List<Post> getPostByLikecount(int page,int pageSize){
        try {
            Page<Post> postObj = new Page<>(page, pageSize);
            QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("is_deleted", 0)  // 只查询未删除的帖子
                       .eq("is_adopted", 1)   // 只查询已通过审核的帖子
                       .orderByDesc("like_count");
            return postMapper.selectPage(postObj, queryWrapper).getRecords();
        } catch (Exception e) {
            log.error("获取帖子列表失败", e);
            return new ArrayList<>();
        }
    }

    /**
    * 根据标题搜索相关帖子（匹配标题、匹配文章内容）分页搜索
    * @param 
    * @return 
    */
    public IPage<Post> getPostByTitle(String title,int page,int pageSize){
        Page<Post> postObj = new Page<>(page, pageSize);
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("title",title).or().like("article", title);
        IPage<Post> posts = postMapper.selectPage(postObj, queryWrapper);
        return posts;
    }

    /**
    * 根据作者昵称搜索相关帖子
    * @param 
    * @return 
    */
    public IPage<Post> getPostByNickname(String nickName,int page,int pageSize){
        Page<Post> postObj = new Page<>(page, pageSize);
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("author_nickname", nickName);
        IPage<Post> posts = postMapper.selectPage(postObj,queryWrapper);
        return posts;
    }

    /**
    * 判断是否有权限除
    * @param 
    * @return 
    */
    public Boolean isLegalDelete(String postId){
        // TODO 默认有权
        // Post post = postMapper.selectById(postId);
        // if(String.valueOf(post.getAuthorId()).equals(jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken()))){
        //     return true;
        // }else{
        //     return false;
        // }
        return true;
    }

    /**
    * 根据帖子ID删除帖子
    * @param 
    * @return 
    */
    public Boolean delete(String postId){
        // 查询全部帖子关联的图片名称集合
        List<PostPics> postPicsList = postPicsMapper.selectByPostId(Long.parseLong(postId));
        List<String> imageFileNames = postPicsList.stream().map(PostPics::getPicture).collect(Collectors.toList());
        // 批量删除七牛云图片
        qiniuService.deleteFile(imageFileNames, "post_pics");


        // 删除帖子表
        postMapper.deleteById(Long.parseLong(postId));

        // 获取帖子图片关联ID集合
        List<Long> postPicsIds = postPicsList.stream().map(PostPics::getId).collect(Collectors.toList());
        // 批量删除帖子图片关联表
        postPicsMapper.delete(new LambdaQueryWrapper<PostPics>().in(!Collections.isEmpty(postPicsIds),PostPics::getId, postPicsIds));
        return true;
    }

    /**
    * 更新帖子
    * @param 
    * @return 
    */
    public int update(Post post){
        int signal = postMapper.updateById(post);
        return signal;
    }

    /**
    * 根据帖子ID点赞
    * @param 
    * @return 
    */
    @Transactional
    public int likePost(Long postId){
        // 当前登录用户ID
        String currentUserId = jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken());
        
        // 检查是否已经存在点赞记录
        QueryWrapper<PostLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", currentUserId)
                    .eq("post_id", postId);
        PostLike existingLike = postLikeMapper.selectOne(queryWrapper);
        
        if (existingLike != null) {
            // 如果已经存在记录且状态为0（已取消），则更新状态为1
            if (existingLike.getStatus() == 0) {
                existingLike.setStatus(1);
                postLikeMapper.updateById(existingLike);
                // 更新点赞数
                updateLikeCount(postId, true);
                return getLikeCount(postId);
            }
            // 如果已经点赞，直接返回当前点赞数
            return getLikeCount(postId);
        }
        
        // 如果不存在记录，创建新的点赞记录
        PostLike postLike = new PostLike();
        postLike.setUserId(Long.parseLong(currentUserId));
        postLike.setPostId(postId);
        postLike.setStatus(1);
        postLikeMapper.insert(postLike);
        
        // Redis更新点赞数
        updateLikeCount(postId, true);
        return getLikeCount(postId);
    }

    /**
    * 根据帖子ID取消点赞
    * @param 
    * @return 
    */
    @Transactional
    public int unlikePost(Long postId){
        // 当前登录用户ID
        String currentUserId = jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken());
        
        // 查找现有的点赞记录
        QueryWrapper<PostLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", currentUserId)
                    .eq("post_id", postId);
        PostLike existingLike = postLikeMapper.selectOne(queryWrapper);
        
        if (existingLike != null && existingLike.getStatus() == 1) {
            // 更新状态为未点赞
            existingLike.setStatus(0);
            postLikeMapper.updateById(existingLike);
            // Redis更新点赞数
            updateLikeCount(postId, false);
        }
        // 返回当前Redis中点赞数
        return getLikeCount(postId);
    }

    // （Redis）辅助方法：更新点赞数
    private void updateLikeCount(Long postId, boolean isIncrement) {
        HashOperations<String,String,String> hashOps = RedisTemplate.opsForHash();
        String likeCount = hashOps.get(LIKE_KEY, String.valueOf(postId));
        
        Post post = postMapper.selectById(postId);
        if (likeCount == null) {
            likeCount = String.valueOf(post.getLikeCount());
        }
        
        int newCount = Integer.parseInt(likeCount) + (isIncrement ? 1 : -1);
        hashOps.put(LIKE_KEY, String.valueOf(postId), String.valueOf(newCount));
        
        post.setLikeCount(newCount);
        postMapper.updateById(post);
    }

    // （Redis）辅助方法：获取当前点赞数
    private int getLikeCount(Long postId) {
        HashOperations<String,String,String> hashOps = RedisTemplate.opsForHash();
        String likeCount = hashOps.get(LIKE_KEY, String.valueOf(postId));
        return likeCount != null ? Integer.parseInt(likeCount) : 0;
    }

    /**
    * 收藏帖子
    * @param 
    * @return 
    */
    public int collectPost(Long postId){
        // 当前登录用户ID
        String currentUserId = jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken());
        // 检查是否已经存在收藏记录
        QueryWrapper<PostCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", currentUserId)
                    .eq("post_id", postId);
        PostCollect existingCollect = postCollectMapper.selectOne(queryWrapper);
        if (existingCollect != null) {
            // 如果已经存在记录且状态为0（已取消），则更新状态为1
            if (existingCollect.getStatus() == 0) {
                existingCollect.setStatus(1);
                postCollectMapper.updateById(existingCollect);
            }
        } else {
            // 如果不存在记录，创建新的收藏记录
            PostCollect postCollect = new PostCollect();
            postCollect.setUserId(Long.parseLong(currentUserId));
            postCollect.setPostId(postId);
            postCollect.setStatus(1);
            postCollectMapper.insert(postCollect);
        }
        // 帖子表收藏数+1
        Post post = postMapper.selectById(postId);
        post.setCollectingCount(post.getCollectingCount() + 1);
        postMapper.updateById(post);
        return 1;
    }

    /**
    * 取消收藏帖子
    * @param 
    * @return 
    */
    public int unCollectPost(Long postId){
        // 当前登录用户ID
        String currentUserId = jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken());
        // 查找现有的收藏记录
        QueryWrapper<PostCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", currentUserId)
                    .eq("post_id", postId);
        PostCollect existingCollect = postCollectMapper.selectOne(queryWrapper);
        if (existingCollect != null && existingCollect.getStatus() == 1) {
            // 更新状态为未收藏
            existingCollect.setStatus(0);
            postCollectMapper.updateById(existingCollect);
        }
        // 帖子表收藏数-1
        Post post = postMapper.selectById(postId);
        post.setCollectingCount(post.getCollectingCount() - 1);
        postMapper.updateById(post);
        return 1;
    }

}
