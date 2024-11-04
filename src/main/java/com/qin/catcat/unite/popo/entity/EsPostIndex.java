package com.qin.catcat.unite.popo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.Data;

/* 
 * ES中的文档实体类
 */
@Data
@Document(indexName = "cat_post_user_index") // 指定索引名称（ES中）
public class EsPostIndex {

    @Id
    private String id;  // 指的是ES索引中每一条文档的ID（插入自动生成），如 "_id": "IpAm25IBphnOwpGPEO9a"

    private String postId;
    private String title;
    private String article;
}
