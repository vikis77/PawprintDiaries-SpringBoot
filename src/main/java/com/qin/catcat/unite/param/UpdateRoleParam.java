package com.qin.catcat.unite.param;

import lombok.Data;

/**
 * @Description 更新角色入参
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-15 18:48
 */
@Data
public class UpdateRoleParam {
    // 用户ID
    private Integer userId;
    // 角色
    private String role;
}
