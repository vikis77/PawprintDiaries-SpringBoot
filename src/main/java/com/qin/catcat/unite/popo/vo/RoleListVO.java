package com.qin.catcat.unite.popo.vo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @Description 角色列表VO.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-15 17:54
 */
@Data
public class RoleListVO {
    //用户ID
    private Integer userId;
    //用户名
    private String userName;
    //昵称
    private String nickName;
    //头像
    private String avatar;
    //注册时间
    // @JsonSerialize(using = LocalDateTimeToTimestampSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerTime;
    //角色
    private String role;
}
