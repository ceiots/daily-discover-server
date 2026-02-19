package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.OrderShippingTrackMapper;
import com.dailydiscover.model.OrderShippingTrack;
import com.dailydiscover.service.OrderShippingTrackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OrderShippingTrackServiceImpl extends ServiceImpl<OrderShippingTrackMapper, OrderShippingTrack> implements OrderShippingTrackService {
    
    @Autowired
    private OrderShippingTrackMapper orderShippingTrackMapper;
    
    @Override
    public List<OrderShippingTrack> findByOrderId(Long orderId) {
        return orderShippingTrackMapper.findByOrderId(orderId);
    }
    
    @Override
    public List<OrderShippingTrack> findByTrackingNumber(String trackingNumber) {
        return orderShippingTrackMapper.findByTrackingNumber(trackingNumber);
    }
    
    @Override
    public OrderShippingTrack addTrackingRecord(Long orderId, String trackingNumber, String location, 
                                               String status, String description) {
        return null;
    }
    
    @Override
    public OrderShippingTrack findLatestTrackByOrderId(Long orderId) {
        return orderShippingTrackMapper.findLatestTrackByOrderId(orderId);
    }
    
    @Override
    public List<OrderShippingTrack> findByStatus(String status) {
        return orderShippingTrackMapper.findByStatus(status);
    }
    
    @Override
    public List<OrderShippingTrack> getTrackingHistory(String trackingNumber) {
        return orderShippingTrackMapper.findByTrackingNumber(trackingNumber);
    }
    
    @Override
    public boolean batchUpdateTrackingStatus(List<Long> orderIds, String status) {
        return false;
    }
}