package com.qin.catcat.unite.popo.vo;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qin.catcat.unite.popo.entity.Cat;

import lombok.Data;

/**
 * @Description 获取猫猫列表VO.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-14 22:09
 */
@Data
public class CatListVO{
    // 小猫ID   
    private Long catId;
    // 小猫名
    private String catname;
    // 性别 1雄性 0雌性
    private Integer gender;
    // 年龄 单位：月
    private Integer age;
    //生日
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
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
    //撸猫指南
    private String catGuide;
    //创建时间
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private LocalDateTime createTime;
    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private LocalDateTime updateTime;
    // 是否删除
    private Integer isDeleted;
    // 热度
    private Integer trending;
    // 点赞数
    private Integer likeCount;
    // 是否已被领养
    private Integer isAdopted;
    // 今日是否点过赞
    private Boolean isLikedToday;
}
