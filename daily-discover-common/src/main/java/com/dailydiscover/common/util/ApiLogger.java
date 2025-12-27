package com.dailydiscover.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * HTTP API日志记录器
 * 专门用于记录HTTP层面的API调用日志，包括请求/响应信息、状态码、性能等
 * 职责：HTTP协议层面的日志记录
 */
@Slf4j
@Component
public class ApiLogger {
    
    /**
     * 记录HTTP API调用信息
     */
    public static void logHttpApiCall(String apiDescription, HttpServletRequest request, Object response, long duration, boolean success) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("🌐 HTTP API调用 | ").append(apiDescription)
                  .append(" | ").append(timestamp);
        
        // HTTP层面信息
        logBuilder.append(" | URL: ").append(request.getRequestURL());
        logBuilder.append(" | 方法: ").append(request.getMethod());
        logBuilder.append(" | 客户端: ").append(getClientIP(request));
        
        // HTTP状态信息
        int statusCode = getResponseStatusCode();
        logBuilder.append(" | 状态码: ").append(statusCode);
        logBuilder.append(" | 状态: ").append(success ? "成功" : "失败");
        
        // 响应内容（简化版）
        String responseContent = formatResponse(response);
        logBuilder.append(" | 响应: ").append(responseContent);
        
        // 性能信息
        logBuilder.append(" | 耗时: ").append(duration).append("ms");
        
        log.info(logBuilder.toString());
    }
    
    /**
     * 记录HTTP API异常信息
     */
    public static void logHttpApiException(String apiDescription, HttpServletRequest request, Exception exception, long duration) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("❌ HTTP API异常 | ").append(apiDescription)
                  .append(" | ").append(timestamp);
        
        // HTTP层面信息
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
    
    /**
     * 格式化响应对象为字符串（简化版，仅用于HTTP层面）
     */
    private static String formatResponse(Object response) {
        if (response == null) {
            return "null";
        }
        
        // 简化处理：直接使用toString，限制长度
        String str = response.toString();
        return str.length() > 100 ? str.substring(0, 100) + "..." : str;
    }
}