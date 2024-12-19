package com.catcat.entity;

import lombok.Data;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * @Description 用户关注表实体类
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-14 18:35
 */
@Data
public class UserFollow {
    /**
     * 主键ID
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    
    /**
     * 关注人ID
     */
    private Long userId;
    
    /**
     * 被关注人ID
     */
    private Long followedUserId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    private Integer isDeleted;
} 