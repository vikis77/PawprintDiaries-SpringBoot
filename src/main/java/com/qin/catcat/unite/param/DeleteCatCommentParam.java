package com.qin.catcat.unite.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description 删除小猫评论入参
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-15 00:28
 */
@Data
@Schema(description = "删除小猫评论入参")
public class DeleteCatCommentParam {
    
    @Schema(description = "评论ID")
    private Integer id;
} 