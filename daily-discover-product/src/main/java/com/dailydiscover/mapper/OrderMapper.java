package com.dailydiscover.mapper;

import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    
    @Insert("INSERT INTO orders (order_id, user_id, product_id, quantity, status, total_amount, created_at) " +
            "VALUES (#{orderId}, #{userId}, #{productId}, #{quantity}, #{status}, #{totalAmount}, CURRENT_TIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void createOrder(Map<String, Object> order);
    
    @Select("SELECT * FROM orders WHERE order_id = #{orderId}")
    Map<String, Object> getOrderById(Long orderId);
    
    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Map<String, Object>> getUserOrders(Long userId);
    
    @Update("UPDATE orders SET status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE order_id = #{orderId}")
    void updateOrderStatus(@Param("orderId") Long orderId, @Param("status") String status);
    
    @Update("UPDATE orders SET status = 'cancelled', updated_at = CURRENT_TIMESTAMP WHERE order_id = #{orderId}")
    void cancelOrder(Long orderId);
    
    @Select("SELECT COUNT(*) as total_orders, " +
            "SUM(CASE WHEN status = 'pending' THEN 1 ELSE 0 END) as pending_orders, " +
            "SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) as completed_orders, " +
            "SUM(CASE WHEN status = 'cancelled' THEN 1 ELSE 0 END) as cancelled_orders " +
            "FROM orders")
    Map<String, Object> getOrderStats();
}