package com.newgreen.seckillservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {
    @Bean
    public Executor executor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置线程池核心线程数量
        executor.setCorePoolSize(10);
        //配置线程池最大线程数量
        executor.setMaxPoolSize(100);
        //配置线程池任务队列容量(即队列中可以暂存的任务的最大数量)
        executor.setQueueCapacity(200);
        //配置线程池中线程的最大空闲时间
        executor.setKeepAliveSeconds(3600);
        //设置线程池中线程的名称前缀
        executor.setThreadNamePrefix("order-");
        /*配置线程池饱和策略：即当线程池中的核心线程都不空闲且达到任务处理
          最大线程数，任务队列存储的任务也达到了最大数量，此时线程池处于饱和状态。
          当达到线程池饱和状态时，超过饱和状态下的任务的处理策略：
          CallerRunsPolicy():不在新线程中执行任务,而是在调用者所在的线程中执行*/
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }
}
