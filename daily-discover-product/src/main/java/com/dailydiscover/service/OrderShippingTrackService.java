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
    java.util.List<OrderShippingTrack> getByOrderId(Long orderId);
    
    /**
     * 根据物流单号查询跟踪记录
     */
    java.util.List<OrderShippingTrack> getByTrackingNumber(String trackingNumber);
    
    /**
     * 添加物流跟踪记录
     */
    OrderShippingTrack addTrackingRecord(Long orderId, String trackingNumber, String location, 
                                        String status, String description);
    
    /**
     * 获取最新物流状态
     */
    OrderShippingTrack getLatestTrackingStatus(Long orderId);
    
    /**
     * 获取物流跟踪历史
     */
    java.util.List<OrderShippingTrack> getTrackingHistory(String trackingNumber);
    
    /**
     * 批量更新物流状态
     */
    boolean batchUpdateTrackingStatus(java.util.List<Long> orderIds, String status);
}