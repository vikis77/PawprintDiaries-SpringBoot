package com.qin.catcat.unite.popo.entity;

import java.io.Serializable;
import java.sql.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

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
    
    @TableId(value = "cat_id",type = IdType.INPUT)
    //ID 主键
    private Long catId;
    //猫名
    private String catname;
    //性别 1雄性 0雌性
    private Integer gender;
    //年龄 单位：月
    private Integer age;
    //生日
    private Date brithday;
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
}
