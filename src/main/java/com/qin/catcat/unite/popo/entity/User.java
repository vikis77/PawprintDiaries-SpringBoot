package com.qin.catcat.unite.popo.entity;

import java.sql.Date;
import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

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
@TableName(value = "user")//mp的注解 指定数据库对应的表名
public class User {
    
    private Integer id;

    @TableId(value = "user_id",type = IdType.INPUT)////mp的注解 指定主键
    //用户ID 主键
    private String userId;
    //用户名
    private String username;
    //密码
    private String password;
    //昵称
    private String nickName;
    //邮箱
    private String email;
    //手机号
    private String phoneNumber;
    //生日
    private Date brithday;
    //地址
    private String address;
    //头像
    private String avatar;
    //用户角色
    private String role;
    //账号状态
    private Integer status;
    //创建时间
    private Timestamp createTime;
    //更新时间
    private Timestamp updateTime;
    //发帖数
    private Integer numberOfPost;
}
