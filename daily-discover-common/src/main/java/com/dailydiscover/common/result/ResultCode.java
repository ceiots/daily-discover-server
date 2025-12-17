package com.dailydiscover.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode implements IResultCode {
    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),
    
    /**
     * 业务异常
     */
    FAILURE(400, "业务异常"),
    
    /**
     * 未认证
     */
    UN_AUTHORIZED(401, "未认证或认证失败"),
    
    /**
     * 未授权
     */
    FORBIDDEN(403, "未授权"),
    
    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),
    
    /**
     * 服务器异常
     */
    ERROR(500, "服务器异常"),
    
    /**
     * 参数错误
     */
    PARAM_ERROR(1000, "参数错误"),
    
    /**
     * 用户不存在
     */
    USER_NOT_FOUND(1001, "用户不存在"),
    
    /**
     * 用户已存在
     */
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    
    /**
     * 密码错误
     */
    PASSWORD_ERROR(1003, "密码错误"),
    
    /**
     * 验证码错误
     */
    CAPTCHA_ERROR(1004, "验证码错误"),
    
    /**
     * 产品不存在
     */
    PRODUCT_NOT_FOUND(2001, "产品不存在"),
    
    /**
     * 库存不足
     */
    STOCK_NOT_ENOUGH(2002, "库存不足"),
    
    /**
     * 订单不存在
     */
    ORDER_NOT_FOUND(3001, "订单不存在"),
    
    /**
     * 支付失败
     */
    PAYMENT_FAILED(3002, "支付失败");

    /**
     * 状态码
     */
    private final int code;
    
    /**
     * 消息
     */
    private final String message;
}