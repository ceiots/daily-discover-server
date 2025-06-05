package com.example.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

import com.example.model.Order;

@Mapper
public interface OrderDao {
    // ... existing code ...
    @Select("SELECT * FROM orders WHERE id = #{id}")
    Order findById(@Param("id") Long id);
    
    /**
     * 根据订单号查询订单
     */
    @Select("SELECT * FROM orders WHERE order_number = #{orderNumber}")
    Order findByOrderNumber(@Param("orderNumber") String orderNumber);
    
    /**
     * 根据用户ID查询订单
     */
    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Order> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID和订单状态查询订单
     */
    @Select("SELECT * FROM orders WHERE user_id = #{userId} AND status = #{status} ORDER BY created_at DESC")
    List<Order> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);
    
    /**
     * 插入订单
     */
    @Insert("INSERT INTO orders(user_id, order_number, payment_amount, status, created_at, payment_time) " +
            "VALUES(#{userId}, #{orderNumber}, #{paymentAmount}, #{status}, #{createdAt}, #{paymentTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);
    
    /**
     * 更新订单状态
     */
    @Update("UPDATE orders SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(Order order);

} 