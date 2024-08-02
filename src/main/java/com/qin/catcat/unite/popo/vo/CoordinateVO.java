package com.qin.catcat.unite.popo.vo;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// @AllArgsConstructor
// @NoArgsConstructor
@Data
public class CoordinateVO {
    /* 
     * id:主键
     */
    @TableId(value = "id")
    private long id;
    /**
     * 区域
     */
    private String area;
    /**
     * 猫猫ID
     */
    private Long catId;
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
    private Timestamp updateTime;

    /* 
     * 返回前端 加多一个猫名
     */
    @TableField(value  = "catname")
    private String catName;

    //全属性构造函数
    public CoordinateVO(Long id, Long catId, String catName , Double longitude, Double latitude,  Timestamp updateTime, String area, String description) {
        this.id = id;
        this.area = area;
        this.catId = catId;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.updateTime = updateTime;
        this.catName = catName;
    }
}
