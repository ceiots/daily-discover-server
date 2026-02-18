package com.dailydiscover.service;

import com.dailydiscover.model.OrdersCore;
import com.dailydiscover.model.OrdersExtend;
import com.dailydiscover.model.OrderItem;
import com.dailydiscover.model.PaymentTransaction;
import java.util.List;
import java.util.Map;

public interface OrderService {
    
    // 基础订单操作
    /**
     * 创建订单
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 数量
     * @return 订单创建结果
     */
    Map<String, Object> createOrder(Long userId, Long productId, int quantity);
    
    /**
     * 获取订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    Map<String, Object> getOrderById(Long orderId);
    
    /**
     * 获取用户订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Map<String, Object>> getUserOrders(Long userId);
    
    /**
     * 取消订单
     * @param orderId 订单ID
     * @return 取消结果
     */
    Map<String, Object> cancelOrder(Long orderId);
    
    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param status 新状态
     * @return 更新结果
     */
    Map<String, Object> updateOrderStatus(Long orderId, String status);
    
    /**
     * 获取订单统计信息
     * @return 统计信息
     */
    Map<String, Object> getOrderStats();
    
    // 订单核心表操作
    OrdersCore getOrderCoreById(Long orderId);
    void saveOrderCore(OrdersCore orderCore);
    
    // 订单扩展表操作
    OrdersExtend getOrderExtendById(Long orderId);
    void saveOrderExtend(OrdersExtend orderExtend);
    
    // 订单项操作
    List<OrderItem> getOrderItems(Long orderId);
    void saveOrderItem(OrderItem orderItem);
    
    // 支付记录操作
    PaymentTransaction getPaymentTransaction(Long orderId);
    List<PaymentTransaction> getPaymentTransactionsByOrder(Long orderId);
    void savePaymentTransaction(PaymentTransaction transaction);
    void updatePaymentStatus(Long transactionId, String status);
}