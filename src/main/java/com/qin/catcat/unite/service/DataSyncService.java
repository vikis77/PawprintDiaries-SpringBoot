package com.qin.catcat.unite.service;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.qin.catcat.unite.mapper.PostMapper;
import com.qin.catcat.unite.popo.entity.EsPostIndex;
import com.qin.catcat.unite.repository.EsPostIndexRepository;

import lombok.extern.slf4j.Slf4j;

/* 
 * 数据同步服务，用于定时自动同步MySQL数据到ES中
 */
@Service
@Slf4j
public class DataSyncService {

    private final EsPostIndexRepository esPostIndexRepository; // 操作ES
    private final PostMapper PostMapper; // 操作MySQL

    // 构造函数 构造器注入
    public DataSyncService(EsPostIndexRepository esPostIndexRepository, PostMapper postMapper) {
        this.esPostIndexRepository = esPostIndexRepository;
        this.PostMapper = postMapper;
    }

    // 定时任务 每隔一小时同步一次
    @Scheduled(fixedRate = 3600000) // 每小时同步一次
    // @Scheduled(fixedRate = 10000) // 单位毫秒
    public void scheduleSyncData() {
        syncDataFromMySQLToES();  // 定时任务调用异步方法
    }

    // TODO 可优化，当前是全量同步，冗余效率低
    @Async // 多线程异步执行数据同步到ES
    public void syncDataFromMySQLToES() {
        // 打印当前执行的线程信息
        Thread currentThread = Thread.currentThread();
        log.info("当前执行的线程信息：ID = {}, Name = {}, Priority = {}", currentThread.getId(), currentThread.getName(), currentThread.getPriority());

        esPostIndexRepository.deleteAll(); // 删除所有文档(多余操作，后期优化)

        // 从MySQL中取出需要同步的数据
        List<EsPostIndex> esPostIndexs = PostMapper.selectEsPostIndex();
        
        // 将数据批量同步保存到ES中
        esPostIndexRepository.saveAll(esPostIndexs);

        log.info("数据ES同步完成，共同步 {} 条数据", esPostIndexs.size());
    }
}
