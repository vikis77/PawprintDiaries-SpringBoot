package com.qin.catcat.unite.service;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @Description 微信分享服务.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-11 00:04
 */
public interface WxShareService {

    /**
     * @Description 获取微信配置
     * @param url 
     * @return 
     */
    public Map<String, String> getWxConfig(String url) throws Exception;

    // /**
    //  * @Description 获取微信access_token
    //  * @param appId 
    //  * @param appSecret 
    //  * @return 
    //  */
    // public String getAccessToken(String appId, String appSecret) throws Exception;

    // /**
    //  * @Description 获取微信jsapi_ticket
    //  * @param accessToken 
    //  * @return 
    //  */
    // public String getJsapiTicket(String accessToken) throws Exception;
}
