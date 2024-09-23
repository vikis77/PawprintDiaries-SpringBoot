package com.qin.catcat.unite.popo.vo;

import lombok.Data;

/* 响应给前端的首页展示的帖子信息 */
@Data
public class HomePostVO {
    // 帖子ID
    private Long postId;
    // 作者ID
    private Long authorId;
    // 作者昵称
    private String authorNickname;
    // 帖子作者头像
    private String authorAvatar;
    // 帖子标题
    private String title;
    // 帖子封面图片
    private String coverPicture;
    // 帖子点赞数
    private Integer likeCount;

}
