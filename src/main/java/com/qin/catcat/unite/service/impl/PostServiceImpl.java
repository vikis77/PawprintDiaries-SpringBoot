package com.qin.catcat.unite.service.impl;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qin.catcat.unite.config.RabbitMQConfig;
import com.qin.catcat.unite.mapper.PostMapper;
import com.qin.catcat.unite.mapper.PostPicsMapper;
import com.qin.catcat.unite.mapper.UserMapper;
import com.qin.catcat.unite.popo.entity.Post;
import com.qin.catcat.unite.popo.entity.PostPics;
import com.qin.catcat.unite.popo.entity.User;
import com.qin.catcat.unite.popo.vo.HomePostVO;
import com.qin.catcat.unite.popo.vo.SinglePostVO;
import com.qin.catcat.unite.service.PostService;

@Service
public class PostServiceImpl implements PostService{

    private static final String LIKE_KEY = "post_likes";

    @Autowired UserMapper userMapper;
    @Autowired PostMapper postMapper;
    @Autowired PostPicsMapper postPicsMapper;
    @Autowired StringRedisTemplate RedisTemplate; //使用redis
    @Autowired RabbitTemplate rabbitTemplate; //使用rabbitmq

    /**
    * 新增帖子
    * @param 
    * @return 
    */
    public Boolean add(Post post){
        int siginal = postMapper.insert(post);
        if(siginal!=1){
            //TODO throw new 
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
        // 查询帖子基本信息
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
    public IPage<HomePostVO> getPostBySendtime(int page,int pageSize){
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
    * 根据帖子ID删除帖子
    * @param 
    * @return 
    */
    public Boolean delete(String postId){
        int signal = postMapper.deleteById(Long.parseLong(postId));
        if(signal!=1){
            //TODO throw new
            return false;
        }else{
            return true;
        }
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
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME,postId);

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
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME,postId);

        // 返回点赞数
        return 1;
    }
}
