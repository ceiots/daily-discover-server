package com.example.common.exception;

import com.example.common.result.IResultCode;
import com.example.common.result.ResultCode;

/**
 * 业务异常
 */
public class BusinessException extends RuntimeException {
    private final IResultCode errorCode;

    public BusinessException(IResultCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(String message) {
        super(message);
        this.errorCode = ResultCode.FAILED;
    }

    public BusinessException(IResultCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ResultCode.FAILED;
    }

    public IResultCode getErrorCode() {
        return errorCode;
    }
}