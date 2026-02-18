package com.dailydiscover.service;

import com.dailydiscover.model.RefundRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 退款记录服务接口
 */
public interface RefundRecordService extends IService<RefundRecord> {
    
    /**
     * 根据退款单号查询退款记录
     */
    RefundRecord getByRefundNo(String refundNo);
    
    /**
     * 根据订单ID查询退款记录
     */
    java.util.List<RefundRecord> getByOrderId(Long orderId);
    
    /**
     * 根据支付交易ID查询退款记录
     */
    java.util.List<RefundRecord> getByPaymentTransactionId(Long transactionId);
    
    /**
     * 创建退款记录
     */
    RefundRecord createRefundRecord(Long paymentTransactionId, java.math.BigDecimal refundAmount, String refundReason);
    
    /**
     * 更新退款状态
     */
    boolean updateRefundStatus(Long refundId, String status);
    
    /**
     * 处理退款回调
     */
    boolean processRefundCallback(String refundNo, java.util.Map<String, Object> callbackData);
    
    /**
     * 获取退款统计信息
     */
    java.util.Map<String, Object> getRefundStats(String startDate, String endDate);
    
    /**
     * 验证退款请求
     */
    boolean validateRefundRequest(Long paymentTransactionId, java.math.BigDecimal refundAmount);
}