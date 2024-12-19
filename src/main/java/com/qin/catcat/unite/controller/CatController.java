package com.qin.catcat.unite.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.popo.entity.Cat;
import com.qin.catcat.unite.popo.vo.CatListVO;
import com.qin.catcat.unite.popo.dto.CatDTO;
import com.qin.catcat.unite.security.HasPermission;
import com.qin.catcat.unite.service.CatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @Description 猫咪基本信息管理控制器
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-04 17:21
 */

@Tag(name = "猫咪基本信息管理接口")
@RestController
@RequestMapping("/api/cat")
public class CatController {
    
    @Autowired
    private CatService catService;
    
    @Operation(summary = "获取猫咪列表")
    @HasPermission("system:cat:view")
    @GetMapping("/list")
    public Result<List<CatListVO>> listCats() {
        return Result.success(catService.CatList());
    }
    
    @Operation(summary = "获取猫咪详情")
    @HasPermission("system:cat:view")
    @GetMapping("/{catId}")
    public Result<Cat> getCatById(@PathVariable Long catId) {
        return Result.success(catService.getById(catId));
    }
    
    @Operation(summary = "新增猫咪")
    @HasPermission("system:cat:add")
    @PostMapping
    public Result<Void> createCat(@RequestBody CatDTO catDTO) {
        catService.createCat(catDTO);
        return Result.success();
    }
    
    @Operation(summary = "更新猫咪信息")
    @HasPermission("system:cat:edit")
    @PutMapping
    public Result<Void> updateCat(@RequestBody Cat cat) {
        catService.update(cat);
        return Result.success();
    }
    
    @Operation(summary = "删除猫咪")
    @HasPermission("system:cat:delete")
    @DeleteMapping("/{catId}")
    public Result<Void> deleteCat(@PathVariable Long catId) {
        catService.delete(catId);
        return Result.success();
    }

    // 点赞小猫
    @Operation(summary = "点赞小猫")
    @HasPermission("system:cat:like") 
    @PostMapping("/like/{catId}")
    public Result<Void> likeCat(@PathVariable Long catId) {
        catService.likeCat(catId);
        return Result.success();
    }
}
