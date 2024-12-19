package com.qin.catcat.unite.popo.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    // 用户名
    private String username;
    // 邮箱
    private String email;
    // 密码
    private String password;
}
