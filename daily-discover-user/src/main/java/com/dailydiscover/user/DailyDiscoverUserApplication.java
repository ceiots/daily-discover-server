package com.dailydiscover.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 每日发现用户服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.dailydiscover.user", "com.dailydiscover.common"})
@MapperScan("com.dailydiscover.user.mapper")
@EnableAspectJAutoProxy
public class DailyDiscoverUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(DailyDiscoverUserApplication.class, args);
        System.out.println("🎉 每日发现用户服务启动成功！");
        System.out.println("📱 API文档地址: http://localhost:8091/user/api");
        System.out.println("🔑 JWT认证已启用");
        System.out.println("☕ 运行环境: JDK 17 + Spring Boot 3.2");
    }
}