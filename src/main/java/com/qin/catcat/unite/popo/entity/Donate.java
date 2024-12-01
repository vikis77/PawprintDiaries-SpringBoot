package com.qin.catcat.unite.popo.entity;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 捐赠表实体类
 */
@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@TableName(value = "Donate")
public class Donate {
    /**
     * 金额
     */
    private Long amount;
    /**
     * 猫猫ID
     */
    private Long catId;
    /**
     * 猫猫名字
     */
    private String catName;
    /**
     * 主键
     */
    @TableId(value = "id",type = IdType.INPUT)
    private Long id;
    /**
     * 留言
     */
    private String message;
    /**
     * 捐赠人名字
     */
    private String name;
    /**
     * 时间
     */
    private Timestamp time;
    // private Object the01J3Axatc0Nxseybnm0Yzre4F0;
}