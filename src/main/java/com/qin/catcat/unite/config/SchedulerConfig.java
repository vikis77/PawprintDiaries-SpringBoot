package com.qin.catcat.unite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 定时任务配置
 * 针对2核4G服务器优化的线程池配置
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
        // 2核CPU，设置线程池大小为CPU核心数+1
        taskScheduler.setPoolSize(3);
        taskScheduler.setThreadNamePrefix("CatCat-Thread-");
        // 设置线程池关闭时等待任务完成
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        // 由于资源有限，缩短等待时间为30秒
        taskScheduler.setAwaitTerminationSeconds(30);
        return taskScheduler;
    }
}
