package com.dailydiscover.service;

import com.dailydiscover.model.PaymentMethod;
import com.dailydiscover.model.PaymentTransaction;
import com.dailydiscover.model.RefundRecord;
import java.util.List;
import java.util.Map;

public interface PaymentService {
    
    // 支付方式管理
    PaymentMethod getPaymentMethodById(Long methodId);
    PaymentMethod getPaymentMethodByCode(String methodCode);
    List<PaymentMethod> getAvailablePaymentMethods();
    List<PaymentMethod> getPaymentMethodsByType(String methodType);
    void savePaymentMethod(PaymentMethod method);
    void updatePaymentMethodStatus(Long methodId, Boolean isEnabled);
    
    // 支付记录管理
    PaymentTransaction getPaymentTransactionById(Long transactionId);
    PaymentTransaction getPaymentTransactionByOrder(Long orderId);
    List<PaymentTransaction> getPaymentTransactionsByUser(Long userId);
    void savePaymentTransaction(PaymentTransaction transaction);
    void updatePaymentTransactionStatus(Long transactionId, String status);
    
    // 退款记录管理
    RefundRecord getRefundRecordById(Long refundId);
    List<RefundRecord> getRefundRecordsByOrder(Long orderId);
    List<RefundRecord> getRefundRecordsByPayment(Long transactionId);
    void saveRefundRecord(RefundRecord record);
    void updateRefundStatus(Long refundId, String status);
    
    // 支付处理
    Map<String, Object> initiatePayment(Long orderId, Long paymentMethodId, Double amount);
    Map<String, Object> processPaymentCallback(String transactionNo, Map<String, Object> callbackData);
    Map<String, Object> queryPaymentStatus(String transactionNo);
    
    // 退款处理
    Map<String, Object> initiateRefund(Long paymentTransactionId, Double refundAmount, String refundReason);
    Map<String, Object> processRefundCallback(String refundNo, Map<String, Object> callbackData);
    
    // 支付统计和分析
    Map<String, Object> getPaymentStats(String startDate, String endDate);
    Map<String, Object> getRefundStats(String startDate, String endDate);
    List<Map<String, Object>> getTopPaymentMethods(int limit);
    
    // 支付安全验证
    Map<String, Object> validatePaymentRequest(Long orderId, Long paymentMethodId);
    Boolean verifyPaymentSignature(Map<String, Object> paymentData, String signature);
}