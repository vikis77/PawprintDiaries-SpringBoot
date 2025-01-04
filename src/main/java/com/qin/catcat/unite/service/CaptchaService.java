package com.qin.catcat.unite.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * @Description 滑动验证码服务
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-04 11:22
 */
public interface CaptchaService {

    /**
     * @Description 生成滑动验证码
     * @return Map<String, Object>
     */
    public Map<String, Object> generateSliderCaptcha() throws IOException;

    /**
     * @Description 验证滑动验证码
     * @param captchaKey 验证码标识
     * @param x 滑块位置
     * @return boolean
     */
    public boolean verifySliderCaptcha(String captchaKey, int x);

    /**
     * @Description 生成图形验证码
     * @return Map<String, Object>
     */
    public Map<String, Object> generateCaptcha();

    /**
     * @Description 验证图形验证码
     * @param captchaKey 验证码标识
     * @param code 验证码
     * @return boolean
     */
    public boolean verifyCaptcha(String captchaKey, String code);
}
