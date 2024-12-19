package com.qin.catcat.unite.popo.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @Description 添加小猫评论DTO.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-15 00:47
 */
@Data
public class AddCatCommentDTO {
    // 小猫ID
    private Integer catId;
    // 评论内容
    private String commentContext;
    // 评论用户ID
    private Integer commentUserId;
    // 评论状态 审核状态：10未审核 20通过 30不通过
    private Integer status;
    // 是否删除 0否 1是
    private Integer isDeleted;
    // 创建时间
    private LocalDateTime createTime;
    // 更新时间
    private LocalDateTime updateTime;
}

