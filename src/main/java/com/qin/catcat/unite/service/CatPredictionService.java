package com.qin.catcat.unite.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.qin.catcat.unite.popo.entity.PredictionResult;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Service
@Slf4j
public class CatPredictionService {
    private final String PREDICTION_URL = "http://localhost:5000/predict";
    
    public PredictionResult predictCatBreed(MultipartFile image) throws IOException {
        log.info("开始调用python预测服务");
        RestTemplate restTemplate = new RestTemplate();
        
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        });
        
        ResponseEntity<PredictionResult> response = restTemplate.postForEntity(
            PREDICTION_URL,
            body,
            PredictionResult.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            PredictionResult result = response.getBody();
            if (result != null) {
                if (result.getSuccess() != null && result.getSuccess()) {
                    log.info("预测服务返回结果: {}", result);
                    return result;
                } else {
                    throw new RuntimeException("预测服务返回错误: " + result.getMessage());
                }
            } else {
                throw new RuntimeException("预测服务返回空结果");
            }
        } else {
            throw new RuntimeException("预测服务调用失败，HTTP状态码: " + response.getStatusCode());
        }
    }
} 