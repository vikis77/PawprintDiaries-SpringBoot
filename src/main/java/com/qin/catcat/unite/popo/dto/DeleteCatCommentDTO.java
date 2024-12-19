package com.qin.catcat.unite.popo.dto;

import lombok.Data;

/**
 * @Description 删除小猫评论DTO
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-15 00:28
 */
@Data
public class DeleteCatCommentDTO {
    // 评论ID
    private Integer id;
    // 评论用户ID
    private Integer commentUserId;
} 