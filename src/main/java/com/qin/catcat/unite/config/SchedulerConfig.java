package com.qin.catcat.unite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 定时任务配置
 * @author qin
 * @date 2024/9/4
 * @version 1.0
 * @since 1.0
 * */
@Configuration
public class SchedulerConfig {
    
    @Bean
    public ThreadPoolTaskScheduler taskScheduler(){
        // 定时任务线程池
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10); // 设置线程池大小
        taskScheduler.setThreadNamePrefix("CatLocationTask-");// 设置线程名前缀
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);// 设置线程池关闭时等待任务完成，保证所有任务都执行完
        taskScheduler.setAwaitTerminationSeconds(60);// 设置线程池关闭时等待时间，超时后线程池会自动销毁
        return taskScheduler;
    }
}
