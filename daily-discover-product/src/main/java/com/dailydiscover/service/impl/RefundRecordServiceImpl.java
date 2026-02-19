package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.RefundRecordMapper;
import com.dailydiscover.model.PaymentTransaction;
import com.dailydiscover.model.RefundRecord;
import com.dailydiscover.service.RefundRecordService;
import com.dailydiscover.service.PaymentTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.math.BigDecimal;
import java.util.Map;

@Service
@Slf4j
public class RefundRecordServiceImpl extends ServiceImpl<RefundRecordMapper, RefundRecord> implements RefundRecordService {
    
    @Autowired
    private RefundRecordMapper refundRecordMapper;
    
    @Autowired
    private PaymentTransactionService paymentTransactionService;
    
    @Override
    public RefundRecord getByRefundNo(String refundNo) {
        return refundRecordMapper.findByRefundNo(refundNo);
    }
    
    @Override
    public List<RefundRecord> getByOrderId(Long orderId) {
        return refundRecordMapper.findByOrderId(orderId);
    }
    
    @Override
    public List<RefundRecord> getByPaymentTransactionId(Long transactionId) {
        return refundRecordMapper.findByPaymentTransactionId(transactionId);
    }
    
    @Override
    public RefundRecord createRefundRecord(Long paymentTransactionId, BigDecimal refundAmount, String refundReason) {
        RefundRecord record = new RefundRecord();
        record.setPaymentTransactionId(paymentTransactionId);
        record.setRefundAmount(refundAmount);
        record.setRefundReason(refundReason);
        record.setRefundNo("REF" + System.currentTimeMillis());
        record.setStatus("pending");
        record.setCreatedAt(java.time.LocalDateTime.now());
        
        save(record);
        return record;
    }
    
    @Override
    public boolean updateRefundStatus(Long refundId, String status) {
        RefundRecord record = getById(refundId);
        if (record != null) {
            record.setStatus(status);
            return updateById(record);
        }
        return false;
    }
    
    @Override
    public boolean processRefundCallback(String refundNo, Map<String, Object> callbackData) {
        RefundRecord record = getByRefundNo(refundNo);
        if (record != null) {
            record.setStatus("completed");
            record.setThirdPartyRefundNo((String) callbackData.get("thirdPartyRefundNo"));
            record.setRefundCompletedAt(java.time.LocalDateTime.now());
            return updateById(record);
        }
        return false;
    }
    
    @Override
    public Map<String, Object> getRefundStats(String startDate, String endDate) {
        List<RefundRecord> records = lambdaQuery()
                .between(RefundRecord::getCreatedAt, startDate, endDate)
                .list();
        
        BigDecimal totalRefundAmount = records.stream()
                .map(RefundRecord::getRefundAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long pendingCount = records.stream()
                .filter(record -> "pending".equals(record.getStatus()))
                .count();
        
        long completedCount = records.stream()
                .filter(record -> "completed".equals(record.getStatus()))
                .count();
        
        return Map.of(
            "totalRefunds", records.size(),
            "totalRefundAmount", totalRefundAmount,
            "pendingCount", pendingCount,
            "completedCount", completedCount
        );
    }
    
    @Override
    public boolean validateRefundRequest(Long paymentTransactionId, BigDecimal refundAmount) {
        // 验证退款请求的逻辑
        // 检查支付交易是否存在且状态为已完成
        PaymentTransaction paymentTransaction = paymentTransactionService.getById(paymentTransactionId);
        if (paymentTransaction == null) {
            log.warn("支付交易不存在: {}", paymentTransactionId);
            return false;
        }
        
        if (!"completed".equals(paymentTransaction.getStatus())) {
            log.warn("支付交易状态不正确: {}, 状态: {}", paymentTransactionId, paymentTransaction.getStatus());
            return false;
        }
        
        // 检查退款金额是否超过支付金额
        if (refundAmount.compareTo(paymentTransaction.getAmount()) > 0) {
            log.warn("退款金额超过支付金额: 退款金额={}, 支付金额={}", refundAmount, paymentTransaction.getAmount());
            return false;
        }
        
        // 检查退款金额是否大于0
        if (refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("退款金额必须大于0: {}", refundAmount);
            return false;
        }
        
        // 检查是否已有退款记录且总退款金额不超过支付金额
        List<RefundRecord> existingRefunds = getByPaymentTransactionId(paymentTransactionId);
        BigDecimal totalRefunded = existingRefunds.stream()
                .filter(record -> "completed".equals(record.getStatus()))
                .map(RefundRecord::getRefundAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal remainingAmount = paymentTransaction.getAmount().subtract(totalRefunded);
        if (refundAmount.compareTo(remainingAmount) > 0) {
            log.warn("退款金额超过剩余可退金额: 退款金额={}, 剩余金额={}", refundAmount, remainingAmount);
            return false;
        }
        
        return true;
    }
}