package com.qin.catcat.unite.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// import com.qin.catcat.unite.common.rabbitMq.LikeMessageListener;

/**
 * RabbitMQ配置
 * @author qin
 * @date 2024/8/5
 * @version 1.0
 * @since 1.0
 * */

// @Configuration
// @EnableRabbit
// public class RabbitMQConfig {

//     public static final String QUEUE_NAME = "PostLikeQueue";

//     @Bean
//     public Queue queue() {
//         return new Queue(QUEUE_NAME, true);
//     }

//     // 注入连接工厂
//     @Bean
//     public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
//         final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//         return rabbitTemplate;
//     }

//     // 注入监听容器
//     @Bean
//     public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
//                                                     MessageListenerAdapter listenerAdapter) {
//         SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//         container.setConnectionFactory(connectionFactory);
//         container.setQueueNames(QUEUE_NAME);
//         container.setMessageListener(listenerAdapter);
//         return container;
//     }

//     // 注入监听适配器
//     @Bean
//     public MessageListenerAdapter listenerAdapter(LikeMessageListener listener) {
//         return new MessageListenerAdapter(listener, "receiveMessage");
//     }
// }
