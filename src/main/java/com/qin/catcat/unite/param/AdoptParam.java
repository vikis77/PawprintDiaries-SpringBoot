package com.qin.catcat.unite.param;

import lombok.Data;

/**
 * @Description 领养入参
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-19 14:38
 */
@Data
public class AdoptParam {
    // 小猫名字
    private String catName;
    // 领养人姓名
    private String name;
    // 领养人班级
    private String schoolClass;
    // 领养人籍贯/住址
    private String origin;
    // 领养人联系方式
    private String phone;
    // 领养人微信
    private String wechat;
}
