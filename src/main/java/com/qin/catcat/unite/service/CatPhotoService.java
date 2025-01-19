package com.qin.catcat.unite.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.qin.catcat.unite.popo.dto.AddCatPhotoDTO;
import com.qin.catcat.unite.popo.entity.CatPics;
import com.qin.catcat.unite.popo.vo.AddCatPhotoVO;

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
    AddCatPhotoVO uploadPhoto(AddCatPhotoDTO addCatPhotoDTO);
    /**
     * 新增图片（不改变原图片ID）
     */
    AddCatPhotoVO uploadPhoto(AddCatPhotoDTO addCatPhotoDTO, Integer originalPhotoId);
    /**
     * 删除图片
     */
    void deletePhoto(Integer catPhotoId);
    /**
     * 更新图片
     */
    AddCatPhotoVO updatePhoto(Integer originalPhotoId, AddCatPhotoDTO addCatPhotoDTO);
    /**
     * 根据猫猫ID查询图片
     */
    List<CatPics> selectPhotoById(String catId, int page, int size);
} 