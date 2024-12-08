package com.qin.catcat.unite.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.param.UploadCoordinateParam;
import com.qin.catcat.unite.popo.dto.CoordinateDTO;
import com.qin.catcat.unite.popo.entity.Coordinate;
import com.qin.catcat.unite.popo.vo.CoordinateVO;
import com.qin.catcat.unite.service.CatLocationService;
import com.qin.catcat.unite.security.HasPermission;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @Description 猫咪位置管理控制器
 *
 * @Author qrb
 * @Version 1.0
 * @Since 2024-12-04 17:21
 */
@Tag(name = "猫咪位置管理接口")
@RestController
@RequestMapping("/api/cat/location")
public class CatLocationController {
    
    @Autowired
    private CatLocationService catLocationService;
    
    @Operation(summary = "上传猫咪位置")
    @HasPermission("system:cat:location:upload")
    @PostMapping("/upload")
    public Result<Void> uploadLocation(@RequestBody UploadCoordinateParam uploadCoordinateParam) {
        catLocationService.addCoordinate(uploadCoordinateParam);
        return Result.success();
    }
    
    @Operation(summary = "获取所有猫咪最新位置")
    @HasPermission("system:cat:location:view")
    @GetMapping("/latest")
    public Result<List<CoordinateVO>> getLatestLocations() {
        return Result.success(catLocationService.selectCoordinate());
    }
    
    @Operation(summary = "获取单只猫咪历史位置")
    @HasPermission("system:cat:location:view")
    @GetMapping("/{catId}")
    public Result<IPage<CoordinateVO>> getCatLocations(
            @PathVariable Long catId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(catLocationService.selectCoordinateByCatId(catId, page, size));
    }
    
    @Operation(summary = "按日期查询猫咪位置")
    @HasPermission("system:cat:location:view")
    @GetMapping("/date/{date}")
    public Result<List<CoordinateVO>> getLocationsByDate(@PathVariable String date) {
        return Result.success(catLocationService.selectCoordinateByDate(date));
    }
    
    @Operation(summary = "按日期和猫咪ID查询位置")
    @HasPermission("system:cat:location:view")
    @GetMapping("/date/{date}/cat/{catId}")
    public Result<List<CoordinateVO>> getLocationsByDateAndCat(
            @PathVariable String date,
            @PathVariable Long catId) {
        return Result.success(catLocationService.selectCoordinateByDateAndCatId(date, catId));
    }
} 