package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.OrderItemMapper;
import com.dailydiscover.model.OrderItem;
import com.dailydiscover.service.OrderItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Override
    public List<OrderItem> getByOrderId(Long orderId) {
        return orderItemMapper.getByOrderId(orderId);
    }
    
    @Override
    public List<OrderItem> getByProductId(Long productId) {
        return orderItemMapper.getByProductId(productId);
    }
    
    @Override
    public List<OrderItem> getBySkuId(Long skuId) {
        return orderItemMapper.getBySkuId(skuId);
    }
    
    @Override
    public OrderItem addOrderItem(Long orderId, Long productId, Long skuId, Integer quantity, BigDecimal unitPrice, BigDecimal totalPrice) {
        return orderItemMapper.addOrderItem(orderId, productId, skuId, quantity, unitPrice, totalPrice);
    }
    
    @Override
    public boolean updateOrderItemQuantity(Long orderItemId, Integer quantity) {
        return orderItemMapper.updateOrderItemQuantity(orderItemId, quantity);
    }
    
    @Override
    public boolean deleteOrderItem(Long orderItemId) {
        return orderItemMapper.deleteOrderItem(orderItemId);
    }
    
    @Override
    public BigDecimal calculateOrderTotalAmount(Long orderId) {
        return orderItemMapper.calculateOrderTotalAmount(orderId);
    }
    
    @Override
    public Map<String, Object> getOrderItemStats(Long orderId) {
        return orderItemMapper.getOrderItemStats(orderId);
    }
}