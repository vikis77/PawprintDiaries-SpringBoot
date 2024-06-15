package com.qin.catcat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
@MapperScan("com.qin.catcat.unite.mapper")
public class CatcatApplication {
	public static void main(String[] args) {
		SpringApplication.run(CatcatApplication.class, args);
		log.info("server started");
	}

}
