package com.newgreen.rabbbitmqsend.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 死信队列配置类
 */
@Configuration
public class DlxQueueConfig {
    //队列名称
    public static final String DLX_QUEUE = "order_dlx_queue";
    //交换机名称
    public static final String DLX_EXCHANGE = "order_dlx_exchange";
    //路由键名称
    public static final String DLX_ROUTING_KEY = "order.dlx.key";
    //队列
    @Bean
    public Queue dlxQueue(){
        //参数1:队列名称,参数2:是否持久化,参数3:是否独有,参数4:是否自动删除
        return new Queue(DLX_QUEUE,true,false,false);
    }
    //交换机
    @Bean
    public DirectExchange dlxExchange(){
        //参数1:交换机名称,参数2:是否持久化,参数3:是否自动删除
        return new DirectExchange(DLX_EXCHANGE,true,false);
    }
    //将队列绑定到交换机和路由键
    @Bean
    public Binding dlxBinding(){
        return BindingBuilder.bind(dlxQueue()).to(dlxExchange()).with(DLX_ROUTING_KEY);
    }
}
