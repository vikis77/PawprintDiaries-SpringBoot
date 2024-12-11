package com.qin.catcat.unite.service.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
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
import com.qin.catcat.unite.common.utils.GeneratorIdUtil;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.common.utils.TokenHolder;
// import com.qin.catcat.unite.config.RabbitMQConfig;
import com.qin.catcat.unite.mapper.PostMapper;
import com.qin.catcat.unite.mapper.PostPicsMapper;
import com.qin.catcat.unite.mapper.UserMapper;
import com.qin.catcat.unite.popo.dto.PostDTO;
import com.qin.catcat.unite.popo.entity.Post;
import com.qin.catcat.unite.popo.entity.PostPics;
import com.qin.catcat.unite.popo.entity.User;
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

    @Autowired UserMapper userMapper;
    @Autowired PostMapper postMapper;
    @Autowired PostPicsMapper postPicsMapper;
    @Autowired StringRedisTemplate RedisTemplate; //使用redis
    @Autowired RabbitTemplate rabbitTemplate; //使用rabbitmq
    @Autowired JwtTokenProvider jwtTokenProvider;
    @Autowired UserService userService;
    @Autowired GeneratorIdUtil generatorIdUtil;
    @Autowired QiniuService qiniuService;

    /**
    * 新增帖子
    * @param 
    * @return 
    */
    public Boolean add(PostDTO postDto){
        Post post = new Post();
        // BeanUtils.copyProperties(postDto, post);//属性拷贝DTO to entity

        String userId = jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken());
        String userNickname = userService.getNicknameFromId(userId); //根据用户ID查询用户昵称

        // 插入帖子基本信息
        post.setPostId(Long.parseLong(generatorIdUtil.GeneratorRandomId()));//设置帖子ID
        post.setTitle(postDto.getTitle()); // 设置标题
        post.setArticle(postDto.getArticle()); // 设置文章
        post.setAuthorId(Long.parseLong(userId));//设置作者ID（即用户本ID）
        post.setLikeCount(0);//设置点赞数 初始化0
        post.setCollectingCount(0); // 设置收藏数 初始化0
        post.setCommentCount(0);//设置评论数 初始化0
        post.setSendTime(Timestamp.from(Instant.now()));//设置发帖时间
        post.setUpdateTime(Timestamp.from(Instant.now()));//设置更新时间
        post.setCoverPicture(postDto.getPictrueList().get(0));//设置封面(默认为第一张图片)
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
        List<Post> posts = postMapper.selectAllPost();
        return posts;
    }

    /**
    * 根据帖子ID查询帖子全部图片
    * @param 
    * @return 
    */
    public SinglePostVO getPostByPostId(String postId){
        // 查询帖基本信息
        Post post = postMapper.selectById(postId);

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

        return singlePostVO;
    }

    /**
    * 根据发布时间分页查询前十条帖子
    * @param 
    * @return 
    */
    @Cacheable(value = "postForSendtime")
    public IPage<HomePostVO> getPostBySendtime(int page, int pageSize) {
        Page<HomePostVO> postObj = new Page<>(page,pageSize);
        return postMapper.selectPostsBySendtime(postObj);
    }

    /**
    * 根据点赞数分页查询前十条帖子
    * @param 
    * @return 
    */
    @Cacheable(value = "postForLikecount")
    public IPage<Post> getPostByLikecount(int page,int pageSize){
        Page<Post> postObj = new Page<>(page, pageSize);
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("like_count");
        IPage<Post> posts = postMapper.selectPage(postObj, queryWrapper);
        return posts;
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
    * 判断是否有权限��除
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
        qiniuService.deleteFile(imageFileNames);


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
    @Transactional //使用事务,保证 数据库和 Redis 数据的一致性
    public int likePost(String postId){
        // 使用 RedisTemplate 的 HashOperations 进行操作
        HashOperations<String,String,String> hashOps =  RedisTemplate.opsForHash();
        String likeCount = hashOps.get(LIKE_KEY, postId); //获取redis中点赞数

        // 如果 Redis 中没有记录，查询数据库获取点赞数
        if (likeCount == null) {
            // 从数据库中获取点赞数
            Post post = postMapper.selectById(postId);
            likeCount = String.valueOf(post.getLikeCount());
        }

        // 更新 Redis 中的点赞数
        hashOps.put(LIKE_KEY, postId, String.valueOf(Integer.parseInt(likeCount)+1)); //点赞数+1 参数说明：键、字段、值

        // 发送消息到消息队列，持久化到数据库
        // rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME,postId);

        // 返回点赞数
        return Integer.parseInt(likeCount) + 1;
    }

    /**
    * 根据帖子ID取消点赞
    * @param 
    * @return 
    */
    @Transactional //使用事务,保证 数据库和 Redis 数据的一致性
    public int unlikePost(String postId){
        HashOperations<String,String,String> hashOps =  RedisTemplate.opsForHash(); //使用redis
        hashOps.increment(LIKE_KEY, postId, -1); //点赞数-1 参数说明：key、field、value

        // 发送消息到消息队列，持久化到数据库
        // rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME,postId);

        // 返回点赞数
        return 1;
    }
}
