package com.qin.catcat.unite.popo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* 
 * 评论父子表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("comment_relationship")
public class CommentRelationship {
    //主键ID
    @TableId(value = "id",type = IdType.INPUT)
    private Long id;
    //子评论ID
    private Long sonId;
    //父评论ID
    private Long fatherId;
}
