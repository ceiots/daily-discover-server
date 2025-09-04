package com.dailydiscover;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 每日发现用户服务启动类
 * 
 * @author Daily Discover Team
 * @since 2024-01-01
 */
@SpringBootApplication
@MapperScan("com.dailydiscover.mapper")
public class DailyDiscoverUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(DailyDiscoverUserApplication.class, args);
        System.out.println("🎉 每日发现用户服务启动成功！");
        System.out.println("📱 API文档地址: http://localhost:8090/api/user");
        System.out.println("🔑 JWT认证已启用");
        System.out.println("☕ 运行环境: JDK 17 + Spring Boot 3.2");
    }
}