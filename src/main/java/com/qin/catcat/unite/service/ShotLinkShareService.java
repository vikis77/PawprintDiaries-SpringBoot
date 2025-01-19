package com.qin.catcat.unite.service;

/**
 * @Description 短链接分享服务
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-18 22:46
 */
public interface ShotLinkShareService {
    /**
     * @Description 获取短链接
     * @param url 如果是帖子 形式为 postId=xxx, 如果是猫猫 形式为 catId=xxx, 如果是坐标 形式为 coordinateId=xxx
     * @return 处理后的短链接
     */
    String getShotLinkShare(String url);

    /**
     * @Description 根据短链接获取原始链接
     * @param shortLink 短链接
     * @return 原始链接
     */
    String getOriginUrl(String shortLink);
}

