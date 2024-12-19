package com.qin.catcat.unite.popo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import lombok.Data;

/* 
 * ES中的文档实体类
 */
@Data
@Document(indexName = "cat_post_user_index", createIndex = true)
@Setting(shards = 1, replicas = 0)
public class EsPostIndex {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String postId;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String article;
}
