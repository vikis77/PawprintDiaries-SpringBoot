package com.qin.catcat.unite.popo.dto;

import java.sql.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.qin.catcat.unite.popo.entity.Cat;

import lombok.Data;

@Data
public class CatDTO{
    //猫名
    private String catname;
    //性别 1雄性 0雌性
    private Integer gender;
    //年龄 单位：月
    private Integer age;
    //生日
    private Date birthday;
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
    //撸猫指南
    private String catGuide;
    //品种
    private String breed;
}
