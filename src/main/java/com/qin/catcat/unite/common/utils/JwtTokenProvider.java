package com.qin.catcat.unite.common.utils;

import io.jsonwebtoken.*;
// import io.jsonwebtoken.io.Decoders;
// import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;


@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;


    //根据 用户名 用户ID 生成Token
    public String generateToken(String username,String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        log.info("Generating token for username: {}", username);
        log.info("JWT Secret: {}", jwtSecret);
        log.info("JWT Expiration: {}", jwtExpirationInMs);
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            throw new IllegalArgumentException("JWT secret cannot be null or empty");
        }
        return Jwts.builder()
                .setId(userId)//设置 JWT 的唯一标识符（JTI）
                .setSubject(username)//设置 JWT 的主题（Subject）
                .setIssuedAt(new Date())//设置 JWT 的签发时间
                .setExpiration(expiryDate)// 设置 JWT 的过期时间 3600000 #生成时间+1小时
                // .claim("role","admin")//设置自定义信息
                .signWith(SignatureAlgorithm.HS256, jwtSecret)//设置签名算法和密钥，用于对 JWT 进行签名
                .compact();
    }

    //从Token中获取userName
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    //从Token中获取userId
    public String getUserIdFromJWT(String token){
        Claims claims = Jwts.parser()
        .setSigningKey(jwtSecret)
        .parseClaimsJws(token)
        .getBody();

        // return claims.get("userId",String.class);
        return claims.getId();
    }

    //验证Token
    public boolean validateToken(String authToken) {
        log.info("调用JWT工具类校验");
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            //JWT 签名无效
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            //JWT 令牌格式无效
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            //JWT 令牌已过期
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            //不支持的 JWT 令牌
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            //JWT 声明字符串为空
            log.error("JWT claims string is empty.");
        } catch (Exception ex) {
            log.error("Unexpected error while validating JWT token", ex);
        }
        return false;
    }

}
