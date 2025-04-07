// 文件路径: src/main/java/com/example/SpringbootApplication.java
package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableTransactionManagement
@SpringBootApplication
@MapperScan("com.example.mapper")
@EnableScheduling
public class DailyDiscoverApplication {

	public static void main(String[] args) {
		SpringApplication.run(DailyDiscoverApplication.class, args);
	}
 	@Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}