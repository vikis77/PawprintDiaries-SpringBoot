package com.qin.catcat.unite.popo.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 新增猫咪照片DTO
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-19 16:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCatPhotoDTO {
    // 猫咪ID
    private Integer catId;
    // 图片集合
    // private List<String> pictrueList;
    private String pictrueName;
}
