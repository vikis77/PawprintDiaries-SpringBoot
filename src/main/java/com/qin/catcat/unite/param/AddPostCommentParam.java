package com.qin.catcat.unite.param;

import lombok.Data;

/**
 * @Description 新增帖子评论入参.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-15 21:43
 */
@Data
public class AddPostCommentParam {
    /**
     * 评论内容
     */
    private String commentContext;
    /**
     * 帖子ID
     */
    private Integer postId;
    /**
     * 评论类型
     */
    private Integer type;
}
