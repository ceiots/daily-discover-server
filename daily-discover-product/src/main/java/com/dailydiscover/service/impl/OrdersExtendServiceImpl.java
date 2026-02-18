package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.OrdersExtendMapper;
import com.dailydiscover.model.OrdersExtend;
import com.dailydiscover.service.OrdersExtendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrdersExtendServiceImpl extends ServiceImpl<OrdersExtendMapper, OrdersExtend> implements OrdersExtendService {
    
    @Autowired
    private OrdersExtendMapper ordersExtendMapper;
    
    @Override
    public OrdersExtend getByOrderId(Long orderId) {
        return ordersExtendMapper.getByOrderId(orderId);
    }
    
    @Override
    public boolean updateExtendInfo(Long orderId, String customerNotes, String internalNotes, String deliveryInstructions) {
        return ordersExtendMapper.updateExtendInfo(orderId, customerNotes, internalNotes, deliveryInstructions);
    }
    
    @Override
    public boolean updateOrderPriority(Long orderId, String priority) {
        return ordersExtendMapper.updateOrderPriority(orderId, priority);
    }
    
    @Override
    public boolean updateOrderTags(Long orderId, String tags) {
        return ordersExtendMapper.updateOrderTags(orderId, tags);
    }
    
    @Override
    public boolean recordOrderOperation(Long orderId, String operationType, String operator, String notes) {
        return ordersExtendMapper.recordOrderOperation(orderId, operationType, operator, notes);
    }
}