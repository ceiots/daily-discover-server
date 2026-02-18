package com.dailydiscover.service;

import com.dailydiscover.model.OrderItem;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 订单项服务接口
 */
public interface OrderItemService extends IService<OrderItem> {
    
    /**
     * 根据订单ID查询订单项
     */
    java.util.List<OrderItem> getByOrderId(Long orderId);
    
    /**
     * 根据商品ID查询订单项
     */
    java.util.List<OrderItem> getByProductId(Long productId);
    
    /**
     * 根据SKU ID查询订单项
     */
    java.util.List<OrderItem> getBySkuId(Long skuId);
    
    /**
     * 添加订单项
     */
    OrderItem addOrderItem(Long orderId, Long productId, Long skuId, Integer quantity, 
                          java.math.BigDecimal unitPrice, java.math.BigDecimal totalPrice);
    
    /**
     * 更新订单项数量
     */
    boolean updateOrderItemQuantity(Long orderItemId, Integer quantity);
    
    /**
     * 删除订单项
     */
    boolean deleteOrderItem(Long orderItemId);
    
    /**
     * 计算订单总金额
     */
    java.math.BigDecimal calculateOrderTotalAmount(Long orderId);
    
    /**
     * 获取订单项统计信息
     */
    java.util.Map<String, Object> getOrderItemStats(Long orderId);
}