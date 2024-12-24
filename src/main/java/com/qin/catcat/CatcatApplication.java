package com.qin.catcat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
@EnableCaching //启用缓存支持
@EnableScheduling
@EnableAsync // 开启异步任务
public class CatcatApplication {
	public static void main(String[] args) {
		SpringApplication.run(CatcatApplication.class, args);
		log.info("server started");
	}

}
