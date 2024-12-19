package com.qin.catcat.unite.service;

import java.util.List;

public interface QiniuService {
    /**
     * 删除七牛云上的文件
     * @param fileNames 文件名列表
     * @param type 文件类型: user_avatar, post_pics, post_video
     * @return 是否删除成功
     */
    boolean deleteFile(List<String> fileNames, String type);
} 