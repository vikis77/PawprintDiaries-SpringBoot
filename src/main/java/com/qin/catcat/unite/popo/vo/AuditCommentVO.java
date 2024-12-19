package com.qin.catcat.unite.popo.vo;

import java.util.List;

import com.qin.catcat.unite.popo.entity.CatComment;
import com.qin.catcat.unite.popo.entity.PostComment;

import lombok.Data;

/**
 * @Description 审核评论VO.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-15 12:53
 */
@Data
public class AuditCommentVO {
    /**
     * 小猫待审核评论
     */
    private List<CatCommentVO> catComments;
    /**
     * 帖子待审核评论
     */
    private List<PostCommentVO> postComments;
}
