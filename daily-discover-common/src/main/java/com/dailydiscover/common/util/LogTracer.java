package com.dailydiscover.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.HashMap;

/**
 * 统一日志追踪工具类
 * 用于统一输出入参信息、响应信息、数据库查询结果等
 * 所有微服务通用的日志追踪工具
 */
@Slf4j
@Component
public class LogTracer {
    
    private static final String SEPARATOR = "========================================";
    private static final String SUB_SEPARATOR = "----------------------------------------";
    
    /**
     * 获取调用位置信息
     */
    private static String getCallerLocation() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        // 跳过LogTracer类本身的方法调用
        for (int i = 3; i < stackTrace.length; i++) {
            StackTraceElement element = stackTrace[i];
            if (!element.getClassName().contains("LogTracer")) {
                return String.format("%s.%s(%s:%d)", 
                    element.getClassName(),
                    element.getMethodName(),
                    element.getFileName(),
                    element.getLineNumber());
            }
        }
        return "Unknown Location";
    }
    
    /**
     * 追踪方法调用（包含入参和出参）
     * @param methodName 方法名
     * @param params 入参信息
     * @param result 返回结果
     */
    public static <T> void traceMethod(String methodName, Object params, T result) {
        String callerLocation = getCallerLocation();
        log.info("📋 方法追踪 | 位置: {} | 方法: {} | 入参: {} | 出参: {}", callerLocation, methodName, params, result);
    }
    
    /**
     * 追踪数据库查询
     * @param sql SQL语句
     * @param params 查询参数
     * @param result 查询结果
     */
    public static <T> void traceDatabaseQuery(String sql, Object params, T result) {
        String callerLocation = getCallerLocation();
        log.info("🗃️  数据库查询 | 位置: {} | SQL: {} | 参数: {} | 结果: {}", callerLocation, sql, params, result);
    }
    
    /**
     * 追踪API调用
     * @param apiName API名称
     * @param data 请求或响应数据
     */
    public static void traceApiCall(String apiName, Object data) {
        String callerLocation = getCallerLocation();
        
        // 优化日志格式，提取关键信息
        String optimizedData = optimizeLogData(data);
        
        log.info("🌐 API调用 | 位置: {} | API: {} | 数据: {}", callerLocation, apiName, optimizedData);
    }
    
    /**
     * 优化日志数据，保持原始格式
     */
    private static String optimizeLogData(Object data) {
        if (data == null) {
            return "无数据";
        }
        
        // 直接返回原始数据，保持与接口返回格式一致
        return data.toString();
    }
    
    /**
     * 追踪业务操作
     * @param operation 操作描述
     * @param details 操作详情
     */
    public static void traceBusinessOperation(String operation, Object details) {
        String callerLocation = getCallerLocation();
        log.info("💼 业务操作 | 位置: {} | 操作: {} | 详情: {}", callerLocation, operation, details);
    }
    
    /**
     * 追踪异常信息
     * @param methodName 方法名
     * @param params 入参
     * @param exception 异常信息
     */
    public static void traceException(String methodName, Object params, Exception exception) {
        String callerLocation = getCallerLocation();
        log.error("❌ 异常追踪 | 位置: {} | 方法: {} | 入参: {} | 异常: {}", callerLocation, methodName, params, exception.getMessage());
    }
    
    /**
     * 性能追踪
     * @param methodName 方法名
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    public static void tracePerformance(String methodName, long startTime, long endTime) {
        String callerLocation = getCallerLocation();
        long duration = endTime - startTime;
        log.info("⚡ 性能追踪 | 位置: {} | 方法: {} | 耗时: {}ms", callerLocation, methodName, duration);
    }
    
    /**
     * 创建追踪上下文（用于链式调用追踪）
     * @param traceId 追踪ID
     * @return 追踪上下文
     */
    public static TraceContext createTraceContext(String traceId) {
        return new TraceContext(traceId);
    }
    
    /**
     * 追踪上下文类
     */
    public static class TraceContext {
        private final String traceId;
        private final Map<String, Object> context = new HashMap<>();
        
        public TraceContext(String traceId) {
            this.traceId = traceId;
        }
        
        public TraceContext add(String key, Object value) {
            context.put(key, value);
            return this;
        }
        
        public void trace(String operation) {
            String callerLocation = getCallerLocation();
            StringBuilder contextInfo = new StringBuilder();
            context.forEach((key, value) -> 
                contextInfo.append(key).append(": ").append(value).append(", ")
            );
            if (contextInfo.length() > 0) {
                contextInfo.setLength(contextInfo.length() - 2); // 移除最后的逗号和空格
            }
            log.info("🔗 链式追踪 | 位置: {} | 追踪ID: {} | 操作: {} | 上下文: {}", callerLocation, traceId, operation, contextInfo.toString());
        }
    }
}