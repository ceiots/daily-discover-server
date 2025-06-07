package com.example.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.example.model.OrderItem;

@Mapper
public interface OrderItemDao {
    
    /**
     * 插入订单项
     */
    @Insert("INSERT INTO order_item(order_id, product_id, quantity, price, subtotal, product_name, image_url) " +
            "VALUES(#{orderId}, #{productId}, #{quantity}, #{price}, #{subtotal}, #{productName}, #{imageUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderItem orderItem);
    
    /**
     * 根据订单ID查询订单项
     */
    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据ID查询订单项
     */
    @Select("SELECT * FROM order_item WHERE id = #{id}")
    OrderItem findById(@Param("id") Long id);
    
    /**
     * 根据订单ID和商品ID查询订单项
     */
    @Select("SELECT * FROM order_item WHERE order_id = #{orderId} AND product_id = #{productId}")
    OrderItem findByOrderIdAndProductId(@Param("orderId") Long orderId, @Param("productId") Long productId);
} 