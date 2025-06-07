package com.example.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.example.model.OrderWithAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.common.api.CommonResult;
import com.example.dto.AddressDto;
import com.example.dto.OrderCreateDTO;
import com.example.model.Order;
import com.example.model.Address;
import com.example.model.OrderItem;
import com.example.service.AddressService;
import com.example.service.OrderService;


/**
 * 订单控制器类，处理订单相关的 HTTP 请求
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private AddressService userAddrService;
    /**


    /**
     * 创建订单
     * @param orderCreateDto 订单创建 DTO
     * @return 通用结果，包含创建好的订单
     */
    @PostMapping("/create")
    public CommonResult<Order> createOrder(@RequestBody OrderCreateDTO orderCreateDto) {
        logger.info("创建的订单，参数：{}", orderCreateDto);
        
        if (orderCreateDto == null) {
            logger.error("创建订单时，订单信息为空");
            return CommonResult.failed("订单信息不能为空");
        }
     
        try {
            Order order = new Order();
            order.setUserId(orderCreateDto.getUserId());
            order.setOrderNumber(orderCreateDto.getOrderNo());
            order.setCreatedAt(new Date());

            List<OrderItem> orderItems = new ArrayList<>();
            for (OrderCreateDTO.OrderItemDto item : orderCreateDto.getItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setProductId(item.getProductId());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setSkuId(item.getSkuId());
                orderItem.setShopId(item.getShopId());
                orderItem.setPrice(item.getPrice()); 
                orderItem.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                orderItems.add(orderItem);
            }
            order.setItems(orderItems);
            
            order.setPaymentAmount(orderCreateDto.getTotalAmount());
            order.setPaymentMethod(orderCreateDto.getPayType());
            order.setStatus(OrderService.ORDER_STATUS_PENDING_PAYMENT); // 使用常量设置为待付款状态
            
            // 处理地址信息
            AddressDto addressDto = orderCreateDto.getAddressDto();
            if (addressDto != null) {
                // 创建订单对象并传递地址DTO
                orderCreateDto.setOrder(order);
                return orderService.createOrder(orderCreateDto);
            } else {
                logger.error("创建订单时，地址信息为空");
                return CommonResult.failed("收货地址不能为空");
            }
        } catch (Exception e) {
            logger.error("创建订单时发生异常", e);
            return CommonResult.failed("创建订单失败：" + e.getMessage());
        }
    }

    // 修改接口路径，保持URI语义一致性
    /**
     * 根据订单号获取订单详情，包含收货信息
     * @param orderNumber 订单号
     * @return 包含订单详情和收货信息的响应实体
     */
    @GetMapping("/{orderNumber}")
    public ResponseEntity<OrderWithAddress> getOrderByNumber(@PathVariable String orderNumber) {
        try {
            Order order = orderService.getOrderByNumber(orderNumber);
            if (order == null) {
                return ResponseEntity.notFound().build();
            }
            Address userAddress = userAddrService.getById(order.getAddressId());
            System.out.println("获取到的收货信息：" + userAddress);

            OrderWithAddress orderWithAddress = new OrderWithAddress(order, userAddress);
            return ResponseEntity.ok(orderWithAddress);
        } catch (Exception e) {
            logger.error("获取订单详情时发生异常，订单号: {}", orderNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 根据用户ID获取订单列表，支持分页和状态筛选
     * @param userId 用户ID
     * @param status 订单状态，整数类型
     * @param page 页码，默认第0页
     * @param size 每页数量，默认10条
     * @return 通用结果，包含订单列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<CommonResult<Page<Order>>> getUserOrdersById(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "0") Integer status,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        try {
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // 创建不带排序的分页对象
            Pageable pageable = PageRequest.of(page, size);

            // 调用服务层方法获取订单列表
            Page<Order> orders = orderService.getUserOrdersById(userId, status, pageable);

            // 返回成功响应
            return ResponseEntity.ok(CommonResult.success(orders));
        } catch (Exception e) {
            logger.error("获取用户订单列表时发生异常，用户ID: {}, 状态: {}", userId, status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

   
}