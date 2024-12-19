package com.qin.catcat.unite.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description 新增小猫评论入参
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-15 00:45
 */
@Data
@Schema(description = "新增小猫评论入参")
public class AddCatCommentParam {
    
    @Schema(description = "小猫ID")
    private Integer catId;
    
    @Schema(description = "评论内容")
    private String commentContext;
    
    @Schema(description = "是否置顶 0否 1是")
    private Integer isTop;
}
