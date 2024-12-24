package com.qin.catcat.unite.popo.vo;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * 新增帖子VO
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-24 21:59
 */
@Data
public class AddPostVO {
    // 文件名转换映射：key为源文件名，value为转换后的文件名
    private Map<String, String> fileNameConvertMap;
}
