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
    
    /**
     * 根据订单号查询订单
     */
    @Select("SELECT * FROM orders_core WHERE order_number = #{orderNumber}")
    OrdersCore getByOrderNumber(@Param("orderNumber") String orderNumber);
    
    /**
     * 根据用户ID查询订单列表
     */
    @Select("SELECT * FROM orders_core WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<OrdersCore> getByUserId(@Param("userId") Long userId);
    
    /**
     * 根据订单状态查询订单列表
     */
    @Select("SELECT * FROM orders_core WHERE status = #{status} ORDER BY created_at DESC")
    List<OrdersCore> getByStatus(@Param("status") String status);
    
    /**
     * 创建订单
     */
    @Select("INSERT INTO orders_core (order_number, user_id, total_amount, order_type, status, created_at) VALUES (CONCAT('ORD', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), LPAD(FLOOR(RAND() * 10000), 4, '0')), #{userId}, #{totalAmount}, #{orderType}, 'PENDING', NOW())")
    OrdersCore createOrder(@Param("userId") Long userId, @Param("totalAmount") java.math.BigDecimal totalAmount, @Param("orderType") String orderType);
    
    /**
     * 更新订单状态
     */
    @Select("UPDATE orders_core SET status = #{status}, updated_at = NOW() WHERE id = #{orderId}")
    boolean updateOrderStatus(@Param("orderId") Long orderId, @Param("status") String status);
    
    /**
     * 取消订单
     */
    @Select("UPDATE orders_core SET status = 'CANCELLED', updated_at = NOW() WHERE id = #{orderId}")
    boolean cancelOrder(@Param("orderId") Long orderId);
    
    /**
     * 完成订单
     */
    @Select("UPDATE orders_core SET status = 'COMPLETED', updated_at = NOW() WHERE id = #{orderId}")
    boolean completeOrder(@Param("orderId") Long orderId);
    
    /**
     * 获取用户订单统计
     */
    @Select("SELECT COUNT(*) as total_orders, SUM(total_amount) as total_amount, AVG(total_amount) as avg_amount FROM orders_core WHERE user_id = #{userId}")
    java.util.Map<String, Object> getUserOrderStats(@Param("userId") Long userId);
    
    /**
     * 获取订单数量统计
     */
    @Select("SELECT COUNT(*) as total_orders, COUNT(CASE WHEN status = 'PENDING' THEN 1 END) as pending_orders, COUNT(CASE WHEN status = 'COMPLETED' THEN 1 END) as completed_orders, COUNT(CASE WHEN status = 'CANCELLED' THEN 1 END) as cancelled_orders FROM orders_core")
    java.util.Map<String, Object> getOrderCountStats();
}