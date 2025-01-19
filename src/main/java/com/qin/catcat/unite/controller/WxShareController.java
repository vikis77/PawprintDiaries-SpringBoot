package com.qin.catcat.unite.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.security.HasPermission;
import com.qin.catcat.unite.service.WxShareService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description 微信分享控制器.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-11 00:08
 */
@RestController
@RequestMapping("/api/wxshare")
@Slf4j
public class WxShareController {
    @Autowired
    private WxShareService wxShareService;

    /**
     * @Description 获取微信配置
     * @param url 
     * @return 
     */
    @Operation(summary = "获取微信配置")
    @GetMapping("/wxConfig")
    @HasPermission(value = "system:wx:wxshare:wxConfig")    
    public Result<Map<String, String>> getWxConfig(@RequestParam String url) throws Exception {
        return Result.success(wxShareService.getWxConfig(url));
    }

    // @GetMapping("/getAccessToken")
    // public Result<String> getAccessToken(@RequestParam String appId, @RequestParam String appSecret) {
    //     try {
    //         return Result.success(wxShareService.getAccessToken(appId, appSecret));
    //     } catch (Exception e) {
    //         log.error("获取微信access_token失败", e);
    //         return Result.error("获取微信access_token失败");
    //     }
    // }

    // @GetMapping("/getJsapiTicket")
    // public Result<String> getJsapiTicket(@RequestParam String accessToken) {
    //     try {
    //         return Result.success(wxShareService.getJsapiTicket(accessToken));
    //     } catch (Exception e) {
    //         log.error("获取微信jsapi_ticket失败", e);
    //         return Result.error("获取微信jsapi_ticket失败");
    //     }
    // }

}
