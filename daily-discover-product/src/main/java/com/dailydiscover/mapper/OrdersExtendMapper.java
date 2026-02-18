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
    
    /**
     * 根据订单ID查询订单扩展信息
     */
    @Select("SELECT * FROM orders_extend WHERE order_id = #{orderId}")
    OrdersExtend getByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 更新订单扩展信息
     */
    @Select("UPDATE orders_extend SET customer_notes = #{customerNotes}, internal_notes = #{internalNotes}, delivery_instructions = #{deliveryInstructions}, updated_at = NOW() WHERE order_id = #{orderId}")
    boolean updateExtendInfo(@Param("orderId") Long orderId, @Param("customerNotes") String customerNotes, @Param("internalNotes") String internalNotes, @Param("deliveryInstructions") String deliveryInstructions);
    
    /**
     * 更新订单优先级
     */
    @Select("UPDATE orders_extend SET priority = #{priority}, updated_at = NOW() WHERE order_id = #{orderId}")
    boolean updateOrderPriority(@Param("orderId") Long orderId, @Param("priority") String priority);
    
    /**
     * 更新订单标签
     */
    @Select("UPDATE orders_extend SET tags = #{tags}, updated_at = NOW() WHERE order_id = #{orderId}")
    boolean updateOrderTags(@Param("orderId") Long orderId, @Param("tags") String tags);
    
    /**
     * 记录订单操作记录
     */
    @Select("INSERT INTO order_operations (order_id, operation_type, operator, notes, created_at) VALUES (#{orderId}, #{operationType}, #{operator}, #{notes}, NOW())")
    boolean recordOrderOperation(@Param("orderId") Long orderId, @Param("operationType") String operationType, @Param("operator") String operator, @Param("notes") String notes);
}