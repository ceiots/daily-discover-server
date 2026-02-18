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
    
    /**
     * 根据订单ID查询订单项
     */
    @Select("SELECT * FROM order_items WHERE order_id = #{orderId} ORDER BY created_at ASC")
    List<OrderItem> getByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据商品ID查询订单项
     */
    @Select("SELECT * FROM order_items WHERE product_id = #{productId} ORDER BY created_at DESC")
    List<OrderItem> getByProductId(@Param("productId") Long productId);
    
    /**
     * 根据SKU ID查询订单项
     */
    @Select("SELECT * FROM order_items WHERE sku_id = #{skuId} ORDER BY created_at DESC")
    List<OrderItem> getBySkuId(@Param("skuId") Long skuId);
    
    /**
     * 添加订单项
     */
    @Select("INSERT INTO order_items (order_id, product_id, sku_id, quantity, unit_price, total_price, created_at) VALUES (#{orderId}, #{productId}, #{skuId}, #{quantity}, #{unitPrice}, #{totalPrice}, NOW())")
    OrderItem addOrderItem(@Param("orderId") Long orderId, @Param("productId") Long productId, @Param("skuId") Long skuId, @Param("quantity") Integer quantity, @Param("unitPrice") java.math.BigDecimal unitPrice, @Param("totalPrice") java.math.BigDecimal totalPrice);
    
    /**
     * 更新订单项数量
     */
    @Select("UPDATE order_items SET quantity = #{quantity}, total_price = quantity * unit_price WHERE id = #{orderItemId}")
    boolean updateOrderItemQuantity(@Param("orderItemId") Long orderItemId, @Param("quantity") Integer quantity);
    
    /**
     * 删除订单项
     */
    @Select("DELETE FROM order_items WHERE id = #{orderItemId}")
    boolean deleteOrderItem(@Param("orderItemId") Long orderItemId);
    
    /**
     * 计算订单总金额
     */
    @Select("SELECT SUM(total_price) FROM order_items WHERE order_id = #{orderId}")
    java.math.BigDecimal calculateOrderTotalAmount(@Param("orderId") Long orderId);
    
    /**
     * 获取订单项统计
     */
    @Select("SELECT COUNT(*) as item_count, SUM(quantity) as total_quantity, SUM(total_price) as total_amount FROM order_items WHERE order_id = #{orderId}")
    java.util.Map<String, Object> getOrderItemStats(@Param("orderId") Long orderId);
}