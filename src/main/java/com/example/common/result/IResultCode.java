package com.example.common.result;

/**
 * 统一错误码接口
 */
public interface IResultCode {
    /**
     * 获取错误码
     */
    int getCode();

    /**
     * 获取错误信息
     */
    String getMessage();
}