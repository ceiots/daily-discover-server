package com.dailydiscover.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * API日志记录器
 * 简化版日志记录器，提供统一的API调用日志输出
 */
@Slf4j
@Component
public class ApiLogger {
    
    /**
     * 记录API调用信息
     */
    public static void logApiCall(String apiDescription, HttpServletRequest request, Object response, long duration, boolean success) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("API调用 | ").append(apiDescription)
                  .append(" | ").append(timestamp);
        
        // 基本信息
        logBuilder.append(" | URL: ").append(request.getRequestURL());
        logBuilder.append(" | 方法: ").append(request.getMethod());
        logBuilder.append(" | 客户端: ").append(getClientIP(request));
        
        // 业务状态
        logBuilder.append(" | 状态: ").append(success ? "成功" : "失败");
        
        // 响应码信息
        int statusCode = getResponseStatusCode();
        logBuilder.append(" | 响应码: ").append(statusCode);
        
        // 性能信息
        logBuilder.append(" | 耗时: ").append(duration).append("ms");
        
        log.info(logBuilder.toString());
    }
    
    /**
     * 记录异常信息
     */
    public static void logException(String apiDescription, HttpServletRequest request, Exception exception, long duration) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("API异常 | ").append(apiDescription)
                  .append(" | ").append(timestamp);
        
        // 基本信息
        logBuilder.append(" | URL: ").append(request.getRequestURL());
        logBuilder.append(" | 方法: ").append(request.getMethod());
        logBuilder.append(" | 客户端: ").append(getClientIP(request));
        
        // 异常信息
        logBuilder.append(" | 异常: ").append(exception.getMessage());
        
        // 性能信息
        logBuilder.append(" | 耗时: ").append(duration).append("ms");
        
        log.error(logBuilder.toString());
    }
    
    /**
     * 获取客户端IP地址
     */
    private static String getClientIP(HttpServletRequest request) {
        String[] headers = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
        };
        
        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * 获取HTTP响应状态码
     */
    private static int getResponseStatusCode() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) 
                RequestContextHolder.getRequestAttributes();
            if (attributes != null && attributes.getResponse() != null) {
                return attributes.getResponse().getStatus();
            }
        } catch (Exception e) {
            // 忽略异常，返回默认状态码
        }
        return 200; // 默认返回200
    }
}