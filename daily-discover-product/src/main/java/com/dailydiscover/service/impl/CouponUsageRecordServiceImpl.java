package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.CouponUsageRecordMapper;
import com.dailydiscover.model.CouponUsageRecord;
import com.dailydiscover.service.CouponUsageRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CouponUsageRecordServiceImpl extends ServiceImpl<CouponUsageRecordMapper, CouponUsageRecord> implements CouponUsageRecordService {
    
    @Autowired
    private CouponUsageRecordMapper couponUsageRecordMapper;
    
    @Override
    public CouponUsageRecord getByOrderId(Long orderId) {
        return couponUsageRecordMapper.getByOrderId(orderId);
    }
    
    @Override
    public List<CouponUsageRecord> getByUserId(Long userId) {
        return couponUsageRecordMapper.getByUserId(userId);
    }
    
    @Override
    public List<CouponUsageRecord> getByCouponId(Long couponId) {
        return couponUsageRecordMapper.getByCouponId(couponId);
    }
    
    @Override
    public CouponUsageRecord recordCouponUsage(Long orderId, Long userId, Long couponId, BigDecimal discountAmount) {
        return couponUsageRecordMapper.recordCouponUsage(orderId, userId, couponId, discountAmount);
    }
    
    @Override
    public Map<String, Object> getUserCouponUsageStats(Long userId) {
        return couponUsageRecordMapper.getUserCouponUsageStats(userId);
    }
    
    @Override
    public Map<String, Object> getCouponUsageStats() {
        return couponUsageRecordMapper.getCouponUsageStats();
    }
}