package com.qin.catcat.unite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.common.utils.TokenHolder;
import com.qin.catcat.unite.popo.dto.DonateDTO;
import com.qin.catcat.unite.popo.entity.Donate;
import com.qin.catcat.unite.security.HasPermission;
import com.qin.catcat.unite.service.DonateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "捐赠接口")
@RestController
@RequestMapping("/api/donate")
@Slf4j
public class DonateController {
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private DonateService donateService;

    /**
     * 新增捐赠记录
     * @param donateDTO 捐赠信息数据传输对象
     * @return 操作结果
     */
    @Operation(summary = "新增捐赠")
    @HasPermission("system:donate:add")
    @PostMapping("/add")
    public Result<?> addDonate(@RequestBody DonateDTO donateDTO) {
        String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
        log.info("用户{}请求新增捐赠", username);

        donateService.addDonate(donateDTO);
        return Result.success();
    }

    /**
     * 删除捐赠记录
     * @param id 捐赠记录ID
     * @return 操作结果
     */
    @Operation(summary = "删除捐赠")
    @HasPermission("system:donate:delete")
    @DeleteMapping("/delete")
    public Result<?> deleteDonate(@RequestParam(name = "id") Long id) {
        String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
        log.info("用户{}请求删除捐赠", username);

        donateService.deleteDonate(id);
        return Result.success();
    }

    /**
     * 更新捐赠记录
     * @param donateDTO 更新后的捐赠信息
     * @return 操作结果
     */
    @Operation(summary = "更新捐赠")
    @HasPermission("system:donate:edit")
    @PutMapping("/update")
    public Result<?> updateDonate(@RequestBody DonateDTO donateDTO) {
        String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
        log.info("用户{}请求更新捐赠", username);

        donateService.updateDonate(donateDTO);
        return Result.success();
    }

    /**
     * 分页获取捐赠记录
     * @param page 页码
     * @param limit 每页大小
     * @return 捐赠记录列表（分页）
     */
    @Operation(summary = "查询捐赠")
    @HasPermission("system:donate:view")
    @GetMapping("/list")
    public Result<IPage<Donate>> getDonate(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "limit", defaultValue = "10") Integer limit) {
        String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
        log.info("用户{}请求获取捐赠列表，第{}页，每页{}条", username, page, limit);

        IPage<Donate> donate = donateService.getDonate(page, limit);
        return Result.success(donate);
    }
}
