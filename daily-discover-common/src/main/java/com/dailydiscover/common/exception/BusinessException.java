package com.dailydiscover.common.exception;

import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final int errorCode;

    /**
     * 构造函数
     *
     * @param message 消息
     */
    public BusinessException(String message) {
        super(message);
        this.errorCode = 400;
    }

    /**
     * 构造函数
     *
     * @param errorCode 错误码
     * @param message   消息
     */
    public BusinessException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     *
     * @param errorCode 错误码
     * @param message   消息
     * @param cause     异常
     */
    public BusinessException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}