package com.qin.catcat.unite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.popo.vo.DataAnalysisVO;
import com.qin.catcat.unite.service.CatAnalysisService;
import com.qin.catcat.unite.security.HasPermission;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @Description 猫咪数据分析控制器
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-04 17:21
 */
@Tag(name = "猫咪数据分析接口")
@RestController
@RequestMapping("/api/cat/analysis")
public class CatAnalysisController {
    
    @Autowired
    private CatAnalysisService catAnalysisService;
    
    @Operation(summary = "获取猫咪数据分析")
    @HasPermission("system:cat:analysis:view")
    @GetMapping
    public Result<DataAnalysisVO> getAnalysis() {
        return Result.success(catAnalysisService.analysis());
    }
} 