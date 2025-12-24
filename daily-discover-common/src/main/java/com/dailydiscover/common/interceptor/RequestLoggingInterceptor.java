package com.dailydiscover.common.interceptor;

import com.dailydiscover.common.util.LogTracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP请求日志拦截器
 * 用于记录接口请求和响应的详细信息
 */
@Slf4j
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {
    
    private static final ThreadLocal<Long> startTimeHolder = new ThreadLocal<>();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 记录请求开始时间
        startTimeHolder.set(System.currentTimeMillis());
        
        // 记录请求信息
        logRequestInfo(request);
        
        return true;
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 记录响应信息
        logResponseInfo(request, response);
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理ThreadLocal
        startTimeHolder.remove();
        
        // 如果有异常，记录异常信息
        if (ex != null) {
            log.error("❌ 请求处理异常 | URL: {} | 方法: {} | 异常: {}", 
                     request.getRequestURI(), request.getMethod(), ex.getMessage(), ex);
        }
    }
    
    /**
     * 记录请求信息
     */
    private void logRequestInfo(HttpServletRequest request) {
        try {
            StringBuilder requestInfo = new StringBuilder();
            requestInfo.append("\n📥 请求信息:");
            requestInfo.append("\n├─ URL: ").append(request.getRequestURL());
            requestInfo.append("\n├─ 方法: ").append(request.getMethod());
            requestInfo.append("\n├─ 客户端IP: ").append(getClientIpAddress(request));
            requestInfo.append("\n├─ User-Agent: ").append(request.getHeader("User-Agent"));
            
            // 记录请求头
            Map<String, String> headers = getRequestHeaders(request);
            if (!headers.isEmpty()) {
                requestInfo.append("\n├─ 请求头:");
                headers.forEach((key, value) -> 
                    requestInfo.append("\n│  ├─ ").append(key).append(": ").append(value)
                );
            }
            
            // 记录请求参数
            Map<String, String[]> parameters = request.getParameterMap();
            if (!parameters.isEmpty()) {
                requestInfo.append("\n├─ 请求参数:");
                parameters.forEach((key, values) -> {
                    requestInfo.append("\n│  ├─ ").append(key).append(": ");
                    for (int i = 0; i < values.length; i++) {
                        if (i > 0) requestInfo.append(", ");
                        requestInfo.append(values[i]);
                    }
                });
            }
            
            log.info(requestInfo.toString());
            
        } catch (Exception e) {
            log.warn("记录请求信息时发生异常: {}", e.getMessage());
        }
    }
    
    /**
     * 记录响应信息
     */
    private void logResponseInfo(HttpServletRequest request, HttpServletResponse response) {
        try {
            Long startTime = startTimeHolder.get();
            if (startTime == null) return;
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            StringBuilder responseInfo = new StringBuilder();
            responseInfo.append("\n📤 响应信息:");
            responseInfo.append("\n├─ URL: ").append(request.getRequestURI());
            responseInfo.append("\n├─ 状态码: ").append(response.getStatus());
            responseInfo.append("\n├─ 耗时: ").append(duration).append("ms");
            
            // 记录重要的响应头（过滤掉不必要的头信息）
            Map<String, String> importantHeaders = getImportantResponseHeaders(response);
            if (!importantHeaders.isEmpty()) {
                responseInfo.append("\n└─ 重要响应头:");
                importantHeaders.forEach((key, value) -> 
                    responseInfo.append("\n   ├─ ").append(key).append(": ").append(value)
                );
            } else {
                responseInfo.append("\n└─ 响应头: 无重要头信息");
            }
            
            log.info(responseInfo.toString());
            
            // 使用LogTracer记录性能信息
            LogTracer.tracePerformance("HTTP请求处理", startTime, endTime);
            
        } catch (Exception e) {
            log.warn("记录响应信息时发生异常: {}", e.getMessage());
        }
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
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
     * 获取请求头信息
     */
    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.put(headerName, headerValue);
        }
        
        return headers;
    }
    
    /**
     * 获取重要的响应头信息（过滤掉不必要的头）
     */
    private Map<String, String> getImportantResponseHeaders(HttpServletResponse response) {
        Map<String, String> importantHeaders = new HashMap<>();
        
        // 需要过滤掉的不重要头信息
        String[] excludedHeaders = {
            "Vary",
            "X-Content-Type-Options",
            "X-XSS-Protection", 
            "Cache-Control",
            "Pragma",
            "Expires",
            "X-Frame-Options",
            "Transfer-Encoding",
            "Connection",
            "Date"
        };
        
        // 重要的头信息（需要保留的）
        String[] importantHeaderKeys = {
            "Content-Type",
            "Content-Length",
            "Location",
            "Set-Cookie",
            "Authorization",
            "X-Request-ID",
            "X-Trace-ID",
            "X-Response-Time",
            "X-RateLimit-Limit",
            "X-RateLimit-Remaining",
            "X-RateLimit-Reset"
        };
        
        // 首先检查是否有重要的头信息
        for (String headerKey : importantHeaderKeys) {
            String headerValue = response.getHeader(headerKey);
            if (headerValue != null && !headerValue.trim().isEmpty()) {
                importantHeaders.put(headerKey, headerValue);
            }
        }
        
        // 如果没有找到重要头信息，则检查其他非排除的头信息
        if (importantHeaders.isEmpty()) {
            response.getHeaderNames().forEach(headerName -> {
                // 检查是否在排除列表中
                boolean shouldExclude = false;
                for (String excludedHeader : excludedHeaders) {
                    if (excludedHeader.equalsIgnoreCase(headerName)) {
                        shouldExclude = true;
                        break;
                    }
                }
                
                if (!shouldExclude) {
                    String headerValue = response.getHeader(headerName);
                    if (headerValue != null && !headerValue.trim().isEmpty()) {
                        importantHeaders.put(headerName, headerValue);
                    }
                }
            });
        }
        
        return importantHeaders;
    }
}