package com.qin.catcat.unite.popo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("comment_like")
public class CommentLike {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 类型：10-小猫评论 20-帖子评论
     */
    private Integer type;
    
    /**
     * 小猫ID/帖子ID
     */
    private Integer targetId;
    
    /**
     * 点赞用户ID
     */
    private Integer userId;
    
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