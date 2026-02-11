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
                
                // 业务公开接口（首页相关）
                .requestMatchers(
                    "/hot",           // 热门商品
                    "/new",           // 新品商品
                    "/recommended",   // 推荐商品
                    "/daily-new",     // 每日上新
                    "/hotspots",      // 实时热点
                    "/tomorrow-contents", // 明日内容
                    "/coupons",       // 优惠券
                    
                    // 商品浏览接口
                    "/{id}",          // 商品详情
                    "/{id}/detail",   // 完整详情
                    "/category/{categoryId}", // 分类商品
                    "/seller/{sellerId}"     // 商家商品
                ).permitAll()
                
                // 用户服务公开接口
                .requestMatchers(
                    "/user/api/auth/*",      // 认证接口
                    "/user/api/public/*",    // 公开信息接口
                    "/user/api/debug/health" // 调试健康检查
                ).permitAll()
                
                // 用户服务管理接口 - 需要管理员权限
                .requestMatchers("/user/api/admin/*").hasRole("ADMIN")
                
                // 用户服务用户接口 - 需要认证
                .requestMatchers("/user/api/users/*").authenticated()
                
                // 用户服务默认规则：其他API接口需要认证
                .requestMatchers("/user/api/*").authenticated()
                
                // 默认规则：其他接口需要认证
                .anyRequest().authenticated()
            )
            .build();
    }
}