package com.qin.catcat.unite.controller;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/upload")
@Tag(name = "文件上传")
@Slf4j
public class FileUploadController {

    private static final String UPLOAD_DIR = "E:\\01-Codes\\JavaCode\\catcat\\src\\main\\resources\\cat_pics"; // 上传文件存储的目录

    @PostMapping("/catImage")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "文件为空，请选择一个文件上传";
        }

        String fileName = file.getOriginalFilename();
        // 上传文件存储的目录
        File destinationFile = new File(UPLOAD_DIR + File.separator + fileName);
        String file_path = UPLOAD_DIR + File.separator + fileName;
        try {
            // 保存文件到目标位置
            file.transferTo(destinationFile);
            // 调用Python预测服务
            String predictionResult = callPythonPredictionService(file_path);
            log.info(predictionResult);
            return predictionResult;
            // return "文件上传成功: " + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "文件上传失败: " + e.getMessage();
        }
    }

    private String callPythonPredictionService(String filePath) throws IOException {
        // Python服务的URL
        String pythonServiceUrl = "http://localhost:5000/predict";

        // 使用HttpClient发起POST请求
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 创建HttpPost请求
            HttpPost uploadFile = new HttpPost(pythonServiceUrl);

            // 使用MultipartEntityBuilder构建multipart请求
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            // 将文件路径作为字符串参数添加到请求体中
            builder.addTextBody("file_path", filePath);

            // 构建HttpEntity
            HttpEntity entity = builder.build();
            uploadFile.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(uploadFile)) {
                String responseString = EntityUtils.toString(response.getEntity());
                log.info(responseString);
                // 处理响应
                return responseString;
            }
        }
    }
}
