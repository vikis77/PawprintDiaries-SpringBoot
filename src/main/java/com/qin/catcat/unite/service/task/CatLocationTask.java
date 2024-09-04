package com.qin.catcat.unite.service.task;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import com.qin.catcat.unite.controller.handle.CatWebSocketHandler;
import com.qin.catcat.unite.popo.vo.CoordinateVO;
import com.qin.catcat.unite.service.CatService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CatLocationTask {
    // 注入 TaskScheduler，负责任务调度
    private final TaskScheduler taskScheduler;

    // 注入 CatWebSocketHandler，用于处理 WebSocket 连接和消息发送 
    // @Lazy // 使用 @Lazy 注解解决循环依赖问题
    private final CatWebSocketHandler catWebSocketHandler;

    // 保存定时任务的状态，用于跟踪任务是否在运行 
    private  ScheduledFuture<?> scheduledFuture;

    private final CatService catService;

    // 构造器注入
    public CatLocationTask(TaskScheduler taskScheduler, @Lazy CatWebSocketHandler catWebSocketHandler,CatService catService){
        this.taskScheduler = taskScheduler;
        this.catWebSocketHandler = catWebSocketHandler;
        this.catService = catService;
    }

    // 同步方法，检查并启动或停止定时任务
    public synchronized void checkAndStartTask(){
        // 如果有用户订阅，启动定时任务
        if (!catWebSocketHandler.getSubscribedSessions().isEmpty()){
            // 如果定时任务还没启动，或者定时任务已经启动但是已经被取消，才重新启动
            // 如果已经启动了，就不需要重新启动。保证单例模式，只会有一个定时任务
            if (scheduledFuture == null || scheduledFuture.isCancelled()){
                startLocationUpdates(); // 启动定时任务
            } 
        } else {
            stopLocationUpdates(); // 如果没有订阅，停止定时任务
        }
    }

    // 启动定时任务，每5秒执行一次
    private void startLocationUpdates(){
        // 使用 TaskScheduler 启动定时任务，任务内容为 updateCatLocation 方法
        scheduledFuture = taskScheduler.schedule(this::updateCatLocation, new CronTrigger("0/5 * * * * *"));
    }

    // 停止定时任务
    private void stopLocationUpdates(){
        // 如果定时任务还在执行，取消任务
        if (scheduledFuture != null && !scheduledFuture.isCancelled()){
            scheduledFuture.cancel(true);
        }
    }

    // 执行定时任务，更新猫咪位置信息并推送给订阅用户
    private void updateCatLocation(){
        if (catWebSocketHandler.getSubscribedSessions().isEmpty()){
            return;// 如果没有用户订阅，跳过执行
        }
        List<CoordinateVO> locationData = catService.selectCoordinate(); // 获取猫猫当前位置
        catWebSocketHandler.getSubscribedSessions().forEach(session->{// 遍历订阅列表，给每个会话发送消息
            try {
                catWebSocketHandler.sendLocaltionUpdate(session,locationData);
            } catch (Exception e){
                // 如果发送失败，打印错误信息
                log.error("Failed to send location update to session {}: {}", session.getId(), e.getMessage()); 
            }
        });
    }
}
