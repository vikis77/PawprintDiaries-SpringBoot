package com.qin.catcat.unite.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.security.HasPermission;
import com.qin.catcat.unite.service.ShotLinkShareService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description 短链接分享
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-18 22:44
 */
@RestController
@RequestMapping("/api/shotLinkShare")
@Slf4j
public class ShotLinkShareController {
    @Autowired
    private ShotLinkShareService shotLinkShareService;

    /**
     * @Description 获取短链接分享
     * @param url 
     * @return 
     */
    @Operation(summary = "获取短链接分享")
    @GetMapping("/getShotLinkShare")
    @HasPermission(value = "system:shotLinkShare:getShotLinkShare")
    public Result<Object> getShotLinkShare(@RequestParam String url) {
        return Result.success(new HashMap<String, String>() {{
            put("urlString", shotLinkShareService.getShotLinkShare(url));
        }});
    }

    /**
     * @Description 根据短链接获取原始链接并重定向
     * @param shortLink 短链接
     * @return ResponseEntity 重定向响应
     */
    @GetMapping("/getOriginLink")
    public ResponseEntity<Void> getOriginUrl(@RequestParam String shortLink) {
        // 获取原始链接
        String originUrl = shotLinkShareService.getOriginUrl(shortLink);
        
        // 设置重定向响应头
        HttpHeaders headers = new HttpHeaders();
        // 如果原始链接为空，则返回主页
        headers.set(HttpHeaders.LOCATION, originUrl != null ? originUrl : "https://pawprintdiaries.luckyiur.com");

        // 返回302临时重定向响应
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
