package com.dailydiscover.service.impl;

import com.dailydiscover.mapper.OrderMapper;
import com.dailydiscover.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderMapper orderMapper;
    
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
}