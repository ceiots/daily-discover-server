package com.dailydiscover.service.impl;

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
public class OrderServiceImpl implements OrderService {
    
    private final OrderMapper orderMapper;
    private final OrdersCoreMapper ordersCoreMapper;
    private final OrdersExtendMapper ordersExtendMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderShippingMapper orderShippingMapper;
    private final OrderShippingTrackMapper orderShippingTrackMapper;
    private final PaymentTransactionMapper paymentTransactionMapper;
    private final AfterSalesApplicationMapper afterSalesApplicationMapper;
    private final OrderInvoiceMapper orderInvoiceMapper;
    
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
    
    // 订单核心表和扩展表操作实现
    @Override
    public OrdersCore getOrderCoreById(Long orderId) {
        return ordersCoreMapper.findById(orderId);
    }
    
    @Override
    public OrdersExtend getOrderExtendById(Long orderId) {
        return ordersExtendMapper.findByOrderId(orderId);
    }
    
    @Override
    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemMapper.findByOrderId(orderId);
    }
    
    @Override
    @Transactional
    public void saveOrderCore(OrdersCore orderCore) {
        if (orderCore.getId() == null) {
            ordersCoreMapper.insert(orderCore);
        } else {
            ordersCoreMapper.update(orderCore);
        }
    }
    
    @Override
    @Transactional
    public void saveOrderExtend(OrdersExtend orderExtend) {
        if (ordersExtendMapper.findByOrderId(orderExtend.getOrderId()) == null) {
            ordersExtendMapper.insert(orderExtend);
        } else {
            ordersExtendMapper.update(orderExtend);
        }
    }
    
    @Override
    @Transactional
    public void saveOrderItem(OrderItem orderItem) {
        if (orderItem.getId() == null) {
            orderItemMapper.insert(orderItem);
        } else {
            orderItemMapper.update(orderItem);
        }
    }
    
    // 物流跟踪功能实现
    @Override
    public OrderShipping getOrderShipping(Long orderId) {
        return orderShippingMapper.findByOrderId(orderId);
    }
    
    @Override
    public List<OrderShippingTrack> getShippingTracks(Long shippingId) {
        return orderShippingTrackMapper.findByShippingId(shippingId);
    }
    
    @Override
    @Transactional
    public void updateShippingStatus(Long orderId, Integer shippingStatus) {
        OrderShipping shipping = orderShippingMapper.findByOrderId(orderId);
        if (shipping != null) {
            shipping.setShippingStatus(shippingStatus);
            orderShippingMapper.update(shipping);
        }
    }
    
    @Override
    @Transactional
    public void addShippingTrack(Long shippingId, OrderShippingTrack track) {
        track.setShippingId(shippingId);
        track.setCreatedAt(new Date());
        orderShippingTrackMapper.insert(track);
    }
    
    // 支付记录管理实现
    @Override
    public PaymentTransaction getPaymentTransaction(Long orderId) {
        return paymentTransactionMapper.findByOrderId(orderId);
    }
    
    @Override
    public List<PaymentTransaction> getPaymentTransactionsByOrder(Long orderId) {
        return paymentTransactionMapper.findByOrderIdList(orderId);
    }
    
    @Override
    @Transactional
    public void savePaymentTransaction(PaymentTransaction transaction) {
        if (transaction.getId() == null) {
            paymentTransactionMapper.insert(transaction);
        } else {
            paymentTransactionMapper.update(transaction);
        }
    }
    
    @Override
    @Transactional
    public void updatePaymentStatus(Long transactionId, String status) {
        PaymentTransaction transaction = paymentTransactionMapper.findById(transactionId);
        if (transaction != null) {
            transaction.setStatus(status);
            paymentTransactionMapper.update(transaction);
        }
    }
    
    // 售后申请管理实现
    @Override
    public AfterSalesApplication getAfterSalesApplication(Long applicationId) {
        return afterSalesApplicationMapper.findById(applicationId);
    }
    
    @Override
    public List<AfterSalesApplication> getAfterSalesByOrder(Long orderId) {
        return afterSalesApplicationMapper.findByOrderId(orderId);
    }
    
    @Override
    @Transactional
    public void saveAfterSalesApplication(AfterSalesApplication application) {
        if (application.getId() == null) {
            afterSalesApplicationMapper.insert(application);
        } else {
            afterSalesApplicationMapper.update(application);
        }
    }
    
    @Override
    @Transactional
    public void updateAfterSalesStatus(Long applicationId, String status) {
        AfterSalesApplication application = afterSalesApplicationMapper.findById(applicationId);
        if (application != null) {
            application.setStatus(status);
            afterSalesApplicationMapper.update(application);
        }
    }
    
    // 发票管理功能实现
    @Override
    public OrderInvoice getOrderInvoice(Long orderId) {
        return orderInvoiceMapper.findByOrderId(orderId);
    }
    
    @Override
    @Transactional
    public void saveOrderInvoice(OrderInvoice invoice) {
        if (invoice.getId() == null) {
            orderInvoiceMapper.insert(invoice);
        } else {
            orderInvoiceMapper.update(invoice);
        }
    }
    
    @Override
    @Transactional
    public void updateInvoiceStatus(Long invoiceId, String status) {
        OrderInvoice invoice = orderInvoiceMapper.findById(invoiceId);
        if (invoice != null) {
            invoice.setInvoiceStatus(status);
            orderInvoiceMapper.update(invoice);
        }
    }
    
    // 高级查询功能实现
    @Override
    public List<OrdersCore> getOrdersByStatus(Integer status) {
        return ordersCoreMapper.findByStatus(status);
    }
    
    @Override
    public List<OrdersCore> getOrdersByPaymentStatus(String paymentStatus) {
        return ordersCoreMapper.findByPaymentStatus(paymentStatus);
    }
    
    @Override
    public List<OrdersCore> getOrdersByDateRange(String startDate, String endDate) {
        return ordersCoreMapper.findByDateRange(startDate, endDate);
    }
    
    // 统计和分析功能实现
    @Override
    public Map<String, Object> getSalesAnalysis(String startDate, String endDate) {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("totalOrders", ordersCoreMapper.countOrdersByDateRange(startDate, endDate));
        analysis.put("totalAmount", ordersCoreMapper.sumAmountByDateRange(startDate, endDate));
        analysis.put("avgOrderValue", ordersCoreMapper.avgOrderValueByDateRange(startDate, endDate));
        analysis.put("topProducts", orderItemMapper.findTopProductsByDateRange(startDate, endDate, 10));
        return analysis;
    }
    
    @Override
    public Map<String, Object> getCustomerOrderStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", ordersCoreMapper.countByUserId(userId));
        stats.put("totalSpent", ordersCoreMapper.sumAmountByUserId(userId));
        stats.put("avgOrderValue", ordersCoreMapper.avgOrderValueByUserId(userId));
        stats.put("lastOrderDate", ordersCoreMapper.findLastOrderDateByUserId(userId));
        return stats;
    }
    
    @Override
    public List<Map<String, Object>> getTopProductsBySales(int limit) {
        return orderItemMapper.findTopProductsBySales(limit);
    }
}