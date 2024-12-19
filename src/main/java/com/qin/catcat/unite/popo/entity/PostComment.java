package com.qin.catcat.unite.popo.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* 
 * 帖子评论表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("post_comment")
@Builder
public class PostComment {
    //主键ID
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    //帖子ID
    private Integer postId;
    //评论类型：10小猫评论 20帖子评论
    private Integer type;
    //创建时间
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
    //评论状态：10待审核 20通过 30未通过
    private Integer status;
    //是否删除：0否 1是
    private Integer isDeleted;
    //是否置顶：0否 1是
    private Integer isTop;
    //评论内容
    private String commentContext;
    //评论点赞数
    private Integer likeCount;
    // 评论者ID
    private Integer commentUserId;
}
