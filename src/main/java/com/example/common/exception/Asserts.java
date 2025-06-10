package com.example.common.exception;

import com.example.common.result.IResultCode;
import org.springframework.util.StringUtils;

/**
 * 断言处理类，用于抛出各种API异常
 */
public class Asserts {
    /**
     * 断言为真，如果为假，则抛出异常
     */
    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            throw new BusinessException(message);
        }
    }
    
    /**
     * 断言为真，如果为假，则抛出异常
     */
    public static void isTrue(boolean condition, IResultCode errorCode, String message) {
        if (!condition) {
            throw new BusinessException(errorCode, message);
        }
    }
    
    /**
     * 断言为假，如果为真，则抛出异常
     */
    public static void isFalse(boolean condition, String message) {
        if (condition) {
            throw new BusinessException(message);
        }
    }
    
    /**
     * 断言为假，如果为真，则抛出异常
     */
    public static void isFalse(boolean condition, IResultCode errorCode, String message) {
        if (condition) {
            throw new BusinessException(errorCode, message);
        }
    }
    
    /**
     * 断言对象不为空，如果为空，则抛出异常
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new BusinessException(message);
        }
    }
    
    /**
     * 断言对象不为空，如果为空，则抛出异常
     */
    public static void notNull(Object object, IResultCode errorCode, String message) {
        if (object == null) {
            throw new BusinessException(errorCode, message);
        }
    }
    
    /**
     * 断言字符串不为空，如果为空，则抛出异常
     */
    public static void notEmpty(String str, String message) {
        if (!StringUtils.hasText(str)) {
            throw new BusinessException(message);
        }
    }
    
    /**
     * 断言字符串不为空，如果为空，则抛出异常
     */
    public static void notEmpty(String str, IResultCode errorCode, String message) {
        if (!StringUtils.hasText(str)) {
            throw new BusinessException(errorCode, message);
        }
    }
    
    /**
     * 直接抛出异常
     */
    public static void fail(String message) {
        throw new BusinessException(message);
    }
    
    /**
     * 直接抛出异常
     */
    public static void fail(IResultCode errorCode) {
        throw new BusinessException(errorCode);
    }
    
    /**
     * 直接抛出异常
     */
    public static void fail(IResultCode errorCode, String message) {
        throw new BusinessException(errorCode, message);
    }
} 