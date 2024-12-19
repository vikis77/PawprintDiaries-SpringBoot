package com.catcat.entity;

import lombok.Data;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @Description 帖子点赞表实体类
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-14 18:36
 */
@Data
@TableName("post_like")
public class PostLike {
    /**
     * 主键ID
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    
    /**
     * 帖子ID
     */
    private Long postId;
    
    /**
     * 点赞用户ID
     */
    private Long userId;
    
    /**
     * 点赞状态 1:点赞 0:取消点赞
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 