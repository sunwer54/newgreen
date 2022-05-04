package com.newgreen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRedisHttpSession(redisNamespace = "user")
@EnableDiscoveryClient
@EnableFeignClients
public class SeckillWebApp {
    public static void main(String[] args) {
        SpringApplication.run(SeckillWebApp.class);
    }
}
