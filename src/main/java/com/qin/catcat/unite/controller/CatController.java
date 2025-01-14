package com.qin.catcat.unite.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.param.AddCatTimelineParam;
import com.qin.catcat.unite.param.AdoptParam;
import com.qin.catcat.unite.param.UpdateCatTimelineParam;
import com.qin.catcat.unite.popo.entity.Cat;
import com.qin.catcat.unite.popo.vo.AddCatVO;
import com.qin.catcat.unite.popo.vo.CatListVO;
import com.qin.catcat.unite.popo.vo.CatTimelineVO;
import com.qin.catcat.unite.popo.vo.UpdateCatVO;
import com.qin.catcat.unite.popo.dto.CatDTO;
import com.qin.catcat.unite.security.HasPermission;
import com.qin.catcat.unite.service.CatService;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description 猫咪基本信息管理控制器
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-04 17:21
 */

@Tag(name = "猫咪基本信息管理接口")
@RestController
@Slf4j
@RequestMapping("/api/cat")
public class CatController {
    
    @Autowired
    private CatService catService;
    // @Autowired
    // private MeterRegistry registry; // 监控指标
    
    @Operation(summary = "获取猫咪列表")
    @HasPermission("system:cat:view")
    @GetMapping("/list")
    public Result<List<CatListVO>> listCats() {
        // 开始计时
        // Timer.Sample sample = Timer.start(registry);
        // try {
            List<CatListVO> catListVOs = catService.CatList();
            return Result.success(catListVOs);
        // } finally {
            // 停止计时
            // sample.stop(registry.timer("api.response.time"));
            // 增加API调用次数
            // registry.counter("api.calls.total").increment();
        // }
        
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
    public Result<AddCatVO> createCat(@RequestBody CatDTO catDTO) {
        return Result.success(catService.createCat(catDTO));
    }
    
    @Operation(summary = "更新猫咪信息")
    @HasPermission("system:cat:edit")
    @PutMapping
    public Result<UpdateCatVO> updateCat(@RequestBody Cat cat) {
        return Result.success(catService.update(cat));
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

    /**
     * @Description 领养小猫（该接口尚未实现）
     * @param catId 小猫ID
     * @return 操作结果
     */
    @Operation(summary = "领养小猫")
    @HasPermission("system:cat:adopt")
    @PostMapping("/adopt/apply")
    public Result<Void> adoptCat(@RequestBody AdoptParam adoptParam) {
        // catService.adoptCat(adoptParam);
        log.info("领养小猫");
        return Result.success();
    }

    /**
     * 获取猫咪的时间线信息
     * @param catId 猫咪ID
     * @return 包含猫咪时间线信息的结果
     */
    @Operation(summary = "获取猫咪时间线")
    @GetMapping("/timeline/{catId}")
    @HasPermission("system:cat:timeline:view")
    public Result<List<CatTimelineVO>> getCatTimeline(@PathVariable Long catId) {
        return Result.success(catService.getCatTimeline(catId));
    }

    /**
     * 新增猫咪时间线
     */
    @Operation(summary = "新增猫咪时间线")
    @HasPermission("system:cat:timeline:add")
    @PostMapping("/timeline/add")
    public Result<Void> addCatTimeline(@RequestBody AddCatTimelineParam param) {
        catService.addCatTimeline(param);
        return Result.success();
    }

    /**
     * 更新猫咪时间线
     */
    @Operation(summary = "更新猫咪时间线")
    @HasPermission("system:cat:timeline:edit")
    @PutMapping("/timeline/update")
    public Result<Void> updateCatTimeline(@RequestBody UpdateCatTimelineParam param) {
        catService.updateCatTimeline(param);
        return Result.success();
    }

    /**
     * 删除猫咪时间线
     */
    @Operation(summary = "删除猫咪时间线")
    @HasPermission("system:cat:timeline:delete")
    @DeleteMapping("/timeline/delete/{id}")
    public Result<Void> deleteCatTimeline(@PathVariable Integer id) {
        catService.deleteCatTimeline(id);
        return Result.success();
    }
}
