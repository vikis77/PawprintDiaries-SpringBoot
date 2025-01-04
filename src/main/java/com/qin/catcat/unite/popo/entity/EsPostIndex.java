package com.qin.catcat.unite.popo.entity;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
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
@Setting(shards = 1, replicas = 0) // 设置分片和副本
public class EsPostIndex {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String postId;

    // 标题
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    // 内容
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String article;

    @Field(type = FieldType.Integer)
    private Integer authorId;

    @Field(type = FieldType.Integer)
    private Integer likeCount;

    @Field(type = FieldType.Integer)
    private Integer collectingCount;

    @Field(type = FieldType.Integer)
    private Integer commentCount;

    @Field(type = FieldType.Date, format = DateFormat.epoch_millis)
    private Date sendTime;

    @Field(type = FieldType.Date, format = DateFormat.epoch_millis)
    private Date updateTime;

    @Field(type = FieldType.Keyword)
    private String coverPicture;

    @Field(type = FieldType.Integer)
    private Integer isDeleted;

    @Field(type = FieldType.Integer)
    private Integer isAdopted;

    @Field(type = FieldType.Integer)
    private Integer approveUserId;
}
