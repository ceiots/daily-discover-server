package com.dailydiscover.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 统一安全配置 - 供各业务服务使用
 * 当前阶段：允许首页接口匿名访问，其他接口需要认证
 * 所有服务统一使用此配置，避免重复配置
 */
@Configuration
public class SimpleSecurityConfig {

    /**
     * 配置基础安全设置 - 供子类复用
     */
    protected HttpSecurity configureBaseSecurity(HttpSecurity http) throws Exception {
        return http
            // 基础安全配置
            .csrf(csrf -> csrf.disable())
            
            // 禁用默认认证方式
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());
    }

    /**
     * 统一安全配置 - 包含所有服务的权限配置
     * 所有业务服务都会自动应用此配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return configureBaseSecurity(http)
            // 统一权限配置：包含所有服务的权限规则
            .authorizeHttpRequests(authz -> authz
                // 系统级公开接口
                .requestMatchers(
                    "/actuator/health",
                    "/swagger-ui/**", 
                    "/v3/api-docs/**",
                    "/webjars/**",
                    "/favicon.ico"
                ).permitAll()
                
                // 默认规则：其他接口需要认证
                .anyRequest().authenticated()
            )
            .build();
    }
}