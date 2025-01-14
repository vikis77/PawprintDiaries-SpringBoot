// package com.qin.catcat.unite.service;

// import java.util.List;
// import java.util.concurrent.CountDownLatch;
// import java.util.concurrent.locks.Lock;
// import java.util.concurrent.locks.ReentrantLock;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Service;

// import com.qin.catcat.unite.common.utils.ElasticsearchUtil;
// import com.qin.catcat.unite.mapper.PostMapper;
// import com.qin.catcat.unite.popo.entity.EsPostIndex;
// import com.qin.catcat.unite.repository.EsPostIndexRepository;

// import lombok.extern.slf4j.Slf4j;

// /* 
//  * 数据同步服务，用于定时自动同步MySQL数据到ES中
//  */
// @Service
// @Slf4j
// public class DataSyncService {

//     private final EsPostIndexRepository esPostIndexRepository; // 操作ES
//     private final PostMapper postMapper; // 操作MySQL
//     private final PostService postService; // 操作MySQL
    
//     @Autowired
//     private DataInitializationService dataInitializationService; // 数据初始化服务
//     @Autowired
//     private ElasticsearchUtil elasticsearchUtil; // ES工具类

//     // 同步锁，确保同一时间只有一个线程在执行同步操作
//     private static final Lock syncLock = new ReentrantLock();

//     // 构造函数 构造器注入
//     public DataSyncService(EsPostIndexRepository esPostIndexRepository, PostMapper postMapper, PostService postService) {
//         this.esPostIndexRepository = esPostIndexRepository;
//         this.postMapper = postMapper;
//         this.postService = postService;
//     }

//     /**
//      * 清空索引并重新同步所有数据
//      */
//     public void reindexAll() {
//         try {
//             // 尝试获取同步锁
//             if (!syncLock.tryLock()) {
//                 log.warn("另一个同步操作正在进行中，跳过本次同步");
//                 return;
//             }

//             try {
//                 log.info("开始重新索引所有数据到ES");
                
//                 // 1. 删除索引
//                 if (elasticsearchUtil.indexExists("cat_post_user_index")) {
//                     elasticsearchUtil.deleteIndex("cat_post_user_index");
//                     log.info("已删除旧索引");
//                 }
                
//                 // 2. 同步所有数据
//                 doSyncData();
                
//                 log.info("重新索引完成");
//             } finally {
//                 syncLock.unlock();
//             }
//         } catch (Exception e) {
//             log.error("重新索引过程中发生错误: ", e);
//             throw new RuntimeException("重新索引失败", e);
//         }
//     }

//     // 暂时停用ES定时同步
//     // 定时任务 每隔一小时同步一次
//     // @Scheduled(fixedRate = 3600000) // 每小时同步一次
//     // @Scheduled(fixedRate = 10000) // 单位毫秒
//     public void scheduleSyncData() {
//         // 等待数据预热完成
//         dataInitializationService.waitForInitialization();
//         // 数据预热完成后执行同步
//         log.info("当前主线程信息：ID = {}, Name = {}, Priority = {}", 
//                     Thread.currentThread().getId(), Thread.currentThread().getName(), Thread.currentThread().getPriority());
//         syncDataFromMySQLToES();
//     }

//     // 异步执行数据同步
//     @Async
//     public void syncDataFromMySQLToES() {
//         // 尝试获取同步锁
//         if (!syncLock.tryLock()) {
//             log.warn("另一个同步操作正在进行中，跳过本次同步");
//             return;
//         }

//         try {
//             // 打印当前执行的线程信息
//             Thread currentThread = Thread.currentThread();
//             log.info("开始执行MySQL帖子数据同步到ES - 执行当前同步任务的线程信息：ID = {}, Name = {}, Priority = {}", 
//                     currentThread.getId(), currentThread.getName(), currentThread.getPriority());

//             // 执行同步操作
//             doSyncData();
//         } finally {
//             syncLock.unlock();
//         }
//     }

//     // 实际执行同步的方法
//     private void doSyncData() {
//         try {
//             // 分页查询，避免一次性加载过多数据
//             int pageSize = 100; // 减小每页数据量，避免数据量太大
//             int currentPage = 0;
//             long total = 0;
//             List<EsPostIndex> esPostIndexs;

//             while (!(esPostIndexs = postService.selectEsPostIndexByPage(currentPage, pageSize)).isEmpty()) {
//                 log.info("开始保存第{}页数据到ES，数据量：{}", currentPage + 1, esPostIndexs.size());
                
//                 try {
//                     // 批量保存到ES
//                     esPostIndexRepository.saveAll(esPostIndexs);
//                     total += esPostIndexs.size();
//                     log.info("成功保存第{}页数据到ES", currentPage + 1);
//                 } catch (Exception e) {
//                     log.error("保存第{}页数据到ES时发生错误: {}", currentPage + 1, e.getMessage());
//                     throw e;
//                 }
                
//                 currentPage++;
//             }
            
//             log.info("数据同步完成，共同步 {} 条数据", total);
            
//             // 验证同步结果
//             long esCount = esPostIndexRepository.count();
//             log.info("ES中实际文档数量：{}", esCount);
//             if (esCount != total) {
//                 log.warn("同步数据量({})与ES中实际文档数量({})不一致，可能存在数据丢失", total, esCount);
//             }
//         } catch (Exception e) {
//             log.error("数据同步过程中发生错误: ", e);
//             throw new RuntimeException("数据同步失败", e);
//         }
//     }
// }
