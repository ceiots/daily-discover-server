package com.dailydiscover.service;

import java.util.List;
import java.util.Map;

public interface OrderService {
    
    /**
     * 创建订单
     * @param productId 商品ID
     * @param quantity 数量
     * @return 订单创建结果
     */
    Map<String, Object> createOrder(String productId, int quantity);
    
    /**
     * 获取订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    Map<String, Object> getOrderById(String orderId);
    
    /**
     * 获取用户订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Map<String, Object>> getUserOrders(String userId);
    
    /**
     * 取消订单
     * @param orderId 订单ID
     * @return 取消结果
     */
    Map<String, Object> cancelOrder(String orderId);
    
    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param status 新状态
     * @return 更新结果
     */
    Map<String, Object> updateOrderStatus(String orderId, String status);
    
    /**
     * 获取订单统计信息
     * @return 统计信息
     */
    Map<String, Object> getOrderStats();
}