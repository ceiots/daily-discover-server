package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.CouponUsageRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 优惠券使用记录表 Mapper
 */
@Mapper
public interface CouponUsageRecordMapper extends BaseMapper<CouponUsageRecord> {
    
    /**
     * 根据用户ID查询优惠券使用记录
     */
    @Select("SELECT * FROM coupon_usage_records WHERE user_id = #{userId} ORDER BY used_at DESC")
    List<CouponUsageRecord> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据优惠券ID查询使用记录
     */
    @Select("SELECT * FROM coupon_usage_records WHERE coupon_id = #{couponId} ORDER BY used_at DESC")
    List<CouponUsageRecord> findByCouponId(@Param("couponId") Long couponId);
    
    /**
     * 根据订单ID查询优惠券使用记录
     */
    @Select("SELECT * FROM coupon_usage_records WHERE order_id = #{orderId}")
    CouponUsageRecord findByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 统计用户使用优惠券次数
     */
    @Select("SELECT COUNT(*) FROM coupon_usage_records WHERE user_id = #{userId}")
    Integer countByUserId(@Param("userId") Long userId);
    
    /**
     * 统计优惠券使用次数
     */
    @Select("SELECT COUNT(*) FROM coupon_usage_records WHERE coupon_id = #{couponId}")
    Integer countByCouponId(@Param("couponId") Long couponId);
    
    /**
     * 根据订单ID查询优惠券使用记录
     */
    @Select("SELECT * FROM coupon_usage_records WHERE order_id = #{orderId}")
    CouponUsageRecord getByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据用户ID查询优惠券使用记录
     */
    @Select("SELECT * FROM coupon_usage_records WHERE user_id = #{userId} ORDER BY used_at DESC")
    List<CouponUsageRecord> getByUserId(@Param("userId") Long userId);
    
    /**
     * 根据优惠券ID查询使用记录
     */
    @Select("SELECT * FROM coupon_usage_records WHERE coupon_id = #{couponId} ORDER BY used_at DESC")
    List<CouponUsageRecord> getByCouponId(@Param("couponId") Long couponId);
    
    /**
     * 记录优惠券使用
     */
    @Select("INSERT INTO coupon_usage_records (order_id, user_id, coupon_id, discount_amount, used_at) VALUES (#{orderId}, #{userId}, #{couponId}, #{discountAmount}, NOW())")
    CouponUsageRecord recordCouponUsage(@Param("orderId") Long orderId, @Param("userId") Long userId, @Param("couponId") Long couponId, @Param("discountAmount") java.math.BigDecimal discountAmount);
    
    /**
     * 获取用户优惠券使用统计
     */
    @Select("SELECT COUNT(*) as total_usage, SUM(discount_amount) as total_discount FROM coupon_usage_records WHERE user_id = #{userId}")
    java.util.Map<String, Object> getUserCouponUsageStats(@Param("userId") Long userId);
    
    /**
     * 获取优惠券使用统计
     */
    @Select("SELECT COUNT(*) as total_usage, SUM(discount_amount) as total_discount FROM coupon_usage_records")
    java.util.Map<String, Object> getCouponUsageStats();
}