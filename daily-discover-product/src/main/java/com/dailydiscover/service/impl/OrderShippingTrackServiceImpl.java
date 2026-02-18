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
    public List<OrderShippingTrack> getByOrderId(Long orderId) {
        return lambdaQuery().eq(OrderShippingTrack::getOrderId, orderId).orderByAsc(OrderShippingTrack::getTrackTime).list();
    }
    
    @Override
    public List<OrderShippingTrack> getByTrackingNumber(String trackingNumber) {
        return lambdaQuery().eq(OrderShippingTrack::getTrackingNumber, trackingNumber).orderByAsc(OrderShippingTrack::getTrackTime).list();
    }
    
    @Override
    public OrderShippingTrack addTrackingRecord(Long orderId, String trackingNumber, String location, 
                                               String status, String description) {
        OrderShippingTrack track = new OrderShippingTrack();
        track.setOrderId(orderId);
        track.setTrackingNumber(trackingNumber);
        track.setLocation(location);
        track.setStatus(status);
        track.setDescription(description);
        track.setTrackTime(new java.util.Date());
        
        save(track);
        return track;
    }
    
    @Override
    public OrderShippingTrack getLatestTrackingStatus(Long orderId) {
        return lambdaQuery()
                .eq(OrderShippingTrack::getOrderId, orderId)
                .orderByDesc(OrderShippingTrack::getTrackTime)
                .last("LIMIT 1")
                .one();
    }
    
    @Override
    public List<OrderShippingTrack> getTrackingHistory(String trackingNumber) {
        return lambdaQuery()
                .eq(OrderShippingTrack::getTrackingNumber, trackingNumber)
                .orderByAsc(OrderShippingTrack::getTrackTime)
                .list();
    }
    
    @Override
    public boolean batchUpdateTrackingStatus(List<Long> orderIds, String status) {
        return lambdaUpdate()
                .in(OrderShippingTrack::getOrderId, orderIds)
                .set(OrderShippingTrack::getStatus, status)
                .set(OrderShippingTrack::getTrackTime, new java.util.Date())
                .update();
    }
}