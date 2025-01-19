package com.qin.catcat.unite.common.utils;

/**
 * @Description 短链接生成工具类
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-18
 */
public class ShortLinkUtils {
    // 字符集：包括数字和大小写字母
    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = BASE62.length();

    /**
     * 生成短链接
     * @param id 唯一标识ID
     * @return 生成的短链接字符串
     */
    public static String generateShortLink(long id) {
        StringBuilder shortUrl = new StringBuilder();
        // 将ID转换为62进制字符串
        while (id > 0) {
            shortUrl.insert(0, BASE62.charAt((int) (id % BASE)));
            id = id / BASE;
        }
        // 补齐到6位
        while (shortUrl.length() < 6) {
            shortUrl.insert(0, BASE62.charAt(0));
        }
        return shortUrl.toString();
    }

    /**
     * 解析短链接回ID
     * @param shortUrl 短链接字符串
     * @return 原始ID
     */
    public static long parseShortLink(String shortUrl) {
        long id = 0;
        for (int i = 0; i < shortUrl.length(); i++) {
            id = id * BASE + BASE62.indexOf(shortUrl.charAt(i));
        }
        return id;
    }
} 