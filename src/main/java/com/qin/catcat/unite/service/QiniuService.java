package com.qin.catcat.unite.service;

import java.util.List;

public interface QiniuService {
    /**
     * 删除七牛云上的文件
     * @param fileUrl 文件URL或文件名
     * @return 是否删除成功
     */
    boolean deleteFile(List<String> fileNames);
} 