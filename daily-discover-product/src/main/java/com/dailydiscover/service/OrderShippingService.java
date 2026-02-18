package com.dailydiscover.service;

import com.dailydiscover.model.OrderShipping;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 订单物流服务接口
 */
public interface OrderShippingService extends IService<OrderShipping> {
    
    /**
     * 根据订单ID查询物流信息
     */
    OrderShipping getByOrderId(Long orderId);
    
    /**
     * 创建物流信息
     */
    OrderShipping createShippingInfo(Long orderId, String shippingMethod, String trackingNumber, 
                                    String recipientName, String recipientPhone, String recipientAddress);
    
    /**
     * 更新物流状态
     */
    boolean updateShippingStatus(Long orderId, String status);
    
    /**
     * 更新物流跟踪信息
     */
    boolean updateTrackingInfo(Long orderId, String trackingNumber, String shippingCompany);
    
    /**
     * 获取待发货订单列表
     */
    java.util.List<OrderShipping> getPendingShipments();
    
    /**
     * 获取已发货订单列表
     */
    java.util.List<OrderShipping> getShippedOrders();
    
    /**
     * 获取物流统计信息
     */
    java.util.Map<String, Object> getShippingStats();
}