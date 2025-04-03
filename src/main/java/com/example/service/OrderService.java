package com.example.service;

import com.example.mapper.OrderMapper;
import com.example.model.Order;
import com.example.model.OrderAddr;
import com.example.model.OrderItem;
import com.example.config.ImageConfig;
import com.example.dto.AddressDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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

    // 支付方式常量
    public static final int PAYMENT_METHOD_ALIPAY = 1; // 支付宝
    public static final int PAYMENT_METHOD_WECHAT = 2; // 微信支付
    public static final int PAYMENT_METHOD_CREDIT_CARD = 3; // 信用卡支付

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

    /**
     * 根据订单号获取订单
     * @param orderNumber 订单号
     * @return 订单对象
     */
    public Order getOrderByNumber(String orderNumber) {
        try {
            // 修改为使用 OrderMapper 进行查询
            Order order = orderMapper.findByOrderNumber(orderNumber);
            return processOrderData(order);
        } catch (Exception e) {
            // 打印异常信息，方便排查
            logger.error("获取订单详情失败，订单号: {}", orderNumber, e);
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
    
        List<Order> orders;
        int total;
    
        if (status != null && status != 0) { // 0表示全部
            orders = orderMapper.getUserOrdersByIdAndStatusWithPage(userId, status);
            total = orderMapper.countOrdersByUserIdAndStatus(userId, status);
        } else {
            orders = orderMapper.getUserOrdersByIdWithPage(userId);
            total = orderMapper.countOrdersByUserId(userId);
        }
    
        // 处理订单数据
        if (orders != null) {
            for (int i = 0; i < orders.size(); i++) {
                Order originalOrder = orders.get(i);
                // 调用 processOrderData 方法处理订单数据
                Order processedOrder = processOrderData(originalOrder);
                orders.set(i, processedOrder);
            }
        }
       
        System.out.println("getUserOrdersById orders:" + orders);
    
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

    /**
     * 处理订单数据，添加前端需要的字段
     * @param order 订单对象
     * @return 处理后的订单对象，如果传入的订单对象为 null，则返回 null
     */
    public Order processOrderData(Order order) {
        if (order == null) {
            return null;
        }
        // 打印原始订单数据
        System.out.println("原始订单: " + order);

        // 格式化日期
        if (order.getCreatedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = sdf.format(order.getCreatedAt());
            order.setDate(formattedDate);
            System.out.println("设置日期: " + formattedDate);
        }
        
        // 设置状态文本
        if (order.getStatus() != null) {
            int status = order.getStatus();

            // 设置状态文本
            String statusText;
            switch (status) {
                case ORDER_STATUS_PENDING_PAYMENT:
                    statusText = "待付款";
                    // 设置倒计时（对于待付款订单）
                    order.setCountdown("30分钟");
                    System.out.println("设置倒计时: 30分钟");
                    break;
                case ORDER_STATUS_PENDING_DELIVERY:
                    statusText = "待发货";
                    break;
                case ORDER_STATUS_PENDING_RECEIPT:
                    statusText = "待收货";
                    break;
                case ORDER_STATUS_COMPLETED:
                    statusText = "已完成";
                    break;
                case ORDER_STATUS_CANCELLED:
                    statusText = "已取消";
                    break;
                default:
                    statusText = "未知状态";
            }
            order.setStatusText(statusText);
            System.out.println("设置状态文本: " + statusText);
        }

        // 设置支付金额
        if (order.getPaymentAmount() == null) {
            order.setPaymentAmount(BigDecimal.ZERO);
        }
        System.out.println("设置支付金额: " + order.getPaymentAmount());

        // 处理订单项并计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> processedItems = new ArrayList<>();

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            System.out.println("处理订单项，数量: " + order.getItems().size());

            for (OrderItem item : order.getItems()) {
                OrderItem processedItem = new OrderItem();
                BeanUtils.copyProperties(item, processedItem);
                
                // 设置图片URL
                if (processedItem.getImageUrl() != null) {
                    String fullImageUrl = ImageConfig.getFullImageUrl(processedItem.getImageUrl());
                    processedItem.setImageUrl(fullImageUrl);
                }

                // 设置店铺URL
                if (processedItem.getShopAvatarUrl() != null) {
                    processedItem.setShopAvatarUrl(ImageConfig.getFullImageUrl(processedItem.getShopAvatarUrl()));
                }

                // 设置价格
                if (processedItem.getPrice() == null) {
                    processedItem.setPrice(BigDecimal.ZERO);
                }

                // 设置小计
                if (processedItem.getSubtotal() != null) {
                    totalAmount = totalAmount.add(processedItem.getSubtotal());
                }

                // 设置商品属性
                processedItem.setAttributes("默认属性");

                processedItems.add(processedItem);
                System.out.println("处理订单项: " + processedItem.getName() + ", 价格: " + processedItem.getPrice());
            }
        }

        // 设置总金额
        order.setTotalAmount(totalAmount);

        // 设置处理后的订单项
        order.setItems(processedItems);

        // 处理支付方式
        if (order.getPaymentMethod() != null) {
            // 从订单对象中获取支付方式代码，并通过 getPaymentMethodText 方法转换为对应的文字描述
            String paymentMethodDescription = getPaymentMethodText(order.getPaymentMethod());
            // 将支付方式的文字描述设置到订单对象中
            order.setPaymentMethodText(paymentMethodDescription);
            System.out.println("设置支付方式文本: " + paymentMethodDescription);
        }

        return order;
    }

    /**
     * 根据支付方式代码获取支付方式文字描述
     * @param paymentMethod 支付方式代码
     * @return 支付方式文字描述
     */
    private String getPaymentMethodText(int paymentMethod) {
        switch (paymentMethod) {
            case PAYMENT_METHOD_ALIPAY:
                return "支付宝";
            case PAYMENT_METHOD_WECHAT:
                return "微信支付";
            case PAYMENT_METHOD_CREDIT_CARD:
                return "信用卡支付";
            default:
                return "未知支付方式";
        }
    }

    // ... 其他方法 ...
}