package com.qin.catcat.unite.popo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserLoginVO {
    private String token;
    private String userName;
}
