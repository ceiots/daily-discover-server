package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.*;
import com.dailydiscover.model.*;
import com.dailydiscover.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrdersCore> implements OrderService {
    
    private final OrderMapper orderMapper;
    private final OrdersCoreMapper ordersCoreMapper;
    private final OrdersExtendMapper ordersExtendMapper;
    private final OrderItemMapper orderItemMapper;
    private final PaymentTransactionMapper paymentTransactionMapper;
    
    @Override
    public Map<String, Object> createOrder(Long userId, Long productId, int quantity) {
        try {
            log.info("创建订单: userId={}, productId={}, quantity={}", userId, productId, quantity);
            
            Map<String, Object> order = new HashMap<>();
            order.put("userId", userId);
            order.put("productId", productId);
            order.put("quantity", quantity);
            order.put("status", "pending");
            order.put("createdAt", System.currentTimeMillis());
            
            orderMapper.createOrder(order);
            
            Map<String, Object> result = new HashMap<>();
            result.put("orderId", order.get("id"));
            result.put("productId", productId);
            result.put("quantity", quantity);
            result.put("status", "pending");
            result.put("createdAt", System.currentTimeMillis());
            result.put("success", true);
            result.put("message", "订单创建成功");
            
            return result;
        } catch (Exception e) {
            log.error("创建订单失败: productId={}, quantity={}", productId, quantity, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "订单创建失败");
            return errorResult;
        }
    }
    
    @Override
    public Map<String, Object> getOrderById(Long orderId) {
        try {
            log.info("获取订单详情: orderId={}", orderId);
            
            Map<String, Object> order = orderMapper.getOrderById(orderId);
            
            return order;
        } catch (Exception e) {
            log.error("获取订单详情失败: orderId={}", orderId, e);
            throw new RuntimeException("获取订单详情失败", e);
        }
    }
    
    @Override
    public List<Map<String, Object>> getUserOrders(Long userId) {
        try {
            log.info("获取用户订单列表: userId={}", userId);
            
            List<Map<String, Object>> orders = orderMapper.getUserOrders(userId);
            
            return orders;
        } catch (Exception e) {
            log.error("获取用户订单列表失败: userId={}", userId, e);
            throw new RuntimeException("获取用户订单列表失败", e);
        }
    }
    
    @Override
    public Map<String, Object> cancelOrder(Long orderId) {
        try {
            log.info("取消订单: orderId={}", orderId);
            
            orderMapper.updateOrderStatus(orderId, "cancelled");
            
            Map<String, Object> result = new HashMap<>();
            result.put("orderId", orderId);
            result.put("success", true);
            result.put("message", "订单取消成功");
            
            return result;
        } catch (Exception e) {
            log.error("取消订单失败: orderId={}", orderId, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "订单取消失败");
            return errorResult;
        }
    }
    
    @Override
    public Map<String, Object> updateOrderStatus(Long orderId, String status) {
        try {
            log.info("更新订单状态: orderId={}, status={}", orderId, status);
            
            orderMapper.updateOrderStatus(orderId, status);
            
            Map<String, Object> result = new HashMap<>();
            result.put("orderId", orderId);
            result.put("status", status);
            result.put("success", true);
            result.put("message", "订单状态更新成功");
            
            return result;
        } catch (Exception e) {
            log.error("更新订单状态失败: orderId={}, status={}", orderId, status, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "订单状态更新失败");
            return errorResult;
        }
    }
    
    @Override
    public Map<String, Object> getOrderStats() {
        try {
            log.info("获取订单统计信息");
            
            Map<String, Object> stats = orderMapper.getOrderStats();
            
            return stats;
        } catch (Exception e) {
            log.error("获取订单统计信息失败", e);
            throw new RuntimeException("获取订单统计信息失败", e);
        }
    }
    
    @Override
    public OrdersCore getOrderCoreById(Long orderId) {
        return ordersCoreMapper.selectById(orderId);
    }
    
    @Override
    @Transactional
    public void saveOrderCore(OrdersCore orderCore) {
        if (orderCore.getId() == null) {
            ordersCoreMapper.insert(orderCore);
        } else {
            ordersCoreMapper.updateById(orderCore);
        }
    }
    
    @Override
    public OrdersExtend getOrderExtendById(Long orderId) {
        return ordersExtendMapper.selectById(orderId);
    }
    
    @Override
    @Transactional
    public void saveOrderExtend(OrdersExtend orderExtend) {
        if (orderExtend.getOrderId() == null) {
            ordersExtendMapper.insert(orderExtend);
        } else {
            ordersExtendMapper.updateById(orderExtend);
        }
    }
    
    @Override
    public List<OrderItem> getOrderItems(Long orderId) {
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        return orderItemMapper.selectList(queryWrapper);
    }
    
    @Override
    @Transactional
    public void saveOrderItem(OrderItem orderItem) {
        if (orderItem.getId() == null) {
            orderItemMapper.insert(orderItem);
        } else {
            orderItemMapper.updateById(orderItem);
        }
    }
    
    @Override
    public PaymentTransaction getPaymentTransaction(Long orderId) {
        QueryWrapper<PaymentTransaction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId).last("LIMIT 1");
        return paymentTransactionMapper.selectOne(queryWrapper);
    }
    
    @Override
    public List<PaymentTransaction> getPaymentTransactionsByOrder(Long orderId) {
        QueryWrapper<PaymentTransaction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        return paymentTransactionMapper.selectList(queryWrapper);
    }
    
    @Override
    @Transactional
    public void savePaymentTransaction(PaymentTransaction transaction) {
        if (transaction.getId() == null) {
            paymentTransactionMapper.insert(transaction);
        } else {
            paymentTransactionMapper.updateById(transaction);
        }
    }
    
    @Override
    @Transactional
    public void updatePaymentStatus(Long transactionId, String status) {
        PaymentTransaction transaction = paymentTransactionMapper.selectById(transactionId);
        if (transaction != null) {
            transaction.setStatus(status);
            paymentTransactionMapper.updateById(transaction);
        }
    }
}