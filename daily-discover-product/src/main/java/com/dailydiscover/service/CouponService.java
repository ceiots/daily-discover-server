package com.dailydiscover.service;

import com.dailydiscover.model.Coupon;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 优惠券服务接口
 */
public interface CouponService extends IService<Coupon> {
    
    /**
     * 查询有效的优惠券
     */
    List<Coupon> findActiveCoupons();
    
    /**
     * 根据优惠券类型查询
     */
    List<Coupon> findByCouponType(String couponType);
    
    /**
     * 查询可用的优惠券（未过期且未使用）
     */
    List<Coupon> findAvailableCoupons();
    
    /**
     * 根据优惠券代码查询
     */
    Coupon findByCouponCode(String couponCode);
}