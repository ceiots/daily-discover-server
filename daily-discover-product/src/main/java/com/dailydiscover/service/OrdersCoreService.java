package com.dailydiscover.service;

import com.dailydiscover.model.OrdersCore;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 订单核心信息服务接口
 */
public interface OrdersCoreService extends IService<OrdersCore> {
    
    /**
     * 根据订单号查询订单
     */
    OrdersCore getByOrderNumber(String orderNumber);
    
    /**
     * 根据用户ID查询订单列表
     */
    java.util.List<OrdersCore> getByUserId(Long userId);
    
    /**
     * 根据订单状态查询订单
     */
    java.util.List<OrdersCore> getByStatus(String status);
    
    /**
     * 创建订单
     */
    OrdersCore createOrder(Long userId, java.math.BigDecimal totalAmount, String orderType);
    
    /**
     * 更新订单状态
     */
    boolean updateOrderStatus(Long orderId, String status);
    
    /**
     * 取消订单
     */
    boolean cancelOrder(Long orderId);
    
    /**
     * 完成订单
     */
    boolean completeOrder(Long orderId);
    
    /**
     * 获取用户订单统计
     */
    java.util.Map<String, Object> getUserOrderStats(Long userId);
    
    /**
     * 获取订单数量统计
     */
    java.util.Map<String, Object> getOrderCountStats();
}