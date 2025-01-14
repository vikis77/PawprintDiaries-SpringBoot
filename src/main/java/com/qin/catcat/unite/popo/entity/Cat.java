package com.qin.catcat.unite.popo.entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* 
 * 猫猫实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "cat")
public class Cat implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableId(value = "cat_id",type = IdType.AUTO)
    //ID 主键
    private Long catId;
    //猫名
    private String catname;
    //性别 1雄性 0雌性
    private Integer gender;
    //年龄 单位：月
    private Integer age;
    //生日
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime birthday;
    //头像
    private String avatar;
    //食物
    private String food;
    //忌讳
    private String taboo;
    //性格
    private String catCharacter;
    //健康状况
    private String healthStatus;
    //绝育情况
    private String sterilizationStatus;
    //疫苗接种情况
    private String vaccinationStatus;
    //不良行为记录
    private String badRecord;
    //区域
    private String area;
    //品种
    private String breed;
    //撸猫指南
    private String catGuide;
    //创建时间
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    // 是否删除
    private Integer isDeleted;
    // 热度
    private Integer trending;
    // 点赞数
    private Integer likeCount;
    // 是否已被领养
    private Integer isAdopted;
}
