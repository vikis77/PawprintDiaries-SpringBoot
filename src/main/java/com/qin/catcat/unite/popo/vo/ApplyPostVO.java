package com.qin.catcat.unite.popo.vo;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

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
 * 审核帖子VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyPostVO {
    //主键ID
    @TableId(value = "post_id",type = IdType.INPUT)
    private Integer postId;
    //标题
    private String title;
    //文章
    private String article;
    //作者ID
    private Integer authorId;
    // 作者头像
    private String authorAvatar;
    //点赞数
    // private Integer likeCount;
    // 收藏数
    // private Integer collectingCount;
    //评论数
    // private Integer commentCount;
    //发帖时间
    @JsonProperty("send_time")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime sendTime;
    //更新时间
    @JsonProperty("update_time")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
    //首页图片地址
    // private String coverPicture;
    // 是否删除
    // private Integer isDeleted;
    // 是否通过审核
    // private Integer isAdopted;
    // 作者昵称
    private String authorNickname;
    // 图片文件名集合
    private List<String> images;
}