package com.qin.catcat.unite.popo.vo;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * @Description 帖子VO.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-21 20:48
 */
@Data
public class PostVO {
    // 帖子ID
    private Integer postId;
    private String userId;
    private String content;
    private LocalDateTime sendTime;
    private Integer likeCount;
    private Integer commentCount;
    private Integer collectCount;
    private List<String> picUrls;
    private String userName;
    private String userAvatar;
    private Boolean isLiked;
    private Boolean isCollected;
}