package com.qin.catcat.unite.controller;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.param.AddFundRecordParam;
import com.qin.catcat.unite.popo.dto.AddFundRecordDTO;
import com.qin.catcat.unite.popo.vo.DataAnalysisVO;
import com.qin.catcat.unite.popo.vo.FundCalculateVO;
import com.qin.catcat.unite.popo.vo.FundRecordVO;
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
@Tag(name = "数据分析接口")
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

    @Operation(summary = "添加或更新资金记录")
    @HasPermission("system:fund:add")
    @PostMapping("/fund/add")
    public Result<?> addOrUpdateFundRecord(@RequestBody AddFundRecordParam addFundRecordParam) {
        AddFundRecordDTO addFundRecordDTO = new AddFundRecordDTO();
        BeanUtils.copyProperties(addFundRecordParam, addFundRecordDTO);
        catAnalysisService.addOrUpdateFundRecord(addFundRecordDTO);
        return Result.success();
    }

    @Operation(summary = "获取资金记录")
    @HasPermission("system:fund:list")
    @GetMapping("/fund/list")
    public Result<List<FundRecordVO>> getFundRecord(@RequestParam Integer type) {
        return Result.success(catAnalysisService.getFundRecord(type));
    }

    /**
     * @Description 删除资金记录
     * 根据资金记录ID删除对应的资金记录
     */
    @Operation(summary = "删除资金记录")
    @HasPermission("system:fund:delete") 
    @DeleteMapping("/fund/delete")
    public Result<?> deleteFundRecord(@RequestParam Integer id) {
        catAnalysisService.deleteFundRecord(id);
        return Result.success();
    }

    /**
     * @Description 计算资金统计数据
     * 根据传入的类型计算对应的资金统计数据：救助资金剩余、资金支出、资金收入
     * @param type 资金类型
     * @return 返回资金统计数据
     */
    @Operation(summary = "计算资金统计数据")
    @HasPermission("system:fund:calculate")
    @GetMapping("/fundCalculate")
    public Result<List<FundCalculateVO>> calculateFund(@RequestParam String type) {
        return Result.success(catAnalysisService.calculateFund(type));
    }
} 