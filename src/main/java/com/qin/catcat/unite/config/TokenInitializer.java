package com.qin.catcat.unite.config;

import com.qin.catcat.unite.mapper.UserMapper;
import com.qin.catcat.unite.popo.entity.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 应用启动时自动生成robot1用户的永久token
 * 这是一个独立的token生成器，不影响现有的JWT逻辑
 *
 * @author qin
 * @date 2025-08-21
 */
@Component
@Slf4j
public class TokenInitializer implements ApplicationRunner {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private Key key;

    private static final String ROBOT_USERNAME = "robot1";
    private static final String ROBOT_PASSWORD = "a1111111";

    // 100年的过期时间（毫秒）
    private static final long PERMANENT_EXPIRATION = 100L * 365L * 24L * 60L * 60L * 1000L;

    @PostConstruct
    private void init() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            log.info("=== 开始生成robot1用户的永久token ===");

            // 1. 查询robot1用户是否存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", ROBOT_USERNAME);
            User robotUser = userMapper.selectOne(queryWrapper);

            if (robotUser == null) {
                log.warn("用户 {} 不存在，无法生成永久token", ROBOT_USERNAME);
                return;
            }

            // 2. 验证密码
            if (!passwordEncoder.matches(ROBOT_PASSWORD, robotUser.getPassword())) {
                log.warn("用户 {} 密码验证失败，无法生成永久token", ROBOT_USERNAME);
                return;
            }

            // 3. 生成永久token
            String permanentToken = generatePermanentToken(robotUser.getUsername(), robotUser.getUserId());

            // 4. 打印到控制台
            log.info("=== robot1用户永久token生成成功 ===");
            log.info("用户名: {}", ROBOT_USERNAME);
            log.info("用户ID: {}", robotUser.getUserId());
            log.info("永久Token: {}", permanentToken);
            log.info("=== 请保存此token用于API调用 ===");

        } catch (Exception e) {
            log.error("生成robot1永久token时发生错误", e);
        }
    }

    /**
     * 独立生成永久token（100年有效期）
     * 不影响现有的JwtTokenProvider逻辑
     */
    private String generatePermanentToken(String username, Integer userId) {
        // 使用中国时区
        TimeZone chinaTimeZone = TimeZone.getTimeZone("Asia/Shanghai");
        Date now = Calendar.getInstance(chinaTimeZone).getTime();
        Date expiryDate = new Date(now.getTime() + PERMANENT_EXPIRATION);

        log.info("生成永久token - 用户名: {}, 用户ID: {}", username, userId);
        log.info("永久token过期时间: {}", expiryDate);

        return Jwts.builder()
                .setId(String.valueOf(userId))
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }
}
