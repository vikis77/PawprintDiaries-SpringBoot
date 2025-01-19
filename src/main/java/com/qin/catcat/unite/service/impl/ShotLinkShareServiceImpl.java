package com.qin.catcat.unite.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qin.catcat.unite.common.constant.Constant;
import com.qin.catcat.unite.common.utils.CacheUtils;
import com.qin.catcat.unite.common.utils.ShortLinkUtils;
import com.qin.catcat.unite.mapper.ShotLinkShareMapper;
import com.qin.catcat.unite.popo.entity.ShotLink;
import com.qin.catcat.unite.service.ShotLinkShareService;

import java.util.zip.CRC32;

/**
 * @Description 短链接分享服务实现
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-18 22:46
 */
@Service
public class ShotLinkShareServiceImpl implements ShotLinkShareService {
    @Autowired
    private ShotLinkShareMapper shotLinkShareMapper;
    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private Environment environment;

    /**
     * 根据URL生成唯一ID
     * @param url 原始URL
     * @return 生成的唯一ID
     */
    private long getUrlId(String url) {
        CRC32 crc32 = new CRC32();
        crc32.update(url.getBytes());
        return crc32.getValue();
    }

    /**
     * @Description 获取短链接
     * @param url 如果是帖子 形式为 postId=xxx, 如果是猫猫 形式为 catId=xxx, 如果是坐标 形式为 coordinateId=xxx
     * @return 处理后的短链接
     */
    @Override
    public String getShotLinkShare(String url) {
        // 查询缓存
        ShotLink shotLink = cacheUtils.getWithMultiLevel(Constant.GET_SHOT_LINK_SHARE + ":" + url, ShotLink.class, () -> {
            // 使用算法生成（Base62编码）：根据url生成短链接关键词
            long urlId = getUrlId(url);
            String shortLink = ShortLinkUtils.generateShortLink(urlId);

            ShotLink shotLinkEntity = new ShotLink();
            if (url.contains("catId=")) {
                shotLinkEntity.setType(10);
            } else if (url.contains("postId=")) {
                shotLinkEntity.setType(20);
            } else if (url.contains("coordinateId=")) {
                shotLinkEntity.setType(30);
            }
            shotLinkEntity.setOriginUrl(url);
            shotLinkEntity.setConvertUrl(shortLink);
            // 保存短链接到数据库
            shotLinkShareMapper.insert(shotLinkEntity);

            String returnUrl = "";
            // 如果是开发环境,返回开发环境链接
            if ("dev".equals(environment.getProperty("spring.profiles.active"))) {
                if (url.contains("postId=")) {
                    returnUrl = "嘿，来看看这个莲峰帖子吧：\n" +
                            "http://localhost:5173/SL/" + shortLink + "\n" +
                            "这是一个关于校园流浪猫救助和分享的社区，快来一起关注和讨论吧！";
                } else if (url.contains("catId=")) {
                    returnUrl = "嘿，来看看这只莲峰猫猫吧：\n" +
                            "http://localhost:5173/SL/" + shortLink + "\n" +
                            "这是一个关于校园流浪猫救助和分享的社区，快来一起关注和讨论吧！";
                }
            } else {
                // 如果是生产环境,返回正式链接
                if (url.contains("postId=")) {
                    returnUrl = "嘿，来看看这个莲峰帖子吧：\n" +
                            "https://pawprintdiaries.luckyiur.com/SL/" + shortLink + "\n" +
                            "这是一个关于校园流浪猫救助和分享的社区，快来一起关注和讨论吧！";
                } else if (url.contains("catId=")) {
                    returnUrl = "嘿，来看看这只莲峰猫猫吧：\n" +
                            "https://pawprintdiaries.luckyiur.com/SL/" + shortLink + "\n" +
                            "这是一个关于校园流浪猫救助和分享的社区，快来一起关注和讨论吧！";
                }
            }

            
            shotLinkEntity.setConvertUrl(returnUrl);
            return shotLinkEntity;
        });
        return shotLink.getConvertUrl();
    }

    /**
     * @Description 根据短链接获取原始链接
     * @param shortLink 短链接
     * @return 原始链接
     */
    @Override
    public String getOriginUrl(String shortLink) {
        // 获取短链接中?之后的部分
        // String shortLinkWithoutParam = shortLink.substring(shortLink.indexOf("?") + 1);
        ShotLink shotLink = shotLinkShareMapper.selectOne(new LambdaQueryWrapper<ShotLink>().eq(ShotLink::getConvertUrl, shortLink).eq(ShotLink::getIsDeleted, 0));
        // 如果是开发环境,直接返回原始链接,方便调试
        if ("dev".equals(environment.getProperty("spring.profiles.active"))) {
            if (shotLink.getType() == 10) {
                return "http://localhost:5173/pages/Card?" + shotLink.getOriginUrl();
            } else if (shotLink.getType() == 20) {
                return "http://localhost:5173/pages/Post?" + shotLink.getOriginUrl();
            } else {
                return null;
            }
        } else {
            // 如果是生产环境,返回正式链接
            if (shotLink != null) {
                if (shotLink.getType() == 10) {
                    return "https://pawprintdiaries.luckyiur.com/pages/Card?" + shotLink.getOriginUrl();
                } else if (shotLink.getType() == 20) {
                    return "https://pawprintdiaries.luckyiur.com/pages/Post?" + shotLink.getOriginUrl();
                } else {
                    return null;
                }
            }
        }
        return null;
    }
}
