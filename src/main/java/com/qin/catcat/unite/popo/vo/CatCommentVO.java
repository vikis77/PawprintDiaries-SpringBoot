package com.qin.catcat.unite.popo.vo;

import com.qin.catcat.unite.popo.entity.CatComment;

import lombok.Data;

/**
 * @Description 小猫评论VO.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-15 13:24
 */
@Data
public class CatCommentVO extends CatComment{
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
    private Boolean liked;
}
