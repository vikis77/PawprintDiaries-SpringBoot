package com.qin.catcat.unite.popo.vo;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * @Description 新增猫咪照片出参
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-19 16:02
 */
@Data
public class AddCatPhotoVO {
    // 文件名转换映射：key为源文件名，value为转换后的文件名
    private Map<String, String> fileNameConvertMap;
}

