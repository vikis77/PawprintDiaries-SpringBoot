package com.qin.catcat.unite.common.utils;


/* 
 * 创建一个TokenHolder类，用于存储和访问Token
 * 使用ThreadLocal来确保每个请求的Token是独立的
 */
public class TokenHolder {
    private static final ThreadLocal<String> tokenHolder = new ThreadLocal<>();

    public static void setToken(String token) {
        tokenHolder.set(token);
    }

    public static String getToken() {
        return tokenHolder.get();
    }

    public static void clear() {
        tokenHolder.remove();
    }
}
