package com.dailydiscover.config;

import com.dailydiscover.common.security.SimpleSecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 商品服务安全配置
 * 继承公共安全配置，启用JWT认证
 */
@Configuration
public class SecurityConfig extends SimpleSecurityConfig {

    /**
     * 重写安全配置链，添加商品服务特定的权限配置
     * 使用 @Primary 确保此配置优先于父类配置
     */
    @Bean
    @Primary
    @Override
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return configureBaseSecurity(http)
            .authorizeHttpRequests(authz -> authz
                // 商品公开接口（所有商品相关接口）
                .requestMatchers(
                    "/",                    // 根路径
                    "/hot",                // 热门商品
                    "/new",                // 新品商品
                    "/recommended",        // 推荐商品
                    "/daily-new",          // 每日上新
                    "/hotspots",           // 实时热点
                    "/tomorrow-contents",  // 明日内容
                    "/coupons",            // 优惠券
                    
                    // 评论相关接口
                    "/reviews/**",         // 所有评论接口
                    
                    // 商品浏览接口（使用通配符匹配）
                    "/**"                   // 允许所有路径
                ).permitAll()
                
                // 商品管理接口 - 需要管理员权限
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // 商品操作接口 - 需要认证
                .requestMatchers("/product/action/**").authenticated()
                
                // 默认规则：其他接口需要认证
                .anyRequest().authenticated()
            )
            .build();
    }
}