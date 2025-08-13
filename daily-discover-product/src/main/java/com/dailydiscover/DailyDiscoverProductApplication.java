package com.dailydiscover;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dailydiscover.mapper")
public class DailyDiscoverProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(DailyDiscoverProductApplication.class, args);
    }
}