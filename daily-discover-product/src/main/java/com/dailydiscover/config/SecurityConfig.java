package com.dailydiscover.config;

import com.dailydiscover.common.security.SimpleSecurityConfig;
import org.springframework.context.annotation.Configuration;

/**
 * 商品服务安全配置
 * 继承公共安全配置，启用JWT认证
 */
@Configuration
public class SecurityConfig extends SimpleSecurityConfig {
    // 自动继承父类的统一安全配置，包含JwtAuthenticationFilter和权限规则
}