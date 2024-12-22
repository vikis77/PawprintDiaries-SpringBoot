package com.qin.catcat.unite.common.constant;

import lombok.Data;

/**
 * @Description 常量类.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-21 22:43
 */
@Data
public class Constant {
    /* 
     * 缓存预热相关key
     */
    public static final String ALL_POSTS = "All_Posts"; // 全部帖子
    public static final String HOT_FIRST_TIME_POST_LIST = "Hot_FirstTime_PostList"; // 热点首次打开展示的帖子
    public static final String HOT_FIRST_TIME_CAT_LIST = "Hot_FirstTime_CatList"; // 热点首次打开展示的猫猫
    public static final String HOT_FIRST_TIME_COORDINATE_LIST = "Hot_FirstTime_CoordinateList"; // 热点首次打开展示的坐标
    public static final String HOT_FIRST_TIME_CAT_DATA_ANALYSIS = "Hot_FirstTime_Cat_Data_Analysis"; // 热点首次打开小猫数据分析
    public static final String PENDING_POST_LIST = "Pending_PostList"; // 待审核的帖子
    public static final String PENDING_CAT_LIST = "Pending_CatList"; // 待审核的猫猫
    public static final String PENDING_COORDINATE_LIST = "Pending_CoordinateList"; // 待审核的坐标
    public static final String PENDING_COMMENT_LIST = "Pending_CommentList"; // 待审核的评论

    /* 
     * 权重帖子相关常量
     */
    public static final String LIKE_KEY = "post_likes"; // 帖子点赞key 暂时不使用
    public static final String WEIGHTED_POSTS_KEY = "weighted_posts:";  // Redis中权重帖子的key前缀
    public static final long CACHE_EXPIRE_SECONDS = 300;  // 缓存过期时间：5分钟
    public static final String HOT_POSTS_CACHE_KEY = "hot_posts:"; // 暂时不使用

    // 缓存劝降相关key前缀
    public static final String PERMISSION_KEY_PREFIX = "permission:";
    public static final String SUB_PERMISSION_KEY_PREFIX = "permission:sub:";
}

