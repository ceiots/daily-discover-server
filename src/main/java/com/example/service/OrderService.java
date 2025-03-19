package com.example.service;

import com.example.dto.PaymentInfo;
import com.example.mapper.AddressMapper;
import com.example.mapper.OrderMapper;
import com.example.model.Address;
import com.example.model.Order;
import com.example.dto.AddressDto;
import com.example.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单服务类，处理订单相关的业务逻辑
 */
@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private InventoryService inventoryService;

    /**
     * 支付订单
     * @param orderId 订单 ID
     * @param userId 用户 ID
     * @param paymentInfo 支付信息
     */
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
            order.setPaymentAmount(paymentAmount);
        } else {
            throw new IllegalStateException("支付信息或支付金额为空，无法完成支付操作");
        }

        order.setPaymentTime(new Date());
        orderMapper.updateOrderStatus(order);
    }

    /**
     * 取消订单
     * @param orderId 订单 ID
     */
    public void cancelOrder(Long orderId) {
        orderMapper.cancelOrder(orderId);
    }

    /**
     * 获取用户的订单列表
     * @param userId 用户 ID
     * @param status 订单状态
     * @return 订单列表
     */
    public List<Order> getUserOrders(Long userId, Integer status) {
        return orderMapper.getUserOrders(userId, status);
    }

    /**
     * 获取所有订单
     * @return 所有订单列表
     */
    public List<Order> getAllOrders() {
        return orderMapper.getAllOrders();
    }

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
                Address address = new Address();
                address.setName(addressDto.getName());
                address.setPhone(addressDto.getPhone());
                address.setAddress(addressDto.getAddress());
                address.setDefault(false); // 默认不设为默认地址
                address.setUserId(order.getUserId()); // 设置用户ID
                addressMapper.insertAddress(address);
            }

            // 计算订单总金额
            BigDecimal totalAmount = order.getItems().stream()
                    .map(item -> item.getSubtotal())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 验证订单总金额
            if (!totalAmount.equals(order.getPaymentAmount())) {
                throw new IllegalArgumentException("订单总金额不匹配");
            }

            order.setStatus(0); // 待支付状态
            order.setCreatedAt(new Date());
            orderMapper.insert(order);

            // 保存订单商品项
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                Map<Long, BigDecimal> productPrices = getProductPrices(order.getItems().stream().map(item -> item.getProductId()).collect(Collectors.toList()));
                List<Order.OrderItem> orderItems = new ArrayList<>();
                for (Order.OrderItem item : order.getItems()) {
                    Long itemId = item.getProductId();
                    item.setProductId(itemId);

                    // 从 DTO 中获取商品数量
                    Integer quantity = getQuantityFromDto(order, itemId);
                    item.setQuantity(quantity);

                    // 从批量获取的价格中获取当前商品的价格
                    BigDecimal price = productPrices.get(itemId);
                    if (price == null) {
                        throw new IllegalArgumentException("未找到商品 ID 为 " + itemId + " 的价格");
                    }
                    item.setPrice(price);
                    item.setSubtotal(price.multiply(BigDecimal.valueOf(item.getQuantity())));

                    orderItems.add(item);
                }
                order.setItems(orderItems);
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
     * 调用商品服务批量获取商品价格
     * @param itemIds 商品 ID 列表
     * @return 商品 ID 到价格的映射
     */
    private Map<Long, BigDecimal> getProductPrices(List<Long> itemIds) {
        // 这里需要根据实际情况实现具体的调用逻辑
        // 例如：
        // return productService.getProductPrices(itemIds);
        return new HashMap<>();
    }

    /**
     * 从订单中获取商品数量
     * @param order 订单对象
     * @param itemId 商品 ID
     * @return 商品数量
     */
    private Integer getQuantityFromDto(Order order, Long itemId) {
        // 这里需要根据实际情况从订单中获取商品数量
        // 例如：
        // return order.getItemQuantities().get(itemId);
        return 1;
    }
}