package com.dailydiscover.config;

import org.springframework.context.annotation.Configuration;

/**
 * 商品服务安全配置
 * 直接使用 common 模块的 SimpleSecurityConfig（已配置为 permitAll）
 * 所有接口公开访问
 */
@Configuration
public class SecurityConfig {
    // 商品服务直接使用 common 模块的安全配置
    // SimpleSecurityConfig 已设置为 anyRequest().permitAll()
}
