package com.qin.catcat.unite.common.rabbitMq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.qin.catcat.unite.mapper.PostMapper;
import com.qin.catcat.unite.popo.entity.Post;

@Component
public class LikeMessageListener {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String LIKE_KEY = "post_likes";

    public void receiveMessage(String postId) {
        // 从Redis中读取点赞数
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash(); // 哈希操作
        String likeCountStr = hashOps.get(LIKE_KEY, postId); // 获取点赞数
        Integer likeCount = likeCountStr == null ? 0 : Integer.parseInt(likeCountStr); // 如果没有点赞数，就设置为0

        // 持久化到数据库
        Post postRecord = postMapper.selectById(postId); // 根据帖子ID查询帖子
        postRecord.setLikeCount(likeCount); // 更新点赞数
        postMapper.updateById(postRecord); // 更新数据库
    }
}
