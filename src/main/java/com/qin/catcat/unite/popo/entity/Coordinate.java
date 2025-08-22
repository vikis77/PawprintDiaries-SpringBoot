package com.qin.catcat.unite.popo.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;


/**
 * 坐标表
 */
@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@TableName("coordinate")
@lombok.Builder
public class Coordinate {
    /**
     * 主键
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    /**
     * 区域
     */
    private String area;
    /**
     * 猫猫ID
     */
    private Integer catId;
    /**
     * 描述
     */
    private String description;
    /**
     * 纬度
     */
    private Double latitude;
    /**
     * 经度
     */
    private Double longitude;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    /* 
     * 上传者
     */
    private String uploader;
    /**
     * 是否删除
     */
    private Integer isDeleted;
}