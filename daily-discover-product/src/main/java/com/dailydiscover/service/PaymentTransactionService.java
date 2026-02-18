package com.dailydiscover.service;

import com.dailydiscover.model.PaymentTransaction;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 支付交易服务接口
 */
public interface PaymentTransactionService extends IService<PaymentTransaction> {
    
    /**
     * 根据订单ID查询支付交易
     */
    PaymentTransaction getByOrderId(Long orderId);
    
    /**
     * 根据交易号查询支付交易
     */
    PaymentTransaction getByTransactionNo(String transactionNo);
    
    /**
     * 根据用户ID查询支付交易
     */
    java.util.List<PaymentTransaction> getByUserId(Long userId);
    
    /**
     * 创建支付交易
     */
    PaymentTransaction createTransaction(Long orderId, Long paymentMethodId, java.math.BigDecimal amount, String currency);
    
    /**
     * 更新支付交易状态
     */
    boolean updateTransactionStatus(Long transactionId, String status);
    
    /**
     * 处理支付回调
     */
    boolean processPaymentCallback(String transactionNo, java.util.Map<String, Object> callbackData);
    
    /**
     * 查询支付状态
     */
    java.util.Map<String, Object> queryPaymentStatus(String transactionNo);
    
    /**
     * 获取支付统计信息
     */
    java.util.Map<String, Object> getPaymentStats(String startDate, String endDate);
    
    /**
     * 验证支付请求
     */
    boolean validatePaymentRequest(Long orderId, Long paymentMethodId);
}