package com.qin.catcat.unite.popo.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 帖子收藏实体类
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-14 20:54
 */
@Data
@TableName("post_favorite")
public class PostCollect {
    // 主键ID
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    // 用户ID
    private Integer userId;
    // 帖子ID
    private Integer postId;
    // 收藏状态 1:收藏 0:取消收藏
    private Integer status;
    // 创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    // 更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
} 