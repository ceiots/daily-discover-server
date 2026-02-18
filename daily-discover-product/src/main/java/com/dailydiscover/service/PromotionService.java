package com.dailydiscover.service;

import com.dailydiscover.model.PromotionActivity;
import com.dailydiscover.model.Coupon;
import com.dailydiscover.model.CouponUsageRecord;
import java.util.List;
import java.util.Map;

public interface PromotionService {
    
    // 促销活动管理
    /**
     * 获取促销活动列表
     * @return 促销活动列表
     */
    List<PromotionActivity> getPromotionActivities();
    
    /**
     * 根据ID获取促销活动
     * @param activityId 活动ID
     * @return 促销活动
     */
    PromotionActivity getPromotionActivityById(Long activityId);
    
    /**
     * 创建促销活动
     * @param activity 促销活动
     * @return 创建结果
     */
    Map<String, Object> createPromotionActivity(PromotionActivity activity);
    
    /**
     * 更新促销活动
     * @param activity 促销活动
     * @return 更新结果
     */
    Map<String, Object> updatePromotionActivity(PromotionActivity activity);
    
    // 优惠券管理
    /**
     * 获取优惠券列表
     * @return 优惠券列表
     */
    List<Coupon> getCoupons();
    
    /**
     * 根据ID获取优惠券
     * @param couponId 优惠券ID
     * @return 优惠券
     */
    Coupon getCouponById(Long couponId);
    
    /**
     * 创建优惠券
     * @param coupon 优惠券
     * @return 创建结果
     */
    Map<String, Object> createCoupon(Coupon coupon);
    
    /**
     * 更新优惠券
     * @param coupon 优惠券
     * @return 更新结果
     */
    Map<String, Object> updateCoupon(Coupon coupon);
    
    /**
     * 根据优惠券代码获取优惠券
     * @param couponCode 优惠券代码
     * @return 优惠券
     */
    Coupon getCouponByCode(String couponCode);
    
    /**
     * 验证优惠券是否可用
     * @param couponCode 优惠券代码
     * @param userId 用户ID
     * @param orderAmount 订单金额
     * @return 验证结果
     */
    Map<String, Object> validateCoupon(String couponCode, Long userId, Double orderAmount);
    
    // 优惠券使用记录
    /**
     * 获取用户优惠券使用记录
     * @param userId 用户ID
     * @return 使用记录列表
     */
    List<CouponUsageRecord> getUserCouponUsageRecords(Long userId);
    
    /**
     * 创建优惠券使用记录
     * @param record 使用记录
     * @return 创建结果
     */
    Map<String, Object> createCouponUsageRecord(CouponUsageRecord record);
    
    /**
     * 获取订单的优惠券使用记录
     * @param orderId 订单ID
     * @return 使用记录
     */
    CouponUsageRecord getCouponUsageRecordByOrder(Long orderId);
}