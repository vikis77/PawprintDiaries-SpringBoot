package com.qin.catcat.unite.manage;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qin.catcat.unite.common.constant.Constant;
import com.qin.catcat.unite.common.utils.CacheUtils;
import com.qin.catcat.unite.common.utils.ThreadLocalUtil;
import com.qin.catcat.unite.mapper.PostMapper;
import com.qin.catcat.unite.popo.entity.Post;
import com.qin.catcat.unite.popo.entity.PostWeight;
import com.qin.catcat.unite.service.PostService;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 帖子管理类.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-22 00:30
 */
@Component
@Slf4j
public class PostManage {
    @Autowired 
    StringRedisTemplate RedisTemplate;
    @Autowired
    PostMapper postMapper;
    @Autowired
    CacheUtils cacheUtils;
    @Autowired
    private ObjectMapper objectMapper;

    /** 
    * 权重随机推送帖子（设置或更新首页热点帖子缓存）：
    * 
    */
    public void initOrUpdateRandomWeightedPosts() {
        // 固定热点首页帖子对应的用户ID为0
        String currentUserId = "0";
        // 重新计算权重和随机排序
        List<PostWeight> weightedPosts = calculateWeightedPosts();
        // 构建缓存key
        String cacheKey = Constant.WEIGHTED_POSTS_KEY + currentUserId + ":" + System.currentTimeMillis();
        // 缓存权重排序结果
        cacheWeightedPosts(weightedPosts, cacheKey);
    }

    /**
    * 工具方法：获取最近的缓存key
    */
    public String getLatestCacheKey(String userId) {
        String pattern = Constant.WEIGHTED_POSTS_KEY + userId + ":*";
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
    * 工具方法：根据缓存的ID列表重建权重帖子列表
    */
    public List<PostWeight> rebuildWeightedPosts(List<String> cachedPostIds) {
        return cachedPostIds.stream()
            .map(id -> {
                Post post = postMapper.selectById(Integer.parseInt(id));
                return post != null ? new PostWeight(post, 1.0) : null;
            })
            .filter(pw -> pw != null)
            .collect(Collectors.toList());
    }

    /**
    * 工具方法：计算带权重的帖子列表
    */
    public List<PostWeight> calculateWeightedPosts() {
        List<Post> allPosts = queryAllPosts();
        // 清除总页数
        ThreadLocalUtil.clearTotalPages();
        // 计算总页数
        Integer totalPages = allPosts.size() / 10 + 1;
        // 设置总页数
        ThreadLocalUtil.setTotalPages(totalPages);
        
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
    * 工具方法：缓存权重排序结果
    */
    public void cacheWeightedPosts(List<PostWeight> weightedPosts, String cacheKey) {
        List<String> postIds = weightedPosts.stream()
            .map(pw -> String.valueOf(pw.getPost().getPostId()))
            .collect(Collectors.toList());
        
        if (!postIds.isEmpty()) {
            // 存入新的排序结果
            RedisTemplate.opsForList().rightPushAll(cacheKey, postIds);
            RedisTemplate.expire(cacheKey, Constant.CACHE_EXPIRE_SECONDS, java.util.concurrent.TimeUnit.SECONDS);
        }
    }

    /**
    * 查询全部帖子
    */
    @SuppressWarnings("unchecked")
    public List<Post> queryAllPosts() {
        try {
            // 尝试从缓存获取数据
            Object cachedData = cacheUtils.getWithMultiLevel(Constant.ALL_POSTS, List.class, () -> {
                // 如果从缓存中获取数据失败，则从数据库查询
                QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("is_deleted", 0)
                    .eq("is_adopted", 1);
                List<Post> posts = postMapper.selectList(queryWrapper);
                return posts;
            });

            // 如果缓存中获取的数据是List类型，则需要进行类型转换
            if (cachedData instanceof List<?>) {
                List<?> dataList = (List<?>) cachedData;
                if (!dataList.isEmpty()) {
                    // 如果缓存中获取的数据是LinkedHashMap类型，则需要进行类型转换
                    if (dataList.get(0) instanceof LinkedHashMap) {
                        // 需要进行类型转换
                        return dataList.stream()
                            .map(item -> {
                                Post post = new Post();
                                BeanUtils.copyProperties(objectMapper.convertValue(item, Post.class), post);
                                return post;
                            })
                            .collect(Collectors.toList());
                    } else if (dataList.get(0) instanceof Post) {
                        // 已经是正确的类型
                        return (List<Post>) dataList;
                    }
                }
            }
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("查询帖子失败: ", e);
            return new ArrayList<>();
        }
    }
}
