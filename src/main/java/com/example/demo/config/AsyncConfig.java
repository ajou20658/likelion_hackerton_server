//package com.example.demo.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//public class AsyncConfig {
//    @Bean
//    public ThreadPoolTaskExecutor taskExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(1);  // 예: 최소 스레드 개수
//        executor.setMaxPoolSize(1);  // 예: 최대 스레드 개수
//        executor.setQueueCapacity(1); // 예: 대기 큐 크기
//        executor.setThreadNamePrefix("MyThreadPool-"); // 스레드 이름 접두사
//        executor.initialize();
//        return executor;
//    }
//}
