package com.dailydiscover.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.HashMap;

/**
 * 业务日志追踪工具类
 * 专门用于记录业务层面的方法调用、数据库操作、业务流程等
 * 职责：业务逻辑层面的日志追踪
 */
@Slf4j
@Component
public class LogTracer {
    
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
     * 追踪业务方法调用（包含入参和出参）
     * @param params 入参信息
     * @param result 返回结果
     */
    public static <T> void traceBusinessMethod(Object params, T result) {
        // 性能优化：懒加载，仅在需要记录时执行
        if (!log.isInfoEnabled()) {
            return;
        }
        
        String callerLocation = getCallerLocation();
        String methodName = extractMethodNameFromStackTrace();
        log.info("📋 业务方法追踪 | 位置: {} | 方法: {} | 入参: {} | 出参: {}", callerLocation, methodName, params, result);
    }
    
    /**
     * 追踪业务方法调用（仅入参）
     * @param params 入参信息
     */
    public static void traceBusinessMethodWithParams(Object params) {
        // 性能优化：懒加载，仅在需要记录时执行
        if (!log.isInfoEnabled()) {
            return;
        }
        traceBusinessMethod(params, null);
    }
    
    /**
     * 追踪业务方法调用（仅出参）
     * @param result 返回结果
     */
    public static <T> void traceBusinessMethodWithResult(T result) {
        // 性能优化：懒加载，仅在需要记录时执行
        if (!log.isInfoEnabled()) {
            return;
        }
        traceBusinessMethod(null, result);
    }
    
    /**
     * 从调用栈中提取方法名
     */
    private static String extractMethodNameFromStackTrace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        // 跳过LogTracer类本身的方法调用
        for (int i = 3; i < stackTrace.length; i++) {
            StackTraceElement element = stackTrace[i];
            if (!element.getClassName().contains("LogTracer")) {
                return element.getClassName() + "." + element.getMethodName();
            }
        }
        return "Unknown Method";
    }
    
    /**
     * 追踪数据库查询
     * @param sql SQL语句
     * @param params 查询参数
     * @param result 查询结果
     */
    public static <T> void traceDatabaseQuery(String sql, Object params, T result) {
        // 性能优化：懒加载，仅在需要记录时执行
        if (!log.isInfoEnabled()) {
            return;
        }
        String callerLocation = getCallerLocation();
        String methodName = extractMethodNameFromStackTrace();
        log.info("🗃️  数据库查询 | 位置: {} | 方法: {} | SQL: {} | 参数: {} | 结果: {}", callerLocation, methodName, sql, params, result);
    }
    
    /**
     * 追踪业务API调用（业务层面，非HTTP层面）
     * @param data 请求或响应数据
     */
    public static void traceBusinessApiCall(Object data) {
        // 性能优化：懒加载，仅在需要记录时执行
        if (!log.isInfoEnabled()) {
            return;
        }
        String callerLocation = getCallerLocation();
        String methodName = extractMethodNameFromStackTrace();
        
        // 优化日志格式，提取关键信息
        String optimizedData = optimizeLogData(data);
        
        log.info("💼 业务API调用 | 位置: {} | 方法: {} | 数据: {}", callerLocation, methodName, optimizedData);
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
     * @param details 操作详情
     */
    public static void traceBusinessOperation(Object details) {
        // 性能优化：懒加载，仅在需要记录时执行
        if (!log.isInfoEnabled()) {
            return;
        }
        String callerLocation = getCallerLocation();
        String methodName = extractMethodNameFromStackTrace();
        log.info("💼 业务操作 | 位置: {} | 方法: {} | 详情: {}", callerLocation, methodName, details);
    }
    
    /**
     * 追踪业务异常信息
     * @param exception 异常对象
     */
    public static void traceBusinessException(Exception exception) {
        // 性能优化：懒加载，仅在需要记录时执行
        if (!log.isErrorEnabled()) {
            return;
        }
        String callerLocation = getCallerLocation();
        log.error("❌ 业务异常追踪 | 位置: {} | 异常类型: {} | 异常信息: {}", callerLocation, exception.getClass().getSimpleName(), exception.getMessage());
    }
    
    /**
     * 追踪业务性能信息
     * @param startTime 开始时间
     */
    public static void traceBusinessPerformance(long startTime) {
        // 性能优化：懒加载，仅在需要记录时执行
        if (!log.isInfoEnabled()) {
            return;
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        String callerLocation = getCallerLocation();
        String methodName = extractMethodNameFromStackTrace();
        
        String performanceLevel;
        if (duration < 100) {
            performanceLevel = "⚡ 快速";
        } else if (duration < 500) {
            performanceLevel = "🐢 一般";
        } else {
            performanceLevel = "🐌 缓慢";
        }
        
        log.info("⏱️  业务性能追踪 | 位置: {} | 方法: {} | 耗时: {}ms | 级别: {}", callerLocation, methodName, duration, performanceLevel);
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