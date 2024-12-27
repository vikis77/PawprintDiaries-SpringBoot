package com.qin.catcat.unite.popo.entity;

import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * @Description 猫咪时间线事件实体类.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-27 21:18
 */
@Data
@TableName("cat_time_line_event")
public class CatTimeLineEvent {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 小猫ID
     */
    private Integer catId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容描述
     */
    private String description;

    /**
     * 事件时间 格式:yyyy-MM-dd
     */
    @TableField("date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;

    /**
     * 创建者
     */
    private Integer createUserId;

    /**
     * 是否删除：1是 0否
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

}
