package com.qin.catcat.unite.param;

import java.util.List;
import lombok.Data;

/**
 * @Description 新增猫咪照片入参
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-19 16:02
 */
@Data
public class AddCatPhotoParam {
    // 图片集合
    // private List<String> pictrueList;
    private String pictrueName;
}
