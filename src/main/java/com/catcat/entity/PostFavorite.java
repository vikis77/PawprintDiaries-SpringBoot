package com.catcat.entity;

import lombok.Data;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * @Description 帖子收藏表实体类
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-14 18:36
 */
@Data
public class PostFavorite {
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
     * 收藏用户ID
     */
    private Long userId;
    
    /**
     * 收藏状态 1:收藏 0:取消收藏
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