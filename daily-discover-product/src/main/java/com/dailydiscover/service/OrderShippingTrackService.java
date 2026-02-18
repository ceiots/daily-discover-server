package com.dailydiscover.service;

import com.dailydiscover.model.OrderShippingTrack;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 订单物流跟踪服务接口
 */
public interface OrderShippingTrackService extends IService<OrderShippingTrack> {
    
    /**
     * 根据订单ID查询物流跟踪记录
     */
    java.util.List<OrderShippingTrack> findByOrderId(Long orderId);
    
    /**
     * 根据物流单号查询跟踪记录
     */
    java.util.List<OrderShippingTrack> findByTrackingNumber(String trackingNumber);
    
    /**
     * 添加物流跟踪记录
     */
    OrderShippingTrack addTrackingRecord(Long orderId, String trackingNumber, String location, 
                                        String status, String description);
    
    /**
     * 查询最新的物流状态
     */
    OrderShippingTrack findLatestTrackByOrderId(Long orderId);
    
    /**
     * 根据状态查询物流跟踪记录
     */
    java.util.List<OrderShippingTrack> findByStatus(String status);
    
    /**
     * 获取物流跟踪历史
     */
    java.util.List<OrderShippingTrack> getTrackingHistory(String trackingNumber);
    
    /**
     * 批量更新物流状态
     */
    boolean batchUpdateTrackingStatus(java.util.List<Long> orderIds, String status);
}