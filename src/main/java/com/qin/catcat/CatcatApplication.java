package com.qin.catcat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.ConfigurableApplicationContext;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@SpringBootApplication
@Slf4j
@EnableCaching //启用缓存支持
@EnableScheduling
@EnableAsync // 开启异步任务
//@ComponentScan(basePackages = {"com.qin.catcat", "com.randb.springaichatstarter"})
public class CatcatApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(CatcatApplication.class, args);
		
		// 检查核心AI相关Bean是否正确注册
		log.info("==== 检查AI相关Bean ====");
		checkBean(context, "chatModel", "org.springframework.ai.chat.model.ChatModel");
		checkBean(context, "chatClient", "org.springframework.ai.chat.client.ChatClient");
		checkBean(context, "dashscopeChatModel", "com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel");
		checkBean(context, "qwen", "com.randb.springaichatstarter.core.ChatService");
		
		// 列出所有自动配置相关的Bean
		// log.info("==== 自动配置类 ====");
		// Arrays.stream(context.getBeanDefinitionNames())
		// 	.filter(name -> name.toLowerCase().contains("config") || name.toLowerCase().contains("auto"))
		// 	.filter(name -> !name.startsWith("org.springframework.context") && !name.startsWith("org.springframework.boot.context"))
		// 	.forEach(name -> log.info("配置Bean: {} -> {}", name, context.getBean(name).getClass().getName()));
	}
	
	private static void checkBean(ConfigurableApplicationContext context, String beanName, String className) {
		try {
			Class<?> clazz = Class.forName(className);
			boolean containsBean = context.containsBean(beanName);
			log.info("Bean '{}' ({}): {}", beanName, className, containsBean ? "已注册" : "未注册");
			
			if (containsBean) {
				Object bean = context.getBean(beanName);
				log.info("  - 实际类型: {}", bean.getClass().getName());
			} else {
				// 检查是否有此类型的Bean，只是名称不同
				String[] beanNames = context.getBeanNamesForType(clazz, true, false);
				if (beanNames.length > 0) {
					log.info("  - 找到类型匹配的Bean: {}", String.join(", ", beanNames));
				}
			}
		} catch (ClassNotFoundException e) {
			log.warn("类 {} 不存在", className);
		} catch (Exception e) {
			log.error("检查Bean时出错: {}", e.getMessage());
		}
	}
}
