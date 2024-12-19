package com.qin.catcat.unite.popo.dto;

import lombok.Data;

@Data
public class UserLoginDTO {
    // 用户名/邮箱/手机号
    private String username;
    // 密码
    private String password;
}

