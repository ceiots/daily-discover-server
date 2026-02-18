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
}