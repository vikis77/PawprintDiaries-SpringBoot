package com.qin.catcat.unite.popo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 帖子权重实体类
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-16 19:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostWeight {
    /**
     * 帖子
     */
    private Post post;
    /**
     * 权重
     */
    private double weight;
} 