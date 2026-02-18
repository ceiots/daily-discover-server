package com.dailydiscover.service;

import com.dailydiscover.model.*;
import java.util.List;
import java.util.Map;

public interface OrderService {
    
    // 基础订单操作
    /**
     * 创建订单
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
    
    // 订单核心表和扩展表操作
    OrdersCore getOrderCoreById(Long orderId);
    OrdersExtend getOrderExtendById(Long orderId);
    List<OrderItem> getOrderItems(Long orderId);
    void saveOrderCore(OrdersCore orderCore);
    void saveOrderExtend(OrdersExtend orderExtend);
    void saveOrderItem(OrderItem orderItem);
    
    // 物流跟踪功能
    OrderShipping getOrderShipping(Long orderId);
    List<OrderShippingTrack> getShippingTracks(Long shippingId);
    void updateShippingStatus(Long orderId, Integer shippingStatus);
    void addShippingTrack(Long shippingId, OrderShippingTrack track);
    
    // 支付记录管理
    PaymentTransaction getPaymentTransaction(Long orderId);
    List<PaymentTransaction> getPaymentTransactionsByOrder(Long orderId);
    void savePaymentTransaction(PaymentTransaction transaction);
    void updatePaymentStatus(Long transactionId, String status);
    
    // 售后申请管理
    AfterSalesApplication getAfterSalesApplication(Long applicationId);
    List<AfterSalesApplication> getAfterSalesByOrder(Long orderId);
    void saveAfterSalesApplication(AfterSalesApplication application);
    void updateAfterSalesStatus(Long applicationId, String status);
    
    // 发票管理功能
    OrderInvoice getOrderInvoice(Long orderId);
    void saveOrderInvoice(OrderInvoice invoice);
    void updateInvoiceStatus(Long invoiceId, String status);
    
    // 高级查询功能
    List<OrdersCore> getOrdersByStatus(Integer status);
    List<OrdersCore> getOrdersByPaymentStatus(String paymentStatus);
    List<OrdersCore> getOrdersByDateRange(String startDate, String endDate);
    
    // 统计和分析功能
    Map<String, Object> getSalesAnalysis(String startDate, String endDate);
    Map<String, Object> getCustomerOrderStats(Long userId);
    List<Map<String, Object>> getTopProductsBySales(int limit);
}