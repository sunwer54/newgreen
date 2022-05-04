package com.newgreen.rabbbitmqsend.send;

import com.newgreen.rabbbitmqsend.config.TTLQueueConfig;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Sender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 订单消息发送者
     * @param msg
     */
    public void sendOrderMsg(String msg, MessagePostProcessor messagePostProcessor){
        rabbitTemplate.convertAndSend(TTLQueueConfig.TTL_EXCHANGE,"order.ttl",msg,messagePostProcessor);
        System.out.println("消息发送成功");
    }
}
