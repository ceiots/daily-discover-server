package com.example.service;

import com.example.dto.PaymentInfo;
import com.example.mapper.OrderMapper;
import com.example.model.Order;
import com.example.model.OrderAddr;
import com.example.dto.AddressDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.model.OrderItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderAddrService orderAddrService; // 新增注入

    /**
     * 创建订单
     * @param order 订单对象
     * @param addressDto 地址信息
     * @return 创建好的订单
     */
    @Transactional
    public Order createOrder(Order order, AddressDto addressDto) {
        try {
            // 处理地址信息
            if (addressDto != null) {
                handleAddressInfo(order, addressDto);
            }
    
            // 计算订单总金额
            BigDecimal totalAmount = order.getItems().stream()
                    .map(OrderItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
    
            // 验证订单总金额
            if (!totalAmount.equals(order.getPaymentAmount())) {
                throw new IllegalArgumentException("订单总金额不匹配");
            }
    
            // 使用常量设置订单状态
            order.setStatus(ORDER_STATUS_PENDING_PAYMENT); 
            order.setCreatedAt(new Date());
            System.out.println("订单创建开始" + order);
            // 插入订单数据
            orderMapper.insertOrder(order); // 调用 Mapper 方法
            System.out.println("订单创建成功，订单号：" + order.getOrderNumber());
    
            // 保存订单商品项
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                // 避免空指针异常
                List<OrderItem> orderItems = order.getItems();
                for (OrderItem item : orderItems) {
                    item.setOrderId(order.getId()); // 设置订单ID
                    insertOrderItems(Collections.singletonList(item)); // 插入单个商品项
                }
            }
    
            // 关联收货信息
            if (order.getOrderAddrId() != null) {
                OrderAddr orderAddr = orderAddrService.getByOrderAddrId(order.getOrderAddrId());
                if (orderAddr == null) {
                    throw new IllegalArgumentException("收货信息不存在");
                }
            }
    
            return order;
        } catch (Exception e) {
            logger.error("Error creating order: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 根据订单 ID 获取订单
     * @param orderId 订单 ID
     * @return 订单对象
     */
    public Order getOrderById(Long orderId) {
        // 这里假设你有一个 OrderMapper 来操作数据库
        return orderMapper.getOrderById(orderId);
    }

    // 插入订单商品项的方法
    public void insertOrderItems(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            Long orderId = item.getOrderId();
            Order order = getOrderById(orderId);
            if (order == null) {
                throw new IllegalArgumentException("关联的订单不存在，订单 ID: " + orderId);
            }
            // 执行插入 order_item 记录的逻辑
            orderMapper.insertOrderItem(item);
        }
    }

    /**
     * 获取用户的订单列表
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单列表
     */
    public List<Order> getUserOrders(Long userId, Integer status) {
        if (status != null) {
            return orderMapper.getUserOrdersByStatus(userId, status);
        } else {
            return orderMapper.getUserOrders(userId);
        }
    }

    /**
     * 取消订单
     * @param orderId 订单ID
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在，订单ID: " + orderId);
        }
        // 这里可以添加更多取消订单的逻辑，如更新库存等
        order.setStatus(ORDER_STATUS_CANCELED); 
        orderMapper.updateOrderStatus(orderId, ORDER_STATUS_CANCELED);
    }

    /**
     * 支付订单
     * @param orderId 订单ID
     * @param userId 用户ID
     * @param paymentInfo 支付信息
     */
    @Transactional
    public void payOrder(Long orderId, Long userId, PaymentInfo paymentInfo) {
        Order order = getOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在，订单ID: " + orderId);
        }
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("用户无权支付该订单，订单ID: " + orderId);
        }
        // 这里可以添加更多支付逻辑，如调用支付接口等
        order.setStatus(ORDER_STATUS_PAID); 
        orderMapper.updateOrderStatus(orderId, ORDER_STATUS_PAID);
    }

    /**
     * 获取所有订单
     * @return 所有订单列表
     */
    public List<Order> getAllOrders() {
        return orderMapper.getAllOrders();
    }

    // 新增订单状态常量
    public static final int ORDER_STATUS_PENDING_PAYMENT = 1;
    public static final int ORDER_STATUS_PAID = 2;
    public static final int ORDER_STATUS_CANCELED = 3;

    /**
     * 处理订单的地址信息
     * @param order 订单对象
     * @param addressDto 地址信息
     */
    private void handleAddressInfo(Order order, AddressDto addressDto) {
        OrderAddr orderAddr = new OrderAddr();
        // 确保字段名称统一
        orderAddr.setName(addressDto.getName());
        orderAddr.setPhone(addressDto.getPhone());
        orderAddr.setAddress(addressDto.getAddress());
        orderAddr.setIsDefault(false); // 默认不设为默认地址
        orderAddr.setUserId(order.getUserId()); // 设置用户ID
        orderAddrService.insertOrderAddr(orderAddr); // 调用 OrderAddrService 的插入方法
        System.out.println("订单地址信息处理完成"+orderAddr.getOrderAddrId());
        // 插入后获取插入地址的ID
        order.setOrderAddrId(orderAddr.getOrderAddrId()); // 设置订单的收货地址ID
    }

}