package com.dailydiscover;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 每日发现用户服务启动类
 */
@SpringBootApplication
@MapperScan("com.dailydiscover.mapper")
public class DailyDiscoverUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(DailyDiscoverUserApplication.class, args);
        System.out.println("🎉 每日发现用户服务启动成功！");
        System.out.println("📱 API文档地址: http://localhost:8091/daily-discover/api");
        System.out.println("🔑 JWT认证已启用");
        System.out.println("☕ 运行环境: JDK 17 + Spring Boot 3.2");
    }
}