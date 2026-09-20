package com.dailydiscover.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 业务异常：携带 API 错误码与对应 HTTP 状态码
 */
public class BusinessException extends RuntimeException {
    private final int code;
    private final int httpStatus;

    public BusinessException(ErrorCode errorCode, HttpStatus httpStatus) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.httpStatus = httpStatus.value();
    }

    public BusinessException(ErrorCode errorCode, HttpStatus httpStatus, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.httpStatus = httpStatus.value();
    }

    public int getCode() { return code; }
    public int getHttpStatus() { return httpStatus; }
}
