package com.newgreen.rabbbitmqsend.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 延时队列配置类
 */
@Configuration
public class TTLQueueConfig {
    //队列名称
    public static final String TTL_QUEUE = "order_ttl_queue";
    //交换机名称
    public static final String TTL_EXCHANGE = "order_ttl_exchange";
    //路由键名称
    public static final String TTL_ROUTING_KEY = "#.ttl";
    //队列
    @Bean
    public Queue ttlQueue(){
        //配置队列的相关参数
        Map<String,Object> args = new HashMap<>();
        //消息过期时间配置：120s（"x-message-ttl"是固定写法，不能随便写）
        args.put("x-message-ttl",120*1000);
        //指定转发到死信队列的交换机的名称（"x-dead-letter-exchange"该名称是固定此写法，不能随便写）
        args.put("x-dead-letter-exchange",DlxQueueConfig.DLX_EXCHANGE);
        //指定转发到死信队列的路由键的名称（"x-dead-letter-routing-key"该名称是固定此写法，不能随便写）
        args.put("x-dead-letter-routing-key",DlxQueueConfig.DLX_ROUTING_KEY);
        //参数1:队列名称,参数2:是否持久化,参数3:是否独有,参数4:是否自动删除,参数5:队列配置参数
        return new Queue(TTL_QUEUE,true,false,false,args);
    }
    //交换机
    @Bean
    public TopicExchange ttlExchange(){
        //参数1:交换机名称,参数2:是否持久化,参数3:是否自动删除
        return new TopicExchange(TTL_EXCHANGE,true,false);
    }
    //队列绑定交换机和路由键
    @Bean
    public Binding ttlBinding(){
        return BindingBuilder.bind(ttlQueue()).to(ttlExchange()).with(TTL_ROUTING_KEY);
    }
}
