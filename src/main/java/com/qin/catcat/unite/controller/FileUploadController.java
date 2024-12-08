package com.qin.catcat.unite.controller;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.common.utils.TokenHolder;
import com.qin.catcat.unite.popo.vo.QiniuTokenVO;
import com.qiniu.util.Auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/upload")
@Tag(name = "文件上传")
@Slf4j
// @CrossOrigin(origins = "https://pawprintdiaries.luckyiur.com") // 允许的来源
public class FileUploadController {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Value("${qiniu.access-key}")
    private String accessKey;
    @Value("${qiniu.secret-key}")
    private String secretKey;
    @Value("${qiniu.bucket}")
    private String bucket;

    private static final String UPLOAD_DIR = "E:\\01-Codes\\JavaCode\\catcat\\src\\main\\resources\\upload_pics_test"; // 上传文件存储的目录

    // 从配置文件获取文件上传目录
    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/catImageTest")
    public ResponseEntity<String> uploadImageTest(@RequestParam("file") MultipartFile file) {
        log.info("用户{}上传图片：{}",TokenHolder.getToken(),file.getOriginalFilename());

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file selected");
        }

        try {
            // 确保目录存在
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 保存文件
            String fileName = file.getOriginalFilename();
            File dest = new File(uploadDir, fileName);
            file.transferTo(dest);

            // // 获取项目根目录的绝对路径
            // String projectRoot = Paths.get("").toAbsolutePath().toString();
            // // 设置文件存储路径
            // String fileName = file.getOriginalFilename();
            // String filePath = projectRoot + "/src/main/resources/upload_pics_test/" + fileName;
            // File dest = new File(filePath);
            
            // // 确保目录存在
            // dest.getParentFile().mkdirs();

            // // 保存文件到指定路径
            // file.transferTo(dest);
            
            // 返回图片的 URL
            // String fileUrl = "http://localhost:8080/images/" + fileName;
            return ResponseEntity.ok("File uploaded successfully");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
        }
    }

    // 客户端上传图片并调用Python识别猫猫服务
    // @PostMapping("/catImage")
    // public String uploadImage(@RequestParam("file") MultipartFile file) {
    //     if (file.isEmpty()) {
    //         return "文件为空，请选择一个文件上传";
    //     }

    //     String fileName = file.getOriginalFilename();
    //     // 上传文件存储的目录
    //     File destinationFile = new File(UPLOAD_DIR + File.separator + fileName);
    //     String file_path = UPLOAD_DIR + File.separator + fileName;
    //     try {
    //         // 保存文件到目标位置
    //         file.transferTo(destinationFile);
    //         // 调用Python预测服务
    //         String predictionResult = callPythonPredictionService(file_path);
    //         log.info(predictionResult);
    //         return predictionResult;
    //         // return "文件上传成功: " + fileName;
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //         return "文件上传失败: " + e.getMessage();
    //     }
    // }

    // 调用Python预测服务
    // private String callPythonPredictionService(String filePath) throws IOException {
    //     // Python服务的URL
    //     String pythonServiceUrl = "http://localhost:5000/predict";

    //     // 使用HttpClient发起POST请求
    //     try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
    //         // 创建HttpPost请求
    //         HttpPost uploadFile = new HttpPost(pythonServiceUrl);

    //         // 使用MultipartEntityBuilder构建multipart请求
    //         MultipartEntityBuilder builder = MultipartEntityBuilder.create();

    //         // 将文件路径作为字符串参数添加到请求体中
    //         builder.addTextBody("file_path", filePath);

    //         // 构建HttpEntity
    //         HttpEntity entity = builder.build();
    //         uploadFile.setEntity(entity);

    //         try (CloseableHttpResponse response = httpClient.execute(uploadFile)) {
    //             String responseString = EntityUtils.toString(response.getEntity());
    //             log.info(responseString);
    //             // 处理响应
    //             return responseString;
    //         }
    //     }
    // }

    // 获取七牛云上传凭证
    @GetMapping("/qiniuUploadToken")
    public Result<QiniuTokenVO> getUploadToken() {
        log.info("用户{}请求获取七牛云上传凭证",jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken()));

        Auth auth = Auth.create(accessKey, secretKey);
        QiniuTokenVO qiniuTokenVO = new QiniuTokenVO();
        qiniuTokenVO.setQiniuToken(auth.uploadToken(bucket));
        return Result.success(qiniuTokenVO);
    }
}
