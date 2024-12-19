package com.qin.catcat.unite.popo.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 小猫评论实体类
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-15 00:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("cat_comment")
@Schema(description = "小猫评论实体")
public class CatComment {
    
    @Schema(description = "评论ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    @Schema(description = "小猫ID")
    private Integer catId;
    
    @Schema(description = "评论内容")
    private String commentContext;
    
    @Schema(description = "评论用户ID")
    private Integer commentUserId;
    
    @Schema(description = "评论状态：10未审核 20通过 30不通过")
    private Integer status;
    
    @Schema(description = "是否删除：0否 1是")
    private Integer isDeleted;
    
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
    
    @Schema(description = "点赞数")
    private Integer likeCount;
    
    @Schema(description = "是否置顶：0否 1是")
    private Integer isTop;

    @Schema(description = "评论类型：10小猫评论 20帖子评论")
    private Integer type;
}