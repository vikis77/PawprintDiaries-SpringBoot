package com.qin.catcat.unite.param;

import lombok.Data;

/**
 * @Description 拒绝通过帖子入参.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-12 21:11
 */
@Data
public class RefuseApproveParam {
    // 帖子ID
    private Long postId;
}
