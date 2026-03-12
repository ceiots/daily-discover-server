package com.dailydiscover.recommendation.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Feign客户端配置类
 * 启用Feign客户端扫描，指定扫描包路径
 */
@Configuration
@EnableFeignClients(basePackages = "com.dailydiscover.recommendation.client")
public class FeignConfig {
}