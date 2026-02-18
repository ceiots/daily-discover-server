package com.dailydiscover.mapper;

import com.dailydiscover.model.OrderShippingTrack;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 物流跟踪记录表 Mapper
 */
@Mapper
public interface OrderShippingTrackMapper extends BaseMapper<OrderShippingTrack> {
    
    /**
     * 根据订单ID查询物流跟踪记录
     */
    @Select("SELECT * FROM order_shipping_tracks WHERE order_id = #{orderId} ORDER BY track_time DESC")
    List<OrderShippingTrack> findByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据物流单号查询跟踪记录
     */
    @Select("SELECT * FROM order_shipping_tracks WHERE tracking_number = #{trackingNumber} ORDER BY track_time DESC")
    List<OrderShippingTrack> findByTrackingNumber(@Param("trackingNumber") String trackingNumber);
    
    /**
     * 查询最新的物流状态
     */
    @Select("SELECT * FROM order_shipping_tracks WHERE order_id = #{orderId} ORDER BY track_time DESC LIMIT 1")
    OrderShippingTrack findLatestTrackByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据物流状态查询
     */
    @Select("SELECT * FROM order_shipping_tracks WHERE track_status = #{status} ORDER BY track_time DESC")
    List<OrderShippingTrack> findByStatus(@Param("status") String status);
}