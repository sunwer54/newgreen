package com.newgreen;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.newgreen.mapper")
@EnableScheduling //开启spring的定时任务
@EnableAsync //开启异步任务
public class SeckillServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(SeckillServiceApp.class);
    }
}
