package com.qin.catcat.unite.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.service.CaptchaService;

/**
 * @Description 系统控制器
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-04 11:20
 */
@RestController
@RequestMapping("/api/system")
public class SystemController {
    @Autowired
    CaptchaService captchaService;

    /**
     * @Description 获取滑动验证码（未使用）
     * @return Result<Map<String, Object>>
     */
    @GetMapping("/slider-captcha")
    public Result<Map<String, Object>> getSliderCaptcha() {
        try {
            return Result.success(captchaService.generateSliderCaptcha());
        } catch (IOException e) {
            return Result.error("生成验证码失败");
        }
    }

    /**
     * @Description 验证滑动验证码（未使用）
     * @param captchaKey 验证码标识
     * @param x 滑块位置
     * @return Result<Boolean>
     */
    @PostMapping("/verify-slider-captcha")
    public Result<Boolean> verifySliderCaptcha(@RequestParam String captchaKey, @RequestParam int x) {
        return Result.success(captchaService.verifySliderCaptcha(captchaKey, x));
    }

    /**
     * @Description 生成图形验证码（未使用）
     * @return Result<Map<String, Object>>
     */
    @GetMapping("/captcha")
    public Result<Map<String, Object>> generateCaptcha() {
        return Result.success(captchaService.generateCaptcha());
    }

    /**
     * @Description 验证图形验证码（未使用）
     * @param captchaKey 验证码标识
     * @param code 验证码
     * @return Result<Boolean>
     */
    @PostMapping("/verify-captcha")
    public Result<Boolean> verifyCaptcha(@RequestParam String captchaKey, @RequestParam String code) {
        return Result.success(captchaService.verifyCaptcha(captchaKey, code));
    }
}
