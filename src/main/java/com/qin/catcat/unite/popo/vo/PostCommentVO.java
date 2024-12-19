package com.qin.catcat.unite.popo.vo;

import com.qin.catcat.unite.popo.entity.PostComment;

import lombok.Data;

/**
 * @Description 帖子评论VO.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-15 13:26
 */
@Data
public class PostCommentVO extends PostComment{
    /**
     * 评论者头像
     */
    private String avatar;
    /**
     * 评论者昵称
     */
    private String nickName;
    /**
     * 是否点赞
     */
    private Boolean Liked;
}
