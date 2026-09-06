package com.dailydiscover;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 每日发现服务端 MVP 入口
 */
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.dailydiscover")
@SpringBootApplication(scanBasePackages = "com.dailydiscover")
public class DailyDiscoverApplication {

    public static void main(String[] args) {
        SpringApplication.run(DailyDiscoverApplication.class, args);
    }
}