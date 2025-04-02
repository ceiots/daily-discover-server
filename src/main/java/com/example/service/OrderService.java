package com.example.service;

import com.example.dto.PaymentInfo;
import com.example.mapper.OrderMapper;
import com.example.model.Order;
import com.example.model.OrderAddr;
import com.example.model.OrderItem;
import com.example.config.ImageConfig;
import com.example.dto.AddressDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.mapper.OrderItemMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    // 订单状态常量
    public static final int ORDER_STATUS_PENDING_PAYMENT = 1; // 待付款
    public static final int ORDER_STATUS_PENDING_DELIVERY = 2; // 待发货
    public static final int ORDER_STATUS_PENDING_RECEIPT = 3; // 待收货
    public static final int ORDER_STATUS_COMPLETED = 4; // 已完成
    public static final int ORDER_STATUS_CANCELLED = 5; // 已取消

    @Autowired
    private OrderMapper orderMapper;
    

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderAddrService orderAddrService;

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
            System.out.println("订单创建开始1" + order);
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

    public Order getOrderByNo(String orderNo) {
        try {
            // 修改为使用 OrderMapper 进行查询
            return orderMapper.findByOrderNo(orderNo);
        } catch (Exception e) {
            // 打印异常信息，方便排查
            logger.error("获取订单详情失败，订单号: {}", orderNo, e);
            throw e;
        }
    }
    // 插入订单商品项的方法
    private void insertOrderItems(List<OrderItem> items) {
        if (items != null && !items.isEmpty()) {
            for (OrderItem item : items) {
                System.out.println("插入订单商品项：" + item);
                orderItemMapper.insertOrderItem(item);  // 使用 insertOrderItem 方法
            }
        }
    }

    /**
     * 获取用户的订单列表
     * @param userId 用户ID
     * @param status 订单状态，如果为"all"则查询所有状态
     * @return 订单列表
     */
    public Page<Order> getUserOrders(Long userId, String status, Pageable pageable) {
        if (status != null && !"all".equals(status)) {
            try {
                Integer statusCode = Integer.parseInt(status);
                System.out.println("getUserOrders statusCode:" + pageable);
                return orderMapper.getUserOrdersByStatus(userId, statusCode, pageable);
            } catch (NumberFormatException e) {
                logger.warn("无效的订单状态值: {}", status);
                return orderMapper.getUserOrders(userId, pageable);
            }
        } else {
            return orderMapper.getUserOrders(userId, pageable);
        }
    }

    /**
     * 取消订单
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 是否成功取消
     */
    @Transactional
    public boolean cancelOrder(Long orderId, Long userId) {
        Order order = getOrderById(orderId);
        if (order == null) {
            logger.warn("订单不存在，订单ID: {}", orderId);
            return false;
        }
        
        // 验证订单是否属于该用户
        if (!order.getUserId().equals(userId)) {
            logger.warn("订单不属于该用户，订单ID: {}, 用户ID: {}", orderId, userId);
            return false;
        }
        
        // 只有待付款状态的订单可以取消
        if (order.getStatus() != ORDER_STATUS_PENDING_PAYMENT) {
            logger.warn("订单状态不允许取消，订单ID: {}, 当前状态: {}", orderId, order.getStatus());
            return false;
        }
        
        // 更新订单状态为已取消
        order.setStatus(ORDER_STATUS_CANCELLED);
        orderMapper.updateOrderStatus(orderId, ORDER_STATUS_CANCELLED);
        return true;
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
        order.setStatus(ORDER_STATUS_PENDING_DELIVERY); // 修改为待发货状态
        // 设置支付时间为当前时间
        order.setPaymentTime(new Date()); 
        orderMapper.updateOrderStatus(orderId, ORDER_STATUS_PENDING_DELIVERY);
        // 更新支付时间
        orderMapper.updatePaymentTime(orderId, order.getPaymentTime()); 
    }

    /**
     * 获取所有订单
     * @return 所有订单列表
     */
    public List<Order> getAllOrders() {
        return orderMapper.getAllOrders();
    }

    // 删除这里的重复定义
    // public static final int ORDER_STATUS_PAID = 2;
    // public static final int ORDER_STATUS_CANCELED = 3;

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

    // 删除重复的常量定义

    /**
     * 根据用户ID获取订单列表，支持分页和状态筛选
     * @param userId 用户ID
     * @param status 订单状态，整数类型
     * @param pageable 分页参数
     * @return 分页后的订单列表
     */
    public Page<Order> getUserOrdersById(Long userId, Integer status, Pageable pageable) {
        System.out.println(status +" getUserOrdersById:" + pageable);
        
        List<Order> orders;
        int total;
        
        if (status != null && status != 0) { // 0表示全部
            orders = orderMapper.getUserOrdersByIdAndStatusWithPage(userId, status);
            total = orderMapper.countOrdersByUserIdAndStatus(userId, status);
        } else {
            orders = orderMapper.getUserOrdersByIdWithPage(userId);
            total = orderMapper.countOrdersByUserId(userId);
        }
        
        // 处理订单数据，添加前端需要的字段
        for (Order order : orders) {
            System.out.println("getUserOrdersById11:" + order);
            // 设置店铺名称 - 使用第一个商品的店铺名称
           /*  if (order.getItems() != null && !order.getItems().isEmpty()) {
                OrderItem firstItem = order.getItems().get(0);
                // 从商品表中获取店铺名称
                if (firstItem.getProductId() != null) {
                    // 这里可以从商品的shopName属性获取，或者设置一个默认值
                    order.setShopName("默认店铺"); // 如果没有关联店铺表，可以设置默认值
                }
            } */
            order.setImageUrl(ImageConfig.getFullImageUrl(order.getImageUrl()));
            
            // 格式化日期
            if (order.getCreatedAt() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                order.setDate(sdf.format(order.getCreatedAt()));
            }
            
            // 设置倒计时（对于待付款订单）- 添加空值检查
            if (order.getStatus() != null && order.getStatus() == ORDER_STATUS_PENDING_PAYMENT) {
                order.setCountdown("30分钟"); // 示例倒计时
            }
            
            // 确保 totalAmount 不为 null
            if (order.getPaymentAmount() == null) {
                order.setPaymentAmount(BigDecimal.ZERO);
            }
            
            // 处理订单项数据
            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    if (item.getPrice() == null) {
                        item.setPrice(BigDecimal.ZERO);
                    }
                    
                    // 设置商品属性
                    if (item.getSpecifications() != null) {
                        item.setAttributes("默认属性"); // 如果没有属性字段，可以设置默认值
                    }
                }
            }
        }
        
        // 手动分页
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), orders.size());
        
        // 防止索引越界
        if (start >= orders.size()) {
            return new org.springframework.data.domain.PageImpl<>(
                new ArrayList<>(), pageable, total);
        }
        
        List<Order> pageContent = orders.subList(start, end);
        
        // 创建 Page 对象
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, total);
    }
}