package com.dailydiscover.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;

@Configuration
@MapperScan("com.dailydiscover.mapper")
public class MyBatisConfig {
    
    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
        return configuration -> {
            // 开启驼峰命名映射
            configuration.setMapUnderscoreToCamelCase(true);
            // 设置日志前缀
            configuration.setLogPrefix("MYBATIS");
            // 其他配置可以根据需要添加
        };
    }
}