package com.dailydiscover.service;

import com.dailydiscover.model.PromotionActivity;
import com.dailydiscover.model.Coupon;
import com.dailydiscover.model.CouponUsageRecord;
import java.util.List;
import java.util.Map;

public interface PromotionService {
    
    // 促销活动管理
    PromotionActivity getPromotionActivityById(Long activityId);
    List<PromotionActivity> getActivePromotions();
    List<PromotionActivity> getPromotionsByType(String activityType);
    void savePromotionActivity(PromotionActivity activity);
    void updatePromotionStatus(Long activityId, String status);
    
    // 优惠券管理
    Coupon getCouponById(Long couponId);
    Coupon getCouponByCode(String couponCode);
    List<Coupon> getAvailableCoupons();
    List<Coupon> getCouponsByType(String couponType);
    void saveCoupon(Coupon coupon);
    void updateCouponStatus(Long couponId, String status);
    
    // 优惠券使用记录
    CouponUsageRecord getCouponUsageRecord(Long recordId);
    List<CouponUsageRecord> getCouponUsageByOrder(Long orderId);
    List<CouponUsageRecord> getCouponUsageByUser(Long userId);
    void saveCouponUsageRecord(CouponUsageRecord record);
    
    // 促销效果分析
    Map<String, Object> getPromotionStats(Long activityId);
    Map<String, Object> getCouponUsageStats(Long couponId);
    List<Map<String, Object>> getTopPromotions(int limit);
    
    // 优惠券验证和计算
    Map<String, Object> validateCoupon(String couponCode, Long userId, Double orderAmount);
    Double calculateDiscount(String couponCode, Double orderAmount);
    
    // 促销活动参与
    Map<String, Object> participatePromotion(Long activityId, Long userId);
    Map<String, Object> checkPromotionEligibility(Long activityId, Long userId);
}