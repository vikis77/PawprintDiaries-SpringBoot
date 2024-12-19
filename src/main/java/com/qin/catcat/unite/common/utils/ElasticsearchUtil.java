package com.qin.catcat.unite.common.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ElasticsearchUtil {

    @Autowired
    private ElasticsearchClient client;

    /**
     * 检查索引是否存在
     */
    public boolean indexExists(String indexName) throws IOException {
        return client.indices().exists(e -> e.index(indexName)).value();
    }

    /**
     * 创建索引
     */
    public void createIndex(String indexName, String mappings) throws IOException {
        CreateIndexResponse response = client.indices().create(c -> c
            .index(indexName)
            .withJson(new java.io.StringReader(mappings))
        );
        log.info("索引创建结果: {}", response.acknowledged());
    }

    /**
     * 删除索引
     */
    public void deleteIndex(String indexName) throws IOException {
        DeleteIndexResponse response = client.indices().delete(d -> d.index(indexName));
        log.info("索引删除结果: {}", response.acknowledged());
    }

    /**
     * 添加/更新文档
     */
    public void indexDocument(String indexName, String id, Map<String, Object> document) throws IOException {
        IndexResponse response = client.index(i -> i
            .index(indexName)
            .id(id)
            .document(document)
        );
        log.info("文档索引结果: {}", response.result().jsonValue());
    }

    /**
     * 搜索文档
     */
    public List<Map<String, Object>> searchDocuments(String indexName, String field, String value) throws IOException {
        SearchResponse<Map> response = client.search(s -> s
            .index(indexName)
            .query(q -> q
                .match(m -> m
                    .field(field)
                    .query(value)
                )
            ),
            Map.class
        );

        List<Map<String, Object>> results = new ArrayList<>();
        for (Hit<Map> hit : response.hits().hits()) {
            results.add(hit.source());
        }
        return results;
    }

    /**
     * 获取单个文档
     */
    public Map<String, Object> getDocument(String indexName, String id) throws IOException {
        GetResponse<Map> response = client.get(g -> g
            .index(indexName)
            .id(id),
            Map.class
        );
        return response.source();
    }

    /**
     * 获取所有索引
     * @return 索引名称列表
     */
    public List<String> getAllIndices() throws IOException {
        return client.indices().get(builder -> builder
            .index("*"))
            .result()
            .keySet()
            .stream()
            .toList();
    }
} 