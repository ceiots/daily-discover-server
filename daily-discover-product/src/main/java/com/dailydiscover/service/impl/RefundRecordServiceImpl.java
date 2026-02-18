package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.RefundRecordMapper;
import com.dailydiscover.model.RefundRecord;
import com.dailydiscover.service.RefundRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RefundRecordServiceImpl extends ServiceImpl<RefundRecordMapper, RefundRecord> implements RefundRecordService {
    
    @Autowired
    private RefundRecordMapper refundRecordMapper;
    
    @Override
    public RefundRecord getByRefundNo(String refundNo) {
        return lambdaQuery().eq(RefundRecord::getRefundNo, refundNo).one();
    }
    
    @Override
    public List<RefundRecord> getByOrderId(Long orderId) {
        return lambdaQuery().eq(RefundRecord::getOrderId, orderId).orderByDesc(RefundRecord::getCreatedAt).list();
    }
    
    @Override
    public List<RefundRecord> getByPaymentTransactionId(Long transactionId) {
        return lambdaQuery().eq(RefundRecord::getPaymentTransactionId, transactionId).orderByDesc(RefundRecord::getCreatedAt).list();
    }
    
    @Override
    public RefundRecord createRefundRecord(Long paymentTransactionId, BigDecimal refundAmount, String refundReason) {
        RefundRecord record = new RefundRecord();
        record.setPaymentTransactionId(paymentTransactionId);
        record.setRefundAmount(refundAmount);
        record.setRefundReason(refundReason);
        record.setRefundNo("REF" + System.currentTimeMillis());
        record.setStatus("pending");
        
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
            record.setRefundCompletedAt(new java.util.Date());
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
        // 这里可以添加业务逻辑，比如检查退款金额是否超过支付金额等
        return true;
    }
}