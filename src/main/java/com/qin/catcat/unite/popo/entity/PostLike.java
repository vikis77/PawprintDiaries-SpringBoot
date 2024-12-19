package com.qin.catcat.unite.popo.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 帖子点赞实体类
 */
@Data
@TableName("post_like")
public class PostLike {
    //主键ID
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    //用户ID
    private Integer userId;
    //帖子ID
    private Integer postId;
    // 点赞状态 1:点赞 0:取消点赞
    private Integer status;
    // 创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    // 更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
} 