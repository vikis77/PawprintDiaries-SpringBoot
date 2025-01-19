package com.qin.catcat.unite.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.qin.catcat.unite.service.QiniuService;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.util.Auth;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QiniuServiceImpl implements QiniuService {

    @Value("${qiniu.access-key}")
    private String accessKey;

    @Value("${qiniu.secret-key}")
    private String secretKey;

    @Value("${qiniu.bucket}")
    private String bucket;

    /**
     * 批量删除七牛云上的文件
     * @param fileNames 文件名列表
     * @return 是否删除成功
     */
    @Override
    public boolean deleteFile(List<String> fileNames, String type) {
        if(fileNames != null && !fileNames.isEmpty()){
            try {
                Configuration cfg = new Configuration(Region.autoRegion());
                Auth auth = Auth.create(accessKey, secretKey);
                BucketManager bucketManager = new BucketManager(auth, cfg);
                for(String fileName : fileNames){
                    try {
                        String key = "";
                        // 直接使用文件名作为key，添加目录前缀
                        if (type.equals("post_pics")){
                            key = "catcat/post_pics/" + fileName;
                        } else if (type.equals("user_avatar")){
                            key = "catcat/user_avatar/" + fileName;
                        } else if (type.equals("cat_pics")){
                            key = "catcat/cat_pics/" + fileName;
                        }
                        // 删除文件
                        bucketManager.delete(bucket, key);
                        log.info("成功删除七牛云文件: {}", key);
                    } catch (Exception e) {
                        log.error("删除文件失败: {}, 错误: {}", fileName, e.getMessage());
                    }
                }
                return true;
            } catch (Exception e) {
                log.error("删除七牛云文件过程中发生错误: {}", e.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * 删除单个七牛云上的文件
     * @param fileName 文件名
     * @param type 文件类型: user_avatar, post_pics, post_video，cat_pics
     * @return 是否删除成功
     */
    @Override
    public boolean deleteFile(String fileName, String type) {
        List<String> fileNames = new ArrayList<>();
        fileNames.add(fileName);
        return deleteFile(fileNames, type);
    }
} 