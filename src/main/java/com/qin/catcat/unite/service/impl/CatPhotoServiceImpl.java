package com.qin.catcat.unite.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qin.catcat.unite.mapper.CatPicsMapper;
import com.qin.catcat.unite.popo.entity.CatPics;
import com.qin.catcat.unite.service.CatPhotoService;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 猫咪图片服务实现类
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-04 17:18
 */
@Service
@Slf4j
public class CatPhotoServiceImpl implements CatPhotoService {
    
    @Autowired
    private CatPicsMapper catPicsMapper;
    
    @Override
    public String uploadPhoto(Long catId, MultipartFile file) {
        // TODO: 实现照片上传逻辑
        return "photo_url";
    }
    
    @Override
    public List<CatPics> selectPhotoById(String catId, int page, int size) {
        Page<CatPics> pageObj = new Page<>(page, size);
        QueryWrapper<CatPics> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cat_id", catId)
                   .orderByDesc("create_time");
        
        IPage<CatPics> result = catPicsMapper.selectPage(pageObj, queryWrapper);
        log.info("根据猫猫ID:{} 查找猫猫图片完成, 返回{}张图片", catId, result.getRecords().size());
        return result.getRecords();
    }
} 