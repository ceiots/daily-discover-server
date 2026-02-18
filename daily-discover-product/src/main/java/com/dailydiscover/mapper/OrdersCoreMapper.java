package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.OrdersCore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单核心表 Mapper
 */
@Mapper
public interface OrdersCoreMapper extends BaseMapper<OrdersCore> {
    
    /**
     * 根据用户ID查询订单列表
     */
    @Select("SELECT * FROM orders_core WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<OrdersCore> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据订单状态查询订单列表
     */
    @Select("SELECT * FROM orders_core WHERE status = #{status} ORDER BY created_at DESC")
    List<OrdersCore> findByStatus(@Param("status") Integer status);
    
    /**
     * 根据支付状态查询订单列表
     */
    @Select("SELECT * FROM orders_core WHERE payment_status = #{paymentStatus} ORDER BY created_at DESC")
    List<OrdersCore> findByPaymentStatus(@Param("paymentStatus") String paymentStatus);
    
    /**
     * 查询最近订单
     */
    @Select("SELECT * FROM orders_core ORDER BY created_at DESC LIMIT #{limit}")
    List<OrdersCore> findRecentOrders(@Param("limit") int limit);
    
    /**
     * 统计用户订单总金额
     */
    @Select("SELECT SUM(total_amount) FROM orders_core WHERE user_id = #{userId}")
    Double sumTotalAmountByUserId(@Param("userId") Long userId);
}