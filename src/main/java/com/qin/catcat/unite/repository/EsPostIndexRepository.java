// package com.qin.catcat.unite.repository;

// import java.util.List;

// import org.springframework.data.elasticsearch.annotations.Query;
// import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;

// import com.qin.catcat.unite.popo.entity.EsPostIndex;

// /* 
//  * 用于操作ES
//  * ElasticsearchRepository<EsPostIndex, String> ：指定操作的实体类和主键类型（ES文档ID）
//  */
// public interface EsPostIndexRepository extends ElasticsearchRepository<EsPostIndex, String> {
//     /**
//      * 根据标题精确匹配
//      */
//     List<EsPostIndex> findByTitle(String title);

//     /**
//      * 根据标题或内容进行模糊搜索
//      */
//     @Query("""
//         {
//             "bool": {
//                 "should": [
//                     {"match": {"title": "?0"}},
//                     {"match": {"article": "?0"}}
//                 ]
//             }
//         }
//     """)
//     Page<EsPostIndex> findByTitleOrArticle(String keyword, Pageable pageable);

//     /**
//      * 高亮搜索接口
//      */
//     @Query("{\"bool\": {\"should\": [{\"match\": {\"title\": \"?0\"}}, {\"match\": {\"article\": \"?0\"}}]}, \"highlight\": {\"fields\": {\"title\": {}, \"article\": {}}}}")
//     Page<EsPostIndex> searchWithHighlight(String keyword, Pageable pageable);

//     /**
//      * 多字段组合查询
//      */
//     @Query("{\"bool\": {\"must\": [{\"bool\": {\"should\": [{\"match\": {\"title\": \"?0\"}}, {\"match\": {\"article\": \"?0\"}}]}}, {\"term\": {\"authorId\": \"?1\"}}]}}")
//     Page<EsPostIndex> searchByKeywordAndAuthor(String keyword, Integer authorId, Pageable pageable);

//     /**
//      * 多字段组合查询（标题或内容）
//      */
//     Page<EsPostIndex> findByTitleContainingOrArticleContaining(String title, String article, Pageable pageable);
// }
