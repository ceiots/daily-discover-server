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
import org.springframework.http.HttpStatus;
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
            order.setPaymentTime(new Date());
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


  // 修改接口路径，保持URI语义一致性
    @GetMapping("/{orderNo}")
    public ResponseEntity<Order> getOrderByNo(@PathVariable String orderNo) {
        try {
            System.out.println("Received request to get order by number: " + orderNo);
            Order order = orderService.getOrderByNo(orderNo);
            System.out.println("Order retrieved: " + order);
            if (order == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            // 打印详细的异常信息
            logger.error("获取订单详情时发生异常，订单号: {}", orderNo, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}