package com.example.service;

import com.example.dto.PaymentInfo;
import com.example.mapper.OrderMapper;
import com.example.model.Order;
import com.example.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private InventoryService inventoryService;

    @Transactional
    public void payOrder(Long orderId, Long userId, PaymentInfo paymentInfo) {
        Order order = orderMapper.findById(orderId);

        // 状态校验增强
        if (order.getStatus() != 0 || !order.getUserId().equals(userId)) {
            throw new IllegalStateException("非法操作：订单状态或用户不匹配");
        }

        // 添加库存扣减逻辑
        try {
            order.getItems().forEach(item ->
                    inventoryService.decreaseStock(item.getProductId(), item.getQuantity())
            );
        } catch (Exception e) {
            throw new IllegalStateException("库存扣减失败：" + e.getMessage());
        }

        // 设置支付信息
        order.setStatus(1);
        order.setPaymentMethod(paymentInfo.getPaymentMethod());

        // 进行空指针检查和支付金额验证
        if (paymentInfo != null && paymentInfo.getPaymentAmount() != null) {
            // 确保金额为非负数
            if (paymentInfo.getPaymentAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalStateException("支付金额不能为负数");
            }

            // 计算订单总金额并转换为 BigDecimal，保留两位小数
            BigDecimal orderTotal = BigDecimal.valueOf(order.calculateTotalAmount())
                    .setScale(2, RoundingMode.HALF_UP);

            // 将支付金额转换为两位小数进行比较
            BigDecimal paymentAmount = paymentInfo.getPaymentAmount()
                    .setScale(2, RoundingMode.HALF_UP);

            // 检查支付金额是否与订单总金额匹配
            if (!paymentAmount.equals(orderTotal)) {
                throw new IllegalStateException("支付金额与订单总金额不匹配");
            }

            // 设置支付金额
            order.setPaymentAmount(paymentAmount.doubleValue());
        } else {
            throw new IllegalStateException("支付信息或支付金额为空，无法完成支付操作");
        }

        order.setPaymentTime(new Date());
        orderMapper.updateOrderStatus(order);
    }

    public void cancelOrder(Long orderId) {
        orderMapper.cancelOrder(orderId);
    }

    public List<Order> getUserOrders(Long userId, Integer status) {
        return orderMapper.getUserOrders(userId, status);
    }

    public List<Order> getAllOrders() {
        return orderMapper.getAllOrders();
    }

    public Order createOrder(Order order) {
        order.setCreatedAt(new Date());
        orderMapper.createOrder(order);
        return order;
    }

    public Order getOrderById(Long orderId) {
        // 这里假设你有一个 OrderMapper 来操作数据库
        return orderMapper.getOrderById(orderId);
    }
}