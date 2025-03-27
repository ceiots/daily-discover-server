package com.example.controller;

import com.example.dto.PaymentInfo;
import com.example.model.Order;
import com.example.model.OrderItem;
import com.example.service.OrderService;
import com.example.common.api.CommonResult;
import com.example.dto.AddressDto;
import com.example.dto.OrderCreateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

/**
 * 订单控制器类，处理订单相关的 HTTP 请求
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    /**
     * 获取用户的订单列表
     * @param status 订单状态
     * @param request HTTP 请求
     * @return 通用结果，包含订单列表
     */
    @GetMapping("/user")
    public CommonResult<List<Order>> getUserOrders(
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return CommonResult.success(orderService.getUserOrders(userId, status));
    }

    /**
     * 取消订单
     * @param orderId 订单 ID
     * @return 通用结果
     */
    @PostMapping("/{orderId}/cancel")
    public CommonResult<Void> cancelOrder(@PathVariable Long orderId) {
        // 参数校验
        if (orderId == null) {
            logger.error("取消订单时，订单ID为空");
            return CommonResult.failed("订单ID不能为空");
        }
        try {
            // 调用服务方法取消订单
            orderService.cancelOrder(orderId);
            logger.info("订单ID为 {} 的订单已成功取消", orderId);
            return CommonResult.success(null);
        } catch (Exception e) {
            // 异常处理
            logger.error("取消订单时发生异常，订单ID: {}", orderId, e);
            return CommonResult.failed("取消订单失败，请稍后重试");
        }
    }

    /**
     * 支付订单
     * @param orderId 订单 ID
     * @param paymentInfo 支付信息
     * @param request HTTP 请求
     * @return 通用结果
     */
    @PostMapping("/{orderId}/pay")
    public CommonResult<Void> payOrder(
            @PathVariable Long orderId,
            @RequestBody PaymentInfo paymentInfo,
            HttpServletRequest request) {
        // 参数校验
        if (orderId == null) {
            logger.error("支付订单时，订单ID为空");
            return CommonResult.failed("订单ID不能为空");
        }
        if (paymentInfo == null) {
            logger.error("支付订单时，支付信息为空");
            return CommonResult.failed("支付信息不能为空");
        }
        try {
            Long userId = (Long) request.getAttribute("userId");
            orderService.payOrder(orderId, userId, paymentInfo);
            logger.info("订单ID为 {} 的订单已成功支付", orderId);
            return CommonResult.success(null);
        } catch (Exception e) {
            // 异常处理
            logger.error("支付订单时发生异常，订单ID: {}", orderId, e);
            return CommonResult.failed("支付订单失败，请稍后重试");
        }
    }

    /**
     * 获取所有订单
     * @return 通用结果，包含所有订单列表
     */
    @GetMapping("/all")
    public CommonResult<List<Order>> getAllOrders() {
        try {
            return CommonResult.success(orderService.getAllOrders());
        } catch (Exception e) {
            logger.error("获取所有订单时发生异常", e);
            return CommonResult.failed("获取所有订单失败，请稍后重试");
        }
    }

    /**
     * 创建订单
     * @param orderCreateDto 订单创建 DTO
     * @return 通用结果，包含创建好的订单
     */
    @PostMapping("/create")
    public CommonResult<Order> createOrder(@RequestBody OrderCreateDto orderCreateDto) {
        logger.info("创建订单，参数：{}", orderCreateDto);
        
        if (orderCreateDto == null) {
            logger.error("创建订单时，订单信息为空");
            return CommonResult.failed("订单信息不能为空");
        }
    
        try {
            Order order = new Order();
            order.setUserId(orderCreateDto.getUserId());
            order.setOrderNumber(orderCreateDto.getOrderNo());
    
            List<OrderItem> orderItems = new ArrayList<>();
            for (OrderCreateDto.OrderItemDto item : orderCreateDto.getItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setProductId(item.getId());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPrice(item.getPrice()); 
                orderItem.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                orderItem.setSpecifications(item.getSpecifications());
                orderItems.add(orderItem);
            }
            order.setItems(orderItems);
    
            order.setPaymentAmount(orderCreateDto.getTotalAmount());
            order.setPaymentMethod(orderCreateDto.getPayType());
            order.setStatus(OrderService.ORDER_STATUS_PENDING_PAYMENT); // 使用常量设置订单状态
    
            AddressDto addressDto = orderCreateDto.getAddress();
            Order createdOrder = orderService.createOrder(order, addressDto);
            logger.info("订单创建成功，订单号：{}", createdOrder.getOrderNumber());
            return CommonResult.success(createdOrder);
            
        } catch (Exception e) {
            logger.error("创建订单时发生异常", e);
            return CommonResult.failed("创建订单失败：" + e.getMessage());
        }
    }

    /**
     * 获取指定订单详情
     * @param orderId 订单 ID
     * @return 通用结果，包含订单详情
     */
    @GetMapping("/{orderId}")
    public CommonResult<Order> getOrderById(@PathVariable Long orderId) {
        // 参数校验
        if (orderId == null) {
            logger.error("获取订单详情时，订单ID为空");
            return CommonResult.failed("订单ID不能为空");
        }
        try {
            // 调用额外的方法，这里假设记录一个信息日志
            logOrderRetrievalAttempt(orderId);
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return CommonResult.failed("未找到该订单");
            }
            return CommonResult.success(order);
        } catch (Exception e) {
            logger.error("获取订单详情时发生异常，订单ID: {}", orderId, e);
            return CommonResult.failed("获取订单详情失败，请稍后重试");
        }
    }

    /**
     * 记录订单检索尝试的日志
     * @param orderId 订单 ID
     */
    private void logOrderRetrievalAttempt(Long orderId) {
        logger.info("尝试获取订单详情，订单ID: {}", orderId);
    }

    @GetMapping("/orders/{orderNo}")
    public ResponseEntity<Order> getOrderByNo(@PathVariable String orderNo) {
        Order order = orderService.getOrderByNo(orderNo);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }
}