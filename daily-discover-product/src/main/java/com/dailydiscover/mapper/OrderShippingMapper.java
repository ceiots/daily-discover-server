package com.dailydiscover.mapper;

import com.dailydiscover.model.OrderShipping;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 订单物流信息表 Mapper
 */
@Mapper
public interface OrderShippingMapper extends BaseMapper<OrderShipping> {
    
    /**
     * 根据订单ID查询物流信息
     */
    @Select("SELECT * FROM order_shipping WHERE order_id = #{orderId}")
    OrderShipping findByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据订单ID查询物流信息
     */
    @Select("SELECT * FROM order_shipping WHERE order_id = #{orderId}")
    OrderShipping getByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 创建物流信息
     */
    @Insert("INSERT INTO order_shipping (order_id, shipping_method, tracking_number, recipient_name, recipient_phone, recipient_address, status, created_at) " +
            "VALUES (#{orderId}, #{shippingMethod}, #{trackingNumber}, #{recipientName}, #{recipientPhone}, #{recipientAddress}, 'pending', NOW())")
    int createShippingInfo(@Param("orderId") Long orderId, @Param("shippingMethod") String shippingMethod, 
                          @Param("trackingNumber") String trackingNumber, @Param("recipientName") String recipientName,
                          @Param("recipientPhone") String recipientPhone, @Param("recipientAddress") String recipientAddress);
    
    /**
     * 更新物流状态
     */
    @Update("UPDATE order_shipping SET status = #{status}, updated_at = NOW() WHERE order_id = #{orderId}")
    int updateShippingStatus(@Param("orderId") Long orderId, @Param("status") String status);
    
    /**
     * 更新物流跟踪信息
     */
    @Update("UPDATE order_shipping SET tracking_number = #{trackingNumber}, shipping_company = #{shippingCompany}, updated_at = NOW() WHERE order_id = #{orderId}")
    int updateTrackingInfo(@Param("orderId") Long orderId, @Param("trackingNumber") String trackingNumber, 
                          @Param("shippingCompany") String shippingCompany);
    
    /**
     * 获取待发货订单列表
     */
    @Select("SELECT * FROM order_shipping WHERE status = 'pending' ORDER BY created_at ASC")
    List<OrderShipping> getPendingShipments();
    
    /**
     * 获取已发货订单列表
     */
    @Select("SELECT * FROM order_shipping WHERE status = 'shipped' ORDER BY shipped_at DESC")
    List<OrderShipping> getShippedOrders();
    
    /**
     * 获取物流统计信息
     */
    @Select("SELECT status, COUNT(*) as count FROM order_shipping GROUP BY status")
    Map<String, Object> getShippingStats();
}