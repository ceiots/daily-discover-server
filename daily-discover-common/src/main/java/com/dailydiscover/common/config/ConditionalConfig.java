package com.dailydiscover.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * 条件配置类
 * 根据运行模式自动启用或禁用特定配置
 */
public class ConditionalConfig {

    /**
     * 独立服务模式下的Web配置
     */
    @Configuration
    @ConditionalOnProperty(name = "daily-discover.common.mode", havingValue = "standalone", matchIfMissing = false)
    public static class StandaloneWebConfig {
        // 独立服务模式下的Web相关配置
    }

    /**
     * 库模式下的配置
     */
    @Configuration
    @ConditionalOnProperty(name = "daily-discover.common.mode", havingValue = "library", matchIfMissing = true)
    public static class LibraryConfig {
        // 库模式下的配置
    }
}