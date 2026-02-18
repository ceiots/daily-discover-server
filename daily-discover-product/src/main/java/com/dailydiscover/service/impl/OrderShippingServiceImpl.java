package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.OrderShippingMapper;
import com.dailydiscover.model.OrderShipping;
import com.dailydiscover.service.OrderShippingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderShippingServiceImpl extends ServiceImpl<OrderShippingMapper, OrderShipping> implements OrderShippingService {
    
    @Autowired
    private OrderShippingMapper orderShippingMapper;
    
    @Override
    public OrderShipping getByOrderId(Long orderId) {
        return orderShippingMapper.getByOrderId(orderId);
    }
    
    @Override
    public OrderShipping createShippingInfo(Long orderId, String shippingMethod, String trackingNumber, String recipientName, String recipientPhone, String recipientAddress) {
        int result = orderShippingMapper.createShippingInfo(orderId, shippingMethod, trackingNumber, recipientName, recipientPhone, recipientAddress);
        if (result > 0) {
            return orderShippingMapper.getByOrderId(orderId);
        }
        return null;
    }
    
    @Override
    public boolean updateShippingStatus(Long orderId, String status) {
        int result = orderShippingMapper.updateShippingStatus(orderId, status);
        return result > 0;
    }
    
    @Override
    public boolean updateTrackingInfo(Long orderId, String trackingNumber, String shippingCompany) {
        int result = orderShippingMapper.updateTrackingInfo(orderId, trackingNumber, shippingCompany);
        return result > 0;
    }
    
    @Override
    public List<OrderShipping> getPendingShipments() {
        return orderShippingMapper.getPendingShipments();
    }
    
    @Override
    public List<OrderShipping> getShippedOrders() {
        return orderShippingMapper.getShippedOrders();
    }
    
    @Override
    public Map<String, Object> getShippingStats() {
        return orderShippingMapper.getShippingStats();
    }
}