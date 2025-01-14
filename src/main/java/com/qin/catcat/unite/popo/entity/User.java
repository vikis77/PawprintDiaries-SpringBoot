package com.qin.catcat.unite.popo.entity;

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

/**
* 用户实体类
*/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "user") // 指定对应的数据库表名
public class User {

    @TableId(value = "id", type = IdType.AUTO) // 指定主键,自增
    private Integer userId; // 主键ID
    
    private String username; // 用户名,不能为空,最大16字符
    
    private String password; // 密码,不能为空,最大64字符
    
    private String nickName; // 昵称,最大45字符
    
    private String email; // 邮箱,最大255字符
    
    private String phoneNumber; // 手机号,最大45字符
    
    private Date birthday; // 生日
    
    private String address; // 住址,最大45字符
    
    private String avatar; // 头像文件名,最大255字符
    
    private Integer status; // 状态(1:启用,0:禁用)
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime; // 创建时间,默认当前时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime; // 更新时间
    
    private Integer postCount; // 发帖数
    
    private Integer fansCount; // 粉丝数
    
    private Integer followCount; // 关注数
    
    private String signature; // 个性签名,最大50字符
    
    private Integer isDeleted; // 是否删除：0否 1是
}
