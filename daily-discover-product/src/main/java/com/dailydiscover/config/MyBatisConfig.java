package com.dailydiscover.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.dailydiscover.mapper")
public class MyBatisConfig {
}