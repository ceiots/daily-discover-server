package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.OrdersCoreMapper;
import com.dailydiscover.model.OrdersCore;
import com.dailydiscover.service.OrdersCoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrdersCoreServiceImpl extends ServiceImpl<OrdersCoreMapper, OrdersCore> implements OrdersCoreService {
    
    @Autowired
    private OrdersCoreMapper ordersCoreMapper;
    
    @Override
    public OrdersCore getByOrderNumber(String orderNumber) {
        return ordersCoreMapper.getByOrderNumber(orderNumber);
    }
    
    @Override
    public List<OrdersCore> getByUserId(Long userId) {
        return ordersCoreMapper.getByUserId(userId);
    }
    
    @Override
    public List<OrdersCore> getByStatus(String status) {
        return ordersCoreMapper.getByStatus(status);
    }
    
    @Override
    public OrdersCore createOrder(Long userId, BigDecimal totalAmount, String orderType) {
        return ordersCoreMapper.createOrder(userId, totalAmount, orderType);
    }
    
    @Override
    public boolean updateOrderStatus(Long orderId, String status) {
        return ordersCoreMapper.updateOrderStatus(orderId, status);
    }
    
    @Override
    public boolean cancelOrder(Long orderId) {
        return ordersCoreMapper.cancelOrder(orderId);
    }
    
    @Override
    public boolean completeOrder(Long orderId) {
        return ordersCoreMapper.completeOrder(orderId);
    }
    
    @Override
    public Map<String, Object> getUserOrderStats(Long userId) {
        return ordersCoreMapper.getUserOrderStats(userId);
    }
    
    @Override
    public Map<String, Object> getOrderCountStats() {
        return ordersCoreMapper.getOrderCountStats();
    }
}