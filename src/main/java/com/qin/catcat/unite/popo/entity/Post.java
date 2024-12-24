package com.qin.catcat.unite.popo.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* 
 * 帖子表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName(value = "post")
public class Post {
    //主键ID
    @TableId(value = "id",type = IdType.AUTO)
    private Integer postId;
    //标题
    private String title;
    //文章
    private String article;
    //作者ID
    private Integer authorId;
    //点赞数
    private Integer likeCount;
    // 收藏数
    private Integer collectingCount;
    //评论数
    private Integer commentCount;
    //发帖时间
    @JsonProperty("send_time")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime sendTime;
    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
    //首页图片地址
    private String coverPicture;
    // 是否删除：0否 1是
    private Integer isDeleted;
    // 审核状态：0待审核 1通过 2不通过
    private Integer isAdopted;
    // 审核人ID
    private Integer approveUserId;
}