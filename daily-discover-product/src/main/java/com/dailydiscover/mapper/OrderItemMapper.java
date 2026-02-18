package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
    
    /**
     * 根据订单ID查询订单项
     */
    @Select("SELECT * FROM order_items WHERE order_id = #{orderId} ORDER BY created_at ASC")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据商品ID查询订单项
     */
    @Select("SELECT * FROM order_items WHERE product_id = #{productId} ORDER BY created_at DESC")
    List<OrderItem> findByProductId(@Param("productId") Long productId);
    
    /**
     * 根据SKU ID查询订单项
     */
    @Select("SELECT * FROM order_items WHERE sku_id = #{skuId} ORDER BY created_at DESC")
    List<OrderItem> findBySkuId(@Param("skuId") Long skuId);
    
    /**
     * 统计订单中商品数量
     */
    @Select("SELECT SUM(quantity) FROM order_items WHERE order_id = #{orderId}")
    Integer countItemsByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 统计订单总金额
     */
    @Select("SELECT SUM(subtotal_amount) FROM order_items WHERE order_id = #{orderId}")
    Double sumOrderAmount(@Param("orderId") Long orderId);
}