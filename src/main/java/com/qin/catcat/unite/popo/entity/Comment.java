package com.qin.catcat.unite.popo.entity;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* 
 * 评论表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("comment")
@Builder
public class Comment {
    //主键ID
    @TableId(value = "comment_id",type = IdType.INPUT)
    private Long commentId;
    //帖子ID
    private Long postId;
    //帖子类型
    private String commentType;
    //评论内容
    private String commentContext;
    //评论者ID
    private Long commentatorId;
    //评论时间
    private Timestamp commentTime;
    //评论点赞数
    private Integer likeCount;
    

}
