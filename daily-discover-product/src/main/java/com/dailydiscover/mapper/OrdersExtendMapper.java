package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.OrdersExtend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单扩展表 Mapper
 */
@Mapper
public interface OrdersExtendMapper extends BaseMapper<OrdersExtend> {
    
    /**
     * 根据地址ID查询订单扩展信息
     */
    @Select("SELECT * FROM orders_extend WHERE address_id = #{addressId}")
    OrdersExtend findByAddressId(@Param("addressId") Long addressId);
    
    /**
     * 根据优惠券ID查询订单扩展信息
     */
    @Select("SELECT * FROM orders_extend WHERE coupon_id = #{couponId}")
    List<OrdersExtend> findByCouponId(@Param("couponId") Long couponId);
    
    /**
     * 根据支付方式ID查询订单扩展信息
     */
    @Select("SELECT * FROM orders_extend WHERE payment_method_id = #{paymentMethodId}")
    List<OrdersExtend> findByPaymentMethodId(@Param("paymentMethodId") Long paymentMethodId);
    
    /**
     * 根据物流ID查询订单扩展信息
     */
    @Select("SELECT * FROM orders_extend WHERE shipping_id = #{shippingId}")
    OrdersExtend findByShippingId(@Param("shippingId") Long shippingId);
    
    /**
     * 根据发票ID查询订单扩展信息
     */
    @Select("SELECT * FROM orders_extend WHERE invoice_id = #{invoiceId}")
    OrdersExtend findByInvoiceId(@Param("invoiceId") Long invoiceId);
}