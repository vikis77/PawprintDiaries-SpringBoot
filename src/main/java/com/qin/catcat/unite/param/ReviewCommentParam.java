package com.qin.catcat.unite.param;

import lombok.Data;

/**
 * @Description 审核评论入参.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-15 16:52
 */
@Data
public class ReviewCommentParam {
    /**
     * 评论ID
     */
    private Integer commentId;
    /**
     * 评论类型：10小猫评论 20帖子评论
     */
    private Integer type;
    /**
     * 审核操作：approve 通过 reject 拒绝
     */
    private String action;
}
