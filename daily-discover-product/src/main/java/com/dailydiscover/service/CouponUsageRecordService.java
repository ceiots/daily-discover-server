package com.dailydiscover.service;

import com.dailydiscover.model.CouponUsageRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 优惠券使用记录服务接口
 */
public interface CouponUsageRecordService extends IService<CouponUsageRecord> {
    
    /**
     * 根据订单ID查询优惠券使用记录
     */
    CouponUsageRecord getByOrderId(Long orderId);
    
    /**
     * 根据用户ID查询优惠券使用记录
     */
    java.util.List<CouponUsageRecord> getByUserId(Long userId);
    
    /**
     * 根据优惠券ID查询使用记录
     */
    java.util.List<CouponUsageRecord> getByCouponId(Long couponId);
    
    /**
     * 记录优惠券使用
     */
    CouponUsageRecord recordCouponUsage(Long orderId, Long userId, Long couponId, 
                                       java.math.BigDecimal discountAmount);
    
    /**
     * 获取用户优惠券使用统计
     */
    java.util.Map<String, Object> getUserCouponUsageStats(Long userId);
    
    /**
     * 获取优惠券使用统计
     */
    java.util.Map<String, Object> getCouponUsageStats();
}