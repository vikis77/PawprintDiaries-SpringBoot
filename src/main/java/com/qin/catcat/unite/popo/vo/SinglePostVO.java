package com.qin.catcat.unite.popo.vo;

import java.util.List;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qin.catcat.unite.popo.entity.PostPics;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/* 
 * 响应给前端的单个帖子的全部信息
 */
@Data
public class SinglePostVO {
    //主键ID
    @TableId(value = "post_id",type = IdType.AUTO)
    private Integer postId;
    //标题
    private String title;
    //文章
    private String article;
    //作者ID
    private Integer authorId;
    //作者昵称
    private String authorNickname;
    //作者头像
    private String authorAvatar;
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
    @JsonProperty("update_time")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
    //帖子全部图片集合
    private List<PostPics> images;
    //是否点赞
    private boolean liked;
    //是否收藏
    private boolean collected;
    //是否关注
    private boolean followed;
}
