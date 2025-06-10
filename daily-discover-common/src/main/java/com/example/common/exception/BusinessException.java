package com.example.common.exception;

import com.example.common.result.IResultCode;
import com.example.common.result.ResultCode;
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
    private final IResultCode resultCode;

    /**
     * 构造函数
     *
     * @param message 消息
     */
    public BusinessException(String message) {
        super(message);
        this.resultCode = ResultCode.FAILURE;
    }

    /**
     * 构造函数
     *
     * @param resultCode 返回码
     */
    public BusinessException(IResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    /**
     * 构造函数
     *
     * @param resultCode 返回码
     * @param message    消息
     */
    public BusinessException(IResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    /**
     * 构造函数
     *
     * @param resultCode 返回码
     * @param cause      异常
     */
    public BusinessException(IResultCode resultCode, Throwable cause) {
        super(cause);
        this.resultCode = resultCode;
    }

    /**
     * 构造函数
     *
     * @param resultCode 返回码
     * @param message    消息
     * @param cause      异常
     */
    public BusinessException(IResultCode resultCode, String message, Throwable cause) {
        super(message, cause);
        this.resultCode = resultCode;
    }
} 