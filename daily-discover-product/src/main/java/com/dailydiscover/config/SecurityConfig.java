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
     */
    @Bean
    @Primary
    @Override
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return configureBaseSecurity(http)
            .authorizeHttpRequests(authz -> authz
                // 商品公开接口（所有 /v1/products/** 路径）
                .requestMatchers(
                    "/v1/products/**",     // 所有商品接口
                    "/hot",                // 热门商品
                    "/new",                // 新品商品
                    "/recommended",        // 推荐商品
                    "/daily-new",          // 每日上新
                    "/hotspots",           // 实时热点
                    "/tomorrow-contents",  // 明日内容
                    "/coupons",            // 优惠券
                    
                    // 商品浏览接口（兼容旧路径和Tailscale转发）
                    "/{id}",               // 商品详情
                    "/{id}/detail",       // 完整详情
                    "/{id}/images",        // 商品图片
                    "/{id}/specifications", // 商品规格
                    "/{id}/skus",          // 商品SKU
                    "/{id}/details",       // 商品详情信息
                    "/{id}/reviews",       // 商品评价
                    "/{id}/features",      // 商品特性
                    "/{id}/related",       // 相关商品
                    "/category/{categoryId}",   // 分类商品
                    "/seller/{sellerId}"   // 商家商品
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