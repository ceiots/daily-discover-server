package com.dailydiscover.service;

import com.dailydiscover.model.OrdersExtend;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 订单扩展信息服务接口
 */
public interface OrdersExtendService extends IService<OrdersExtend> {
    
    /**
     * 根据订单ID查询扩展信息
     */
    OrdersExtend getByOrderId(Long orderId);
    
    /**
     * 更新订单扩展信息
     */
    boolean updateExtendInfo(Long orderId, String customerNotes, String internalNotes, String deliveryInstructions);
    
    /**
     * 更新订单优先级
     */
    boolean updateOrderPriority(Long orderId, String priority);
    
    /**
     * 更新订单标签
     */
    boolean updateOrderTags(Long orderId, String tags);
    
    /**
     * 记录订单操作日志
     */
    boolean recordOrderOperation(Long orderId, String operationType, String operator, String notes);
}