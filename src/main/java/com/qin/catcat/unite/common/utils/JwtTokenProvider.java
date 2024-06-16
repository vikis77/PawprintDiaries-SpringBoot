package com.qin.catcat.unite.common.utils;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationInMs;


    //生成Token
    public String generateToken(String username,String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        
        return Jwts.builder()
                .setId(userId)//设置 JWT 的唯一标识符（JTI）
                .setSubject(username)//设置 JWT 的主题（Subject）
                .setIssuedAt(new Date())//设置 JWT 的签发时间
                .setExpiration(expiryDate)// 设置 JWT 的过期时间
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

        return claims.get("userId",String.class);
    }

    //验证Token
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            // 签名异常
        } catch (MalformedJwtException ex) {
            // JWT 格式错误
        } catch (ExpiredJwtException ex) {
            // JWT 过期
        } catch (UnsupportedJwtException ex) {
            // 不支持的 JWT
        } catch (IllegalArgumentException ex) {
            // JWT 字符串为空或 null
        }
        return false;
    }
}
