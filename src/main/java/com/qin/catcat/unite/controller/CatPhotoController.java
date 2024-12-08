package com.qin.catcat.unite.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.popo.entity.CatPics;
import com.qin.catcat.unite.service.CatPhotoService;
import com.qin.catcat.unite.security.HasPermission;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @Description 猫咪照片管理控制器
 *
 * @Author qrb
 * @Version 1.0
 * @Since 2024-12-04 17:21
 */
@Tag(name = "猫咪照片管理接口")
@RestController
@RequestMapping("/api/cat/photo")
public class CatPhotoController {
    
    @Autowired
    private CatPhotoService catPhotoService;
    
    @Operation(summary = "上传猫咪照片")
    @HasPermission("system:cat:photo:upload")
    @PostMapping("/{catId}")
    public Result<String> uploadPhoto(@PathVariable Long catId, @RequestParam("file") MultipartFile file) {
        String photoUrl = catPhotoService.uploadPhoto(catId, file);
        return Result.success(photoUrl);
    }
    
    @Operation(summary = "获取猫咪照片列表")
    @HasPermission("system:cat:photo:view")
    @GetMapping("/{catId}")
    public Result<List<CatPics>> getPhotos(@PathVariable String catId, 
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        return Result.success(catPhotoService.selectPhotoById(catId, page, size));
    }
} 