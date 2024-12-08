package com.qin.catcat.unite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.popo.entity.PredictionResult;
import com.qin.catcat.unite.security.HasPermission;
import com.qin.catcat.unite.service.CatPredictionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;

/**
 * @Description 猫咪品种预测接口
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-08 21:20
 */
@Tag(name = "猫咪品种预测接口")
@RestController
@RequestMapping("/api/cat/prediction")
public class CatPredictionController {
    
    @Autowired
    private CatPredictionService catPredictionService;
    
    /**
     * 预测猫咪品种
     * @param image 猫咪照片
     * @return 预测结果
     * @throws IOException 文件读取异常
     */
    @Operation(summary = "预测猫咪品种")
    @HasPermission("system:cat:predict")
    @PostMapping("/predict")
    public Result<PredictionResult> predictCatBreed(@RequestParam("image") MultipartFile image) throws IOException {
        PredictionResult result = catPredictionService.predictCatBreed(image);
        return Result.success(result);
    }
} 