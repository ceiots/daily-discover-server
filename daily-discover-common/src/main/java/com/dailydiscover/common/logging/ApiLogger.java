package com.dailydiscover.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * HTTP API日志记录器
 * 专门用于记录HTTP层面的API调用日志，包括URL、HTTP方法、状态码、客户端IP等HTTP协议信息
 * 职责：仅处理HTTP层面信息，不涉及业务逻辑追踪
 */
@Slf4j
@Component
public class ApiLogger {
    
    /**
     * 记录HTTP API调用信息
     */
    public static void logHttpApiCall(String apiDescription, HttpServletRequest request, 
                                     Object response, long duration, boolean success,
                                     boolean logRequest, boolean logResponse, String requestParams) {
        // 性能优化：仅在需要记录时执行
        if (!log.isInfoEnabled()) {
            return;
        }
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("🌐 HTTP API调用 | ").append(apiDescription)
                  .append(" | ").append(timestamp);
        
        // HTTP层面信息（核心职责）
        logBuilder.append(" | URL: ").append(request.getRequestURL().toString());
        logBuilder.append(" | 方法: ").append(request.getMethod());
        logBuilder.append(" | 客户端: ").append(getClientIP(request));
        
        // 请求参数（根据配置决定是否记录）
        if (logRequest && requestParams != null && !requestParams.isEmpty()) {
            logBuilder.append(" | 参数: ").append(requestParams);
        }
        
        // HTTP状态信息
        int statusCode = getResponseStatusCode();
        logBuilder.append(" | 状态码: ").append(statusCode);
        logBuilder.append(" | 状态: ").append(success ? "成功" : "失败");
        
        // 响应内容（根据配置决定是否记录）
        if (logResponse) {
            String responseContent = formatResponseSafely(response);
            logBuilder.append(" | 响应: ").append(responseContent);
        }
        
        // 性能信息
        logBuilder.append(" | 耗时: ").append(duration).append("ms");
        
        log.info(logBuilder.toString());
    }
    
    /**
     * 记录HTTP API异常信息
     */
    public static void logHttpApiException(String apiDescription, HttpServletRequest request, 
                                          Exception exception, long duration, String requestParams) {
        // 性能优化：仅在需要记录时执行
        if (!log.isErrorEnabled()) {
            return;
        }
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("❌ HTTP API异常 | ").append(apiDescription)
                  .append(" | ").append(timestamp);
        
        // HTTP层面信息（核心职责）
        logBuilder.append(" | URL: ").append(request.getRequestURL().toString());
        logBuilder.append(" | 方法: ").append(request.getMethod());
        logBuilder.append(" | 客户端: ").append(getClientIP(request));
        
        // 请求参数
        if (requestParams != null && !requestParams.isEmpty()) {
            logBuilder.append(" | 参数: ").append(requestParams);
        }
        
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
     * 安全格式化响应对象，防止大对象或循环引用导致的性能问题
     */
    private static String formatResponseSafely(Object response) {
        if (response == null) {
            return "null";
        }
        
        try {
            // 限制对象深度和长度，防止性能问题
            String str = response.toString();
            
            // 安全检查：防止无限递归或超大对象
            if (str.length() > 500) {
                return str.substring(0, 500) + "... [内容过长已截断]";
            }
            
            // 检查是否包含内存地址（可能未重写toString）
            if (str.contains("@") && str.matches(".*@[0-9a-f]+.*")) {
                return "[对象实例，详细内容请使用LogTracer追踪]";
            }
            
            return str;
        } catch (Exception e) {
            // 防止toString方法抛出异常
            return "[格式化失败: " + e.getMessage() + "]";
        }
    }
}