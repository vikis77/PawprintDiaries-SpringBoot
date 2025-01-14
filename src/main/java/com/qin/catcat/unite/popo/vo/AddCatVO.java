package com.qin.catcat.unite.popo.vo;

import java.util.Map;

import lombok.Data;

/**
 * @Description 新增猫猫VO
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-29 17:47
 */
@Data
public class AddCatVO {
    // 文件名转换映射：key为源文件名，value为转换后的文件名
    private Map<String, String> fileNameConvertMap;
    // 小猫ID
    private Integer catId;
}
