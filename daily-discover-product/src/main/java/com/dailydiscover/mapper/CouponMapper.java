package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 优惠券表 Mapper
 */
@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {
    
    /**
     * 查询有效的优惠券
     */
    @Select("SELECT * FROM coupons WHERE status = 'active' AND start_time <= NOW() AND end_time >= NOW()")
    List<Coupon> findActiveCoupons();
    
    /**
     * 根据优惠券类型查询
     */
    @Select("SELECT * FROM coupons WHERE coupon_type = #{couponType} AND status = 'active'")
    List<Coupon> findByCouponType(@Param("couponType") String couponType);
    
    /**
     * 查询可用的优惠券（未过期且未使用）
     */
    @Select("SELECT * FROM coupons WHERE status = 'active' AND end_time >= NOW() AND usage_limit > used_count")
    List<Coupon> findAvailableCoupons();
    
    /**
     * 根据优惠券代码查询
     */
    @Select("SELECT * FROM coupons WHERE coupon_code = #{couponCode}")
    Coupon findByCouponCode(@Param("couponCode") String couponCode);
}