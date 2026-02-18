package com.dailydiscover.service;

import com.dailydiscover.model.Coupon;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 优惠券服务接口
 */
public interface CouponService extends IService<Coupon> {
    
    /**
     * 根据优惠券代码查询优惠券
     */
    Coupon getByCouponCode(String couponCode);
    
    /**
     * 查询可用的优惠券
     */
    java.util.List<Coupon> getAvailableCoupons();
    
    /**
     * 根据优惠券类型查询优惠券
     */
    java.util.List<Coupon> getByCouponType(String couponType);
    
    /**
     * 创建优惠券
     */
    Coupon createCoupon(String couponCode, String couponType, java.math.BigDecimal discountAmount, 
                       java.math.BigDecimal minOrderAmount, java.time.LocalDateTime validFrom, 
                       java.time.LocalDateTime validTo);
    
    /**
     * 更新优惠券状态
     */
    boolean updateCouponStatus(Long couponId, String status);
    
    /**
     * 验证优惠券是否可用
     */
    boolean validateCoupon(String couponCode, java.math.BigDecimal orderAmount);
    
    /**
     * 获取优惠券统计信息
     */
    java.util.Map<String, Object> getCouponStats();
}