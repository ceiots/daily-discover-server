package com.dailydiscover.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 通用模块应用启动类
 * 可作为独立服务运行，也可作为公共库被其他服务引入
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.dailydiscover"})
public class CommonApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommonApplication.class, args);
    }
}