package com.qin.catcat.unite.popo.entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * @Description 资金记录实体类
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-05 15:58
 */
@Data
@TableName("fund_record")
public class FundRecord {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id; // 主键ID
    
    @TableField("create_user_id")
    private Integer createUserId; // 创建者ID
    
    @TableField("date") 
    private Date date; // 记录时间
    
    @TableField("amount")
    private BigDecimal amount; // 金额
    
    @TableField("type")
    private Integer type; // 类型：1：收入 2：支出
    
    @TableField("description")
    private String description; // 描述
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime; // 创建时间
    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime; // 更新时间
    
    @TableField("is_deleted")
    private Integer isDeleted; // 是否删除：1是 0否
}
