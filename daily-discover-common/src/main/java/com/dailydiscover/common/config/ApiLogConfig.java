package com.dailydiscover.common.config;

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
     * 是否记录请求参数
     */
    private boolean logRequest = true;
    
    /**
     * 是否记录响应结果
     */
    private boolean logResponse = true;
    
    /**
     * 是否记录执行时间
     */
    private boolean logExecutionTime = true;
    
    /**
     * 是否记录异常信息
     */
    private boolean logException = true;
    
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
}