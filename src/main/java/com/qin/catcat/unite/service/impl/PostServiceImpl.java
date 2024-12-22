package com.qin.catcat.unite.service.impl;

import java.sql.Timestamp;
import java.util.Date;
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
import com.qin.catcat.unite.common.constant.Constant;
import com.qin.catcat.unite.common.utils.CacheUtils;
import com.qin.catcat.unite.common.utils.GeneratorIdUtil;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.common.utils.ThreadLocalUtil;
import com.qin.catcat.unite.common.utils.TokenHolder;
import com.qin.catcat.unite.exception.BusinessException;
import com.qin.catcat.unite.manage.PostManage;
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
import com.qin.catcat.unite.popo.vo.PostVO;
import com.qin.catcat.unite.popo.vo.SinglePostVO;
import com.qin.catcat.unite.service.PostService;
import com.qin.catcat.unite.service.QiniuService;
import com.qin.catcat.unite.service.UserService;
import com.qin.catcat.unite.popo.entity.EsPostIndex;
import com.qin.catcat.unite.repository.EsPostIndexRepository;

import io.jsonwebtoken.lang.Collections;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.com.lmax.disruptor.BusySpinWaitStrategy;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
@Slf4j
public class PostServiceImpl implements PostService{

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
    @Autowired CacheUtils cacheUtils;
    @Autowired 
    PostManage postManage;
    @Autowired 
    private EsPostIndexRepository esPostIndexRepository;

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
        post.setAuthorId(Integer.parseInt(userId));//设置作者ID（即用户本ID）
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
    * 根据帖子ID查询帖子
    * @param 
    * @return 
    */
    public SinglePostVO getPostByPostId(String postId){
        log.debug("Fetching post with id: {}", postId);
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", postId);
        queryWrapper.eq("is_deleted", 0); // 查询未删除的帖子
        // queryWrapper.eq("is_adopted", 1); // 查询通过审核的帖子
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
        if (post.getIsAdopted() == 0) {
            Integer authorId = post.getAuthorId();
            // 如果作者ID和当前用户ID不一致，则抛出异常
            if (!String.valueOf(authorId).equals(jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken()))) {
                throw new BusinessException("帖子未通过审核");
            }
            // 如果作者查看自己尚未通过审核的帖子，则抛出异常
            else{
                singlePostVO.setTitle(singlePostVO.getTitle() + "（帖子审核中）");
            }
        }
        // 根据作者ID查询作者昵称、作者头像
        User author = userMapper.selectById(post.getAuthorId());
        if (author != null) {
            singlePostVO.setAuthorNickname(author.getNickName());
            singlePostVO.setAuthorAvatar(author.getAvatar());
        }else{
            throw new RuntimeException("作者不存在");
        }
        // 根据帖子ID查询帖子的全部图片（post_pics表）
        List<PostPics> postPicsList = postPicsMapper.selectByPostId(Integer.parseInt(postId));
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
    * 根据发布时间分页查询前十条帖子（暂时不用）
    * @param 
    * @return 
    */
    // @Cacheable(value = "postForSendtime", key = "#page + '-' + #pageSize", cacheManager = "redisCacheManager")
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
    * @param firstTime 是否是用户第一次请求：true-使用通用缓存，false-使用个性化缓存
    * @return 帖子列表
    */
    public List<HomePostVO> getRandomWeightedPosts(int page, int pageSize, boolean firstTime) {
        try {
            // “首次登录”和“未登录”都使用默认值，即为热点帖子数据的缓存KEY因素
            String currentUserId = "0";
            // 第一次请求，不管用户是否登录，都使用通用缓存（固定的热点数据），不使用用户ID
            if (!firstTime && TokenHolder.getToken() != null) {
                currentUserId = jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken());
            }

            List<PostWeight> weightedPosts;
            String cacheKey = "";

            if (page == 1) {
                if (!firstTime) {
                    // 第一页且用户刷新：重新计算权重和随机排序
                    weightedPosts = postManage.calculateWeightedPosts();
                    if (weightedPosts.isEmpty()) {
                        return new ArrayList<>();
                    }
                    // 将第一页的排序结果缓存，供后续翻页使用
                    cacheKey = Constant.WEIGHTED_POSTS_KEY + currentUserId + ":" + System.currentTimeMillis();
                    postManage.cacheWeightedPosts(weightedPosts, cacheKey);
                } 
                // 第一页且是用户首次打开：使用通用缓存
                else {
                    // 获取最近一次的缓存key
                    String latestCacheKey = postManage.getLatestCacheKey(currentUserId);
                    // 热点首页过期，则重新计算权重和随机排序
                    if (latestCacheKey == null) {
                        postManage.initOrUpdateRandomWeightedPosts();
                        latestCacheKey = postManage.getLatestCacheKey(currentUserId);
                    }
                    // 获取缓存中的帖子ID列表
                    List<String> cachedPostIds = RedisTemplate.opsForList().range(latestCacheKey, 0, -1);
                    if (cachedPostIds == null || cachedPostIds.isEmpty()) {
                        return new ArrayList<>();
                    }
                    weightedPosts = postManage.rebuildWeightedPosts(cachedPostIds);
                }
            } else {
                // 非第一页：尝试获取最近的一次缓存结果
                String latestCacheKey = postManage.getLatestCacheKey(currentUserId);
                if (latestCacheKey == null) {
                    // 如果没有缓存，重新计算
                    weightedPosts = postManage.calculateWeightedPosts();
                    if (weightedPosts.isEmpty()) {
                        return new ArrayList<>();
                    }
                    // 缓存计算结果
                    cacheKey = Constant.WEIGHTED_POSTS_KEY + currentUserId + ":" + System.currentTimeMillis();
                    postManage.cacheWeightedPosts(weightedPosts, cacheKey);
                } else {
                    // 使用缓存的结果
                    List<String> cachedPostIds = RedisTemplate.opsForList().range(latestCacheKey, 0, -1);
                    log.info("从Redis缓存中获取数据，key: {}", latestCacheKey);
                    if (cachedPostIds == null || cachedPostIds.isEmpty()) {
                        return new ArrayList<>();
                    }
                    weightedPosts = postManage.rebuildWeightedPosts(cachedPostIds);
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
    @Transactional // 确保数据操作的原子性，数据在三个存储层（MySQL、Redis、ES）保持一致性
    public Boolean passApprove(Integer postId){
        // 1. 更新MySQL数据
        Post post = postMapper.selectById(postId);
        Integer userId = Integer.parseInt(jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken()));
        post.setApproveUserId(userId);
        post.setIsAdopted(1);
        postMapper.updateById(post);

        // 2. 更新Redis缓存
        // 清除相关的缓存
        String cacheKey = Constant.ALL_POSTS;
        cacheUtils.remove(cacheKey);
        
        // 3. 同步到ES
        // 创建ES文档对象
        EsPostIndex esPostIndex = new EsPostIndex();
        // 复制所有相同的字段
        BeanUtils.copyProperties(post, esPostIndex);
        // 特殊处理postId字段（因为类型不同）
        esPostIndex.setPostId(String.valueOf(post.getPostId()));
        
        // 保存到ES
        List<EsPostIndex> indexList = new ArrayList<>();
        indexList.add(esPostIndex);
        esPostIndexRepository.saveAll(indexList);
        
        log.info("帖子{}审核通过，数据已同步到MySQL、Redis和ES", postId);
        return true;
    }

    /**
    * 帖子审核拒绝通过
    * @param 
    * @return 
    */
    public Boolean refuseApprove(Integer postId){
        Post post = postMapper.selectById(postId);
        Integer userId = Integer.parseInt(jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken()));
        post.setApproveUserId(userId);
        post.setIsAdopted(2);
        postMapper.updateById(post);
        return true;
    }

    /**
    * 根据点赞数分页查询前十条帖子（暂时不用）
    * @param 
    * @return 
    */
    // @Cacheable(value = "postForLikecount", cacheManager = "redisCacheManager")
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
        List<PostPics> postPicsList = postPicsMapper.selectByPostId(Integer.parseInt(postId));
        List<String> imageFileNames = postPicsList.stream().map(PostPics::getPicture).collect(Collectors.toList());
        // 批量删除七牛云图片
        qiniuService.deleteFile(imageFileNames, "post_pics");

        // 删除帖子表
        postMapper.deleteById(Integer.parseInt(postId));

        // 获取帖子图片关联ID集合
        List<Integer> postPicsIds = postPicsList.stream().map(PostPics::getId).collect(Collectors.toList());
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
    public int likePost(Integer postId){
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
        postLike.setUserId(Integer.parseInt(currentUserId));
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
    public int unlikePost(Integer postId){
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
    private void updateLikeCount(Integer postId, boolean isIncrement) {
        HashOperations<String,String,String> hashOps = RedisTemplate.opsForHash();
        String likeCount = hashOps.get(Constant.LIKE_KEY, String.valueOf(postId));
        
        Post post = postMapper.selectById(postId);
        if (likeCount == null) {
            likeCount = String.valueOf(post.getLikeCount());
        }
        
        int newCount = Integer.parseInt(likeCount) + (isIncrement ? 1 : -1);
        hashOps.put(Constant.LIKE_KEY, String.valueOf(postId), String.valueOf(newCount));
        
        post.setLikeCount(newCount);
        postMapper.updateById(post);
    }

    // （Redis）辅助方法：获取当前点赞数
    private int getLikeCount(Integer postId) {
        HashOperations<String,String,String> hashOps = RedisTemplate.opsForHash();
        String likeCount = hashOps.get(Constant.LIKE_KEY, String.valueOf(postId));
        return likeCount != null ? Integer.parseInt(likeCount) : 0;
    }

    /**
    * 收藏帖子
    * @param 
    * @return 
    */
    public int collectPost(Integer postId){
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
            postCollect.setUserId(Integer.parseInt(currentUserId));
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
    public int unCollectPost(Integer postId){
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

    /**
     * 获取热门帖子
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<PostVO> getHotPosts(int pageNum, int pageSize) {
        String cacheKey = Constant.HOT_POSTS_CACHE_KEY + pageNum + ":" + pageSize;
        
        return (List<PostVO>) cacheUtils.getWithMultiLevel(cacheKey, List.class, () -> {
            // 从数据库加载热门帖子
            Page<Post> page = new Page<>(pageNum, pageSize);
            QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("like_count", "send_time");
            
            IPage<Post> postPage = postMapper.selectPage(page, queryWrapper);
            return convertToPostVOList(postPage.getRecords());
        });
    }

    /**
     * 更新帖子点赞数
     */
    private void updatePostLikeCount(Integer postId, String likeCount, boolean isIncrement) {
        // 更新Redis缓存
        HashOperations<String,String,String> hashOps = RedisTemplate.opsForHash();
        
        Post post = postMapper.selectById(postId);
        if (likeCount == null) {
            likeCount = String.valueOf(post.getLikeCount());
        }
        
        int newCount = Integer.parseInt(likeCount) + (isIncrement ? 1 : -1);
        
        // 使用多级缓存更新点赞数
        String cacheKey = "post_like:" + postId;
        cacheUtils.put(cacheKey, newCount);
        
        // 同时更新Redis的hash结构
        hashOps.put(Constant.LIKE_KEY, String.valueOf(postId), String.valueOf(newCount));
        
        post.setLikeCount(newCount);
        postMapper.updateById(post);
        
        // 清除相关的热门帖子缓存
        cacheUtils.remove(Constant.HOT_POSTS_CACHE_KEY + "*");
    }

    private List<PostVO> convertToPostVOList(List<Post> posts) {
        return posts.stream().map(post -> {
            PostVO vo = new PostVO();
            BeanUtils.copyProperties(post, vo);
            
            // 获取作者信息
            User author = userMapper.selectById(post.getAuthorId());
            if (author != null) {
                vo.setUserName(author.getNickName());
                vo.setUserAvatar(author.getAvatar());
            }
            
            // 获取图片列表
            List<PostPics> postPics = postPicsMapper.selectByPostId(post.getPostId());
            vo.setPicUrls(postPics.stream()
                .map(PostPics::getPicture)
                .collect(Collectors.toList()));
            
            return vo;
        }).collect(Collectors.toList());
    }

    /**
    * 获取同步ES的全部帖子数据 
    */
    @Override
    public List<EsPostIndex> selectEsPostIndexByPage(int pageNum, int pageSize) {
        // 构建查询条件
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0)  // 只同步未删除的帖子
                  .eq("is_adopted", 1)   // 只同步已审核的帖子
                  .orderByAsc("id");     // 按ID升序排序，确保分页顺序一致
        
        // 分页查询
        Page<Post> page = new Page<>(pageNum + 1, pageSize); // pageNum从0开始，所以这里要+1
        IPage<Post> postPage = postMapper.selectPage(page, queryWrapper);
        List<Post> posts = postPage.getRecords();
        
        log.info("查询到待同步数据 - 当前页：{}，每页大小：{}，本页数据量：{}，总数据量：{}", 
                pageNum + 1, pageSize, posts.size(), postPage.getTotal());
        
        // 转换并返回结果
        List<EsPostIndex> esPostIndexList = posts.stream().map(post -> {
            EsPostIndex esPostIndex = new EsPostIndex();
            // 复制所有相同的字段
            BeanUtils.copyProperties(post, esPostIndex);
            // 特殊处理postId字段（因为类型不同）
            esPostIndex.setPostId(String.valueOf(post.getPostId()));
            // 设置ES文档ID，避免重复
            esPostIndex.setId(String.valueOf(post.getPostId())); 
            
            log.debug("转换帖子数据 - postId: {}, title: {}", post.getPostId(), post.getTitle());
            return esPostIndex;
        }).collect(Collectors.toList());

        log.info("完成数据转换，转换后数据量：{}", esPostIndexList.size());
        return esPostIndexList;
    }

    private EsPostIndex convertToEsPostIndex(Post post) {
        if (post == null) {
            return null;
        }
        EsPostIndex esPostIndex = new EsPostIndex();
        BeanUtils.copyProperties(post, esPostIndex);
        // 设置ID
        esPostIndex.setId(post.getPostId().toString());
        esPostIndex.setPostId(post.getPostId().toString());
        // 转换日期
        if (post.getSendTime() != null) {
            esPostIndex.setSendTime(new Date(post.getSendTime().getTime()));
        }
        if (post.getUpdateTime() != null) {
            esPostIndex.setUpdateTime(new Date(post.getUpdateTime().getTime()));
        }
        return esPostIndex;
    }

}
