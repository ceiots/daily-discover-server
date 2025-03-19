package com.example.controller;

import com.example.dto.PaymentInfo;
import com.example.model.Order;
import com.example.service.OrderService;
import com.example.common.api.CommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @GetMapping("/user")
    public CommonResult<List<Order>> getUserOrders(
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return CommonResult.success(orderService.getUserOrders(userId, status));
    }

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

    // 新增支付接口
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

    // 假设添加一个获取所有订单的接口
    @GetMapping("/all")
    public CommonResult<List<Order>> getAllOrders() {
        try {
            return CommonResult.success(orderService.getAllOrders());
        } catch (Exception e) {
            logger.error("获取所有订单时发生异常", e);
            return CommonResult.failed("获取所有订单失败，请稍后重试");
        }
    }

    // 假设添加一个创建订单的接口
    @PostMapping("/create")
    public CommonResult<Order> createOrder(@RequestBody Order order) {
        // 参数校验
        if (order == null) {
            logger.error("创建订单时，订单信息为空");
            return CommonResult.failed("订单信息不能为空");
        }
        try {
            return CommonResult.success(orderService.createOrder(order));
        } catch (Exception e) {
            logger.error("创建订单时发生异常", e);
            return CommonResult.failed("创建订单失败，请稍后重试");
        }
    }

    // 新增获取指定订单详情的接口
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
     * @param orderId 订单ID
     */
    private void logOrderRetrievalAttempt(Long orderId) {
        logger.info("尝试获取订单详情，订单ID: {}", orderId);
    }
}