package com.dailydiscover.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        
        // 响应内容信息
        if (response != null) {
            String responseStr = formatResponse(response);
            logBuilder.append(" | 响应: ").append(responseStr);
        }
        
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
    
    /**
     * 格式化响应对象为字符串
     */
    private static String formatResponse(Object response) {
        if (response == null) {
            return "null";
        }
        
        try {
            // 如果是字符串，直接返回
            if (response instanceof String) {
                String str = (String) response;
                return str.length() > 100 ? str.substring(0, 100) + "..." : str;
            }
            
            // 如果是简单对象，使用toString
            if (response.getClass().isPrimitive() || 
                response instanceof Number || 
                response instanceof Boolean) {
                return response.toString();
            }
            
            // 对于复杂对象，提取关键信息
            StringBuilder sb = new StringBuilder();
            sb.append(response.getClass().getSimpleName());
            
            // 尝试获取常见业务对象的字段信息
            try {
                java.lang.reflect.Field[] fields = response.getClass().getDeclaredFields();
                sb.append("{");
                int fieldCount = 0;
                for (java.lang.reflect.Field field : fields) {
                    if (fieldCount >= 3) { // 最多显示3个字段
                        sb.append("...");
                        break;
                    }
                    
                    field.setAccessible(true);
                    Object value = field.get(response);
                    
                    if (fieldCount > 0) {
                        sb.append(", ");
                    }
                    
                    sb.append(field.getName()).append("=");
                    if (value != null) {
                        String valueStr = value.toString();
                        if (valueStr.length() > 20) {
                            valueStr = valueStr.substring(0, 20) + "...";
                        }
                        sb.append(valueStr);
                    } else {
                        sb.append("null");
                    }
                    
                    fieldCount++;
                }
                sb.append("}");
            } catch (Exception e) {
                // 如果反射失败，使用简单的toString
                String str = response.toString();
                if (str.length() > 100) {
                    str = str.substring(0, 100) + "...";
                }
                sb = new StringBuilder(str);
            }
            
            return sb.toString();
        } catch (Exception e) {
            // 如果所有方法都失败，使用简单的toString
            String str = response.toString();
            return str.length() > 100 ? str.substring(0, 100) + "..." : str;
        }
    }
    
    /**
     * 记录信息级别日志
     */
    public static void info(String message, Object... args) {
        log.info(message, args);
    }
    
    /**
     * 记录警告级别日志
     */
    public static void warn(String message, Object... args) {
        log.warn(message, args);
    }
    
    /**
     * 记录错误级别日志
     */
    public static void error(String message, Object... args) {
        log.error(message, args);
    }
    
    /**
     * 记录错误级别日志（包含异常）
     */
    public static void error(String message, Throwable throwable) {
        log.error(message, throwable);
    }
}