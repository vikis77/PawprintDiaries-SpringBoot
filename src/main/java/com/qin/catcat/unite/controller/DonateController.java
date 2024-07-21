package com.qin.catcat.unite.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.common.utils.TokenHolder;
import com.qin.catcat.unite.popo.dto.DonateDTO;
import com.qin.catcat.unite.popo.entity.Donate;
import com.qin.catcat.unite.service.DonateService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/donate")
@Tag(name = "捐赠接口")
@Slf4j
public class DonateController {
    @Autowired JwtTokenProvider jwtTokenProvider;
    @Autowired DonateService donateService;

    /**
    * 新增捐赠 前端随意传一个id字段即可
    * @param 
    * @return 
    */
    @GetMapping("/add")
    public Result<?> addDonate(@RequestBody DonateDTO donateDTO) {
        String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
        log.info("用户{}请求新增捐赠", username);

        donateService.addDonate(donateDTO);
        return Result.success();
    }

    /**
    * 删除捐赠
    * @param 
    * @return 
    */
    @GetMapping("/delete")
    public Result<?> deleteDonate(@RequestParam(name = "id") Long id) {
        String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
        log.info("用户{}请求删除捐赠", username);

        donateService.deleteDonate(id);
        return Result.success();
    }

    /**
    * 更新捐赠
    * @param 
    * @return 
    */
    @GetMapping("/update")
    public Result<?> updateDonate(@RequestBody DonateDTO donateDTO) {
        String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
        log.info("用户{}请求更新捐赠", username);

        donateService.updateDonate(donateDTO);
        return Result.success();
    }

    /**
    * 获取捐赠信息 （分页）
    * @param 
    * @return 
    */
    @GetMapping("/get")
    public Result<?> getDonate(@RequestParam(name = "page",defaultValue = "1") Integer page, @RequestParam(name = "limit",defaultValue = "10") Integer limit) {
        String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
        log.info("用户{}请求获取捐赠", username);

        IPage<Donate> donate = donateService.getDonate(page, limit);
        return Result.success(donate);
    }
    
}
