package com.qin.catcat.unite.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.qin.catcat.unite.popo.entity.CatPics;

/**
 * @Description 猫咪图片服务接口
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-04 17:20
 */
public interface CatPhotoService {
    /**
     * 上传图片
     */
    String uploadPhoto(Long catId, MultipartFile file);
    /**
     * 根据猫猫ID查询图片
     */
    List<CatPics> selectPhotoById(String catId, int page, int size);
} 