package com.qin.catcat.unite.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

import com.qin.catcat.unite.popo.entity.EsPostIndex;

import java.util.List;

/* 
 * 用于操作ES
 * ElasticsearchRepository<EsPostIndex, String> ：指定操作的实体类和主键类型（ES文档ID）
 */
public interface EsPostIndexRepository extends ElasticsearchRepository<EsPostIndex, String> {
        // 自定义查询方法
    List<EsPostIndex> findByTitle(String title);

    // 根据输入的关键词匹配标题或文章内容，返回符合条件的帖子
    List<EsPostIndex> findByTitleOrArticle(String title, String article);

    // 根据输入的关键词匹配标题或文章内容，返回符合条件的帖子
    // List<EsPostIndex> findByTitleContainingOrArticleContaining(String title, String article);

    // 使用 ElasticsearchRepository 提供的 findAll 方法
    List<EsPostIndex> findAll();

}
