package com.qin.catcat.unite.popo.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * @Description 小猫领养申请记录
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-18 22:16
 */
@Data
@TableName("cat_adopt_apply_record")
public class CatAdoptApplyRecord {
    // 主键ID
    @TableId(type = IdType.AUTO)
    private Integer id;
    // 小猫ID
    private Integer catId;
    // 领养人姓名
    private String name;
    // 领养人班级
    private String schoolClass;
    // 领养人籍贯/住址
    private String origin;
    // 领养人联系方式
    private String phone;
    // 领养人微信
    private String wechat;
    // 领养人ID
    private Integer applyUserId;
    // 创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    // 更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    // 是否删除：0否 1是
    private Integer isDeleted;
}
