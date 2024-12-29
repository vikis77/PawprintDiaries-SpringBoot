package com.qin.catcat.unite.popo.vo;

import java.util.Map;

import lombok.Data;

/**
 * @Description 更新个人信息VO
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-29 17:47
 */
@Data
public class UpdateProfileVO {
    // 文件名转换映射：key为源文件名，value为转换后的文件名
    private Map<String, String> fileNameConvertMap;
}
