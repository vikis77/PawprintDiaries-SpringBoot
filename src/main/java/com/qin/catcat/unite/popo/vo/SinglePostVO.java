package com.qin.catcat.unite.popo.vo;

import java.util.List;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qin.catcat.unite.popo.entity.PostPics;

import lombok.Data;

import java.sql.Timestamp;

/* 
 * 响应给前端的单个帖子的全部信息
 */
@Data
public class SinglePostVO {
    //主键ID
    @TableId(value = "post_id",type = IdType.INPUT)
    private Long postId;
    //标题
    private String title;
    //文章
    private String article;
    //作者ID
    private Long authorId;
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
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSS")
    private Timestamp sendTime;
    //更新时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSS")
    private Timestamp updateTime;
    //帖子全部图片集合
    private List<PostPics> images;
}
