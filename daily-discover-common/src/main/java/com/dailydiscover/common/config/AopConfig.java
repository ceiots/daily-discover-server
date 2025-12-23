package com.dailydiscover.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * AOP配置类
 * 启用AspectJ自动代理功能
 * 
 */
@Configuration
@EnableAspectJAutoProxy
public class AopConfig {
    // 启用AspectJ自动代理，无需额外配置
}