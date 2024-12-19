package com.qin.catcat.unite.popo.dto;

import lombok.Data;

/**
 * @Description 审核小猫评论DTO
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-15 00:28
 */
@Data
public class AuditCatCommentDTO {
    // 评论ID
    private Integer id;
    // 审核员ID
    private Integer auditUserId;
    // 审核备注
    private String auditRemark;
} 