package com.qin.catcat.unite.popo.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * @Description 短链接实体类
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-18 23:21
 */
@Data
@TableName("shot_link")
public class ShotLink {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 记录类型：10猫猫 20帖子
     */
    private Integer type;
    
    /**
     * 原始Url信息
     */
    private String originUrl;
    
    /**
     * 转换后的url
     */
    private String convertUrl;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 是否删除：0否 1是
     */
    private Integer isDeleted;
}
