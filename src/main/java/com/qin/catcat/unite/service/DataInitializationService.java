package com.qin.catcat.unite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.qin.catcat.unite.common.constant.Constant;
import com.qin.catcat.unite.common.utils.CacheUtils;
import com.qin.catcat.unite.manage.CatManage;
import com.qin.catcat.unite.manage.PostManage;
// import com.qin.catcat.unite.repository.EsPostIndexRepository;

import org.redisson.api.RedissonClient;
import org.redisson.api.RBloomFilter;
import org.springframework.scheduling.annotation.Scheduled;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.springframework.context.annotation.Bean;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/* 
 * 初始化数据，在应用启动时
 */
@Service
@Slf4j
public class DataInitializationService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate; // Redis 模板
    @Autowired 
    CatService catService;
    @Autowired 
    PostService postService;
    @Autowired 
    CacheUtils cacheUtils;
    @Autowired
    CatManage catManage;
    @Autowired
    PostManage postManage;
    @Autowired
    CatAnalysisService catAnalysisService;
    @Autowired
    CatLocationService catLocationService;
    // @Autowired
    // EsPostIndexRepository esPostIndexRepository;

    // 初始化完成标志
    private volatile boolean initialized = false;
    // 初始化锁
    private final Object initLock = new Object();

    // 提前写入缓存，做数据预热
    @EventListener(ApplicationReadyEvent.class) // 监听应用启动事件
    public void init() {
        // 使用了双重检查锁定模式来确保初始化只执行一次
        if (!initialized) {
            synchronized (initLock) {
                if (!initialized) {
                    try {
                        // 清空全部缓存
                        // cacheUtils.clearAllCache();
                        // log.info("清空全部缓存完成\n");
                        // 查询全部猫猫数据，提前写入缓存
                        log.info("开始预热全部猫猫数据 - 查询全部猫猫数据（实体类），写入缓存");
                        cacheUtils.put(Constant.HOT_FIRST_TIME_CAT_LIST, catManage.getCatList());
                        log.info("开始预热全部猫猫数据 - 查询猫爪页面猫猫信息，写入缓存");
                        cacheUtils.put(Constant.CAT_LIST_FOR_CATCLAW, catManage.getCatListForCatClaw());
                        log.info("预热全部猫猫数据完成\n");
                        // 热点小猫分析数据
                        log.info("开始预热小猫分析数据 - 查询小猫分析数据，写入缓存");
                        catAnalysisService.analysis();
                        log.info("预热小猫分析数据完成\n");
                        // 预热全部帖子数据
                        log.info("开始预热全部帖子数据 - 查询全部帖子数据，写入缓存");
                        postManage.queryAllPosts();
                        log.info("预热全部帖子数据完成\n");
                        // 预热首页权重帖子数据
                        log.info("开始预热首页权重帖子数据 - 查询权重帖子数据，写入缓存");
                        postManage.initOrUpdateRandomWeightedPosts();
                        log.info("预热首页权重帖子数据完成\n");
                        // 预热所有猫咪最新位置
                        log.info("开始预热所有猫咪最新位置 - 查询所有猫咪最新位置，写入缓存");
                        catLocationService.selectCoordinate();
                        log.info("预热所有猫咪最新位置完成\n");
                        // 预热ES索引
                        // log.info("正在删除旧的ES索引 - 删除ES索引");
                        // esPostIndexRepository.deleteAll(); // 暂时停用ES
                        // log.info("删除ES索引完成\n");
                        // log.info("开始保存新的ES索引 - 保存新的ES索引"); // 这里不需要保存，定时任务会自己保存
                        
                        initialized = true;
                        initLock.notifyAll(); // 通知等待的线程初始化已完成
                    } catch (Exception e) {
                        log.error("数据预热过程中发生错误: ", e);
                        throw new RuntimeException("数据预热失败", e);
                    }
                }
            }
        }
    }

    // 等待初始化完成
    public void waitForInitialization() {
        // 如果初始化未完成，则等待初始化完成
        if (!initialized) {
            synchronized (initLock) {
                while (!initialized) {
                    try {
                        log.info("等待初始化完成...");
                        initLock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("等待初始化完成时被中断", e);
                    }
                }
            }
        }
    }
}
