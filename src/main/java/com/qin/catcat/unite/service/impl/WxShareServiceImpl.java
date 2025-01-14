package com.qin.catcat.unite.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qin.catcat.unite.common.utils.CacheUtils;
import com.qin.catcat.unite.service.WxShareService;

import cn.hutool.core.lang.UUID;

/**
 * @Description 微信分享服务实现类.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-11 00:05
 */
@Service
public class WxShareServiceImpl implements WxShareService {
    @Value("${wx.appId}")
    private String appId;
    @Value("${wx.appSecret}")
    private String appSecret;
    @Autowired
    private CacheUtils cacheUtils;

    /**
     * @Description 获取微信配置
     * @param url 
     * @return 
     */
    public Map<String, String> getWxConfig(String url) throws Exception {
        // 获取access_token
        String accessToken = getAccessToken(appId, appSecret);
        // 获取jsapi_ticket
        String jsapiTicket = getJsapiTicket(accessToken);
        // 生成随机字符串
        String noncestr = UUID.randomUUID().toString();
        // 获取时间戳
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        // 生成签名
        String signature = generateSignature(jsapiTicket, noncestr, timestamp, url);

        Map<String, String> config = new HashMap<>();
        config.put("appId", appId);
        config.put("timestamp", timestamp);
        config.put("nonceStr", noncestr);
        config.put("signature", signature);
        return config;
    }

    /**
     * @Description 获取微信access_token
     * @param appId 
     * @param appSecret 
     * @return 
     */
    private String getAccessToken(String appId, String appSecret) throws Exception {
        // 查询缓存
        String cacheKey = "wx_access_token";
        String result = cacheUtils.getWithMultiLevel(cacheKey, String.class, 100, () -> {
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + appSecret;
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String r = EntityUtils.toString(entity);
            response.close();
            httpClient.close();
            return r;
        });

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(result);
        return root.path("access_token").asText();
    }

    /**
     * @Description 获取微信jsapi_ticket
     * @param accessToken 
     * @return 
     */
    private String getJsapiTicket(String accessToken) throws Exception {
        // 查询缓存
        String cacheKey = "wx_jsapi_ticket";
        String result = cacheUtils.getWithMultiLevel(cacheKey, String.class, 100, () -> {
            String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + accessToken + "&type=jsapi";
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String r = EntityUtils.toString(entity);
            response.close();
            httpClient.close();
            return r;
        });
    
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(result);
        return root.path("ticket").asText();
    }

    /**
     * @Description 生成微信分享签名
     * @param jsapiTicket 
     * @param noncestr 
     * @param timestamp 
     * @param url 
     * @return 
     */
    private String generateSignature(String jsapiTicket, String noncestr, String timestamp, String url) {
        String[] arr = new String[]{
                "jsapi_ticket=" + jsapiTicket,
                "noncestr=" + noncestr,
                "timestamp=" + timestamp,
                "url=" + url
        };
        Arrays.sort(arr); // 字典序排序
        String str = String.join("&", arr); // 拼接字符串
        return DigestUtils.sha1Hex(str); // SHA-1 加密
    }

    
}
