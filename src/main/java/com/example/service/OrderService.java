package com.example.service;

import com.example.dto.OrderRequest;
import com.example.model.Order;
import com.example.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Transactional
    public Order createOrder(OrderRequest orderRequest) {
        try {
            Order order = new Order();
            order.setUserId(orderRequest.getUserId());
            order.setProductIds(orderRequest.getProductIds());
            order.setShippingAddress(orderRequest.getShippingAddress());
            order.setStatus("Pending");
            order.setCreatedAt(new Date());
            orderMapper.insert(order);
            return order;
        } catch (Exception e) {
            // 记录异常日志
            System.err.println("Error creating order: " + e.getMessage());
            throw e; // 重新抛出异常以便事务回滚
        }
    }

    public Order getOrderById(Long orderId) {
        try {
            return orderMapper.findById(orderId);
        } catch (Exception e) {
            // 记录异常日志
            System.err.println("Error fetching order: " + e.getMessage());
            return null;
        }
    }
} 