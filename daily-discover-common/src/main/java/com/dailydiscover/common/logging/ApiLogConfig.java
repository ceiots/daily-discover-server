package com.dailydiscover.common.logging;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * API日志配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "api.log")
public class ApiLogConfig {
    
    /**
     * 是否启用API日志
     */
    private boolean enabled = true;
    
    /**
     * 需要排除的包路径（逗号分隔）
     */
    private String excludePackages = "";
    
    /**
     * 需要排除的类名（逗号分隔）
     */
    private String excludeClasses = "";
    
    /**
     * 需要排除的方法名（逗号分隔）
     */
    private String excludeMethods = "";
    
    /**
     * 敏感字段列表（逗号分隔），自动脱敏
     */
    private String sensitiveFields = "password,phone,idCard,email,bankCard";
    
    /**
     * 是否启用异步日志记录
     */
    private boolean asyncEnabled = false;
    
    /**
     * 响应体最大记录长度（字符数）
     */
    private int maxResponseLength = 500;
}