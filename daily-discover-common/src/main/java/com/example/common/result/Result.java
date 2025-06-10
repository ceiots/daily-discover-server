package com.example.common.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一返回结果
 *
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 时间戳
     */
    private long timestamp = System.currentTimeMillis();

    private Result(IResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    private Result(IResultCode resultCode, T data) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    private Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 返回成功
     *
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS);
    }

    /**
     * 返回成功
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return Result
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS, data);
    }

    /**
     * 返回成功
     *
     * @param message 消息
     * @param <T>     数据类型
     * @return Result
     */
    public static <T> Result<T> success(String message) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message);
    }

    /**
     * 返回成功
     *
     * @param message 消息
     * @param data    数据
     * @param <T>     数据类型
     * @return Result
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 返回失败
     *
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> failure() {
        return new Result<>(ResultCode.FAILURE);
    }

    /**
     * 返回失败
     *
     * @param message 消息
     * @param <T>     数据类型
     * @return Result
     */
    public static <T> Result<T> failure(String message) {
        return new Result<>(ResultCode.FAILURE.getCode(), message);
    }

    /**
     * 返回失败
     *
     * @param resultCode 返回码
     * @param <T>        数据类型
     * @return Result
     */
    public static <T> Result<T> failure(IResultCode resultCode) {
        return new Result<>(resultCode);
    }

    /**
     * 返回失败
     *
     * @param resultCode 返回码
     * @param message    消息
     * @param <T>        数据类型
     * @return Result
     */
    public static <T> Result<T> failure(IResultCode resultCode, String message) {
        return new Result<>(resultCode.getCode(), message);
    }

    /**
     * 返回失败
     *
     * @param code    状态码
     * @param message 消息
     * @param <T>     数据类型
     * @return Result
     */
    public static <T> Result<T> failure(int code, String message) {
        return new Result<>(code, message);
    }

    /**
     * 判断是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return this.code == ResultCode.SUCCESS.getCode();
    }
} 