package com.qin.catcat.unite.service.impl;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.qin.catcat.unite.service.CaptchaService;

/**
 * @Description 滑动验证码服务实现类
 * 
 * 这样的组合可以有效防止：
    1. 自动化攻击
    2. 请求伪造
    3. 重放攻击
    4. 参数篡改
    建议根据接口的安全级别选择合适的验证方式：
        普通接口：仅限流
        重要接口：限流 + 签名验证
        关键接口：限流 + 签名验证 + 验证码
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-04 11:23
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    private static final long CAPTCHA_EXPIRE_TIME = 300; // 5分钟过期
    
    /**
     * @Description 生成滑动验证码
     * @return Map<String, Object>
     */
    public Map<String, Object> generateSliderCaptcha() throws IOException {
        // 1. 生成滑块验证码背景图和滑块图
        BufferedImage originalImage = ImageIO.read(new File("background.jpg"));
        BufferedImage templateImage = createTemplateImage();
        
        // 2. 随机生成滑块位置
        int x = new Random().nextInt(originalImage.getWidth() - templateImage.getWidth());
        int y = new Random().nextInt(originalImage.getHeight() - templateImage.getHeight());
        
        // 3. 生成验证码标识
        String captchaKey = UUID.randomUUID().toString();
        
        // 4. 存储正确位置
        redisTemplate.opsForValue().set(
            "slider:" + captchaKey,
            String.valueOf(x),
            300,
            TimeUnit.SECONDS
        );
        
        // 5. 返回验证码信息
        Map<String, Object> result = new HashMap<>();
        result.put("captchaKey", captchaKey);
        result.put("backgroundImage", encodeImage(originalImage));
        result.put("sliderImage", encodeImage(templateImage));
        
        return result;
    }
    
    /**
     * @Description 验证滑动验证码
     * @param captchaKey 验证码标识
     * @param submitX 提交的滑块位置
     * @return boolean
     */
    public boolean verifySliderCaptcha(String captchaKey, int submitX) {
        String correctX = redisTemplate.opsForValue().get("slider:" + captchaKey);
        if (correctX != null) {
            int x = Integer.parseInt(correctX);
            // 允许5像素的误差
            return Math.abs(submitX - x) <= 5;
        }
        return false;
    }

    /**
     * @Description 生成图形验证码
     * @return String
     */
    public Map<String, Object> generateCaptcha() {
        // 1. 生成验证码
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(200, 100);
        String code = captcha.getCode();
        String image = captcha.getImageBase64Data();
        
        // 2. 生成唯一标识
        String captchaKey = UUID.randomUUID().toString();
        
        // 3. 将验证码存入Redis
        redisTemplate.opsForValue().set(
            "captcha:" + captchaKey, 
            code,
            CAPTCHA_EXPIRE_TIME, 
            TimeUnit.SECONDS
        );
        
        // 4. 返回图片和key
        Map<String, Object> result = new HashMap<>();
        result.put("captchaKey", captchaKey);
        result.put("captchaImage", image);
        
        return result;
    }

    /**
     * @Description 验证图形验证码
     * @param captchaKey 验证码标识
     * @param code 验证码
     * @return boolean
     */
    public boolean verifyCaptcha(String captchaKey, String code) {
        // 1. 从Redis获取验证码
        String correctCode = redisTemplate.opsForValue().get("captcha:" + captchaKey);
        
        // 2. 验证
        if (correctCode != null && correctCode.equalsIgnoreCase(code)) {
            // 验证成功后删除验证码
            redisTemplate.delete("captcha:" + captchaKey);
            return true;
        }
        
        return false;
    }

    /**
     * 创建滑块模板图片
     */
    private BufferedImage createTemplateImage() {
        // 创建一个48x48的滑块图片
        BufferedImage templateImage = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        // 这里简单创建一个矩形滑块，实际应用中可以创建更复杂的形状
        for (int x = 0; x < 48; x++) {
            for (int y = 0; y < 48; y++) {
                templateImage.setRGB(x, y, 0xFF000000); // 黑色不透明
            }
        }
        return templateImage;
    }

    /**
     * 将图片转换为Base64字符串
     */
    private String encodeImage(BufferedImage image) {
        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            return java.util.Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new RuntimeException("图片转换失败", e);
        }
    }

}

