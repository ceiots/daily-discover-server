package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.PaymentTransactionMapper;
import com.dailydiscover.model.PaymentTransaction;
import com.dailydiscover.service.PaymentTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PaymentTransactionServiceImpl extends ServiceImpl<PaymentTransactionMapper, PaymentTransaction> implements PaymentTransactionService {
    
    @Autowired
    private PaymentTransactionMapper paymentTransactionMapper;
    
    @Override
    public PaymentTransaction getByOrderId(Long orderId) {
        return paymentTransactionMapper.getByOrderId(orderId);
    }
    
    @Override
    public PaymentTransaction getByTransactionNo(String transactionNo) {
        return paymentTransactionMapper.getByTransactionNo(transactionNo);
    }
    
    @Override
    public List<PaymentTransaction> getByUserId(Long userId) {
        return paymentTransactionMapper.getByUserId(userId);
    }
    
    @Override
    public PaymentTransaction createTransaction(Long orderId, Long paymentMethodId, BigDecimal amount, String currency) {
        int result = paymentTransactionMapper.createTransaction(orderId, paymentMethodId, amount, currency);
        if (result > 0) {
            return paymentTransactionMapper.getByOrderId(orderId);
        }
        return null;
    }
    
    @Override
    public boolean updateTransactionStatus(Long transactionId, String status) {
        int result = paymentTransactionMapper.updateTransactionStatus(transactionId, status);
        return result > 0;
    }
    
    @Override
    public boolean processPaymentCallback(String transactionNo, Map<String, Object> callbackData) {
        int result = paymentTransactionMapper.processPaymentCallback(transactionNo, callbackData);
        return result > 0;
    }
    
    @Override
    public Map<String, Object> queryPaymentStatus(String transactionNo) {
        return paymentTransactionMapper.queryPaymentStatus(transactionNo);
    }
    
    @Override
    public Map<String, Object> getPaymentStats(String startDate, String endDate) {
        return paymentTransactionMapper.getPaymentStatsSingle(startDate, endDate);
    }
    
    @Override
    public boolean validatePaymentRequest(Long orderId, Long paymentMethodId) {
        return paymentTransactionMapper.validatePaymentRequest(orderId, paymentMethodId);
    }
}