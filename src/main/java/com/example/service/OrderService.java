package com.example.service;

import com.example.mapper.OrderMapper;
import com.example.model.Order;
import com.example.model.Address;
import com.example.model.OrderItem;
import com.example.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

import com.example.config.ImageConfig;
import com.example.dto.AddressDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.mapper.AddressMapper;
import com.example.mapper.OrderItemMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import com.example.common.api.CommonResult;
import com.example.dto.OrderCreateDTO;
import com.example.dto.OrderItemDTO;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {

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
    
    /**
     * 创建订单
     * @param orderDTO 订单创建信息
     * @return 订单信息
     */
    CommonResult<Order> createOrder(OrderCreateDTO orderDTO);
    
    /**
     * 支付订单
     * @param orderId 订单ID
     * @return 支付结果
     */
    CommonResult<Boolean> payOrder(Long orderId);
    
    /**
     * 取消订单
     * @param orderId 订单ID
     * @return 取消结果
     */
    CommonResult<Boolean> cancelOrder(Long orderId);
    
    /**
     * 申请退款
     * @param orderId 订单ID
     * @param reason 退款原因
     * @return 退款申请结果
     */
    CommonResult<Boolean> refundOrder(Long orderId, String reason);
    
    /**
     * 申请退货退款
     * @param orderId 订单ID
     * @param items 退货商品项
     * @param reason 退货原因
     * @return 退货申请结果
     */
    CommonResult<Boolean> returnOrder(Long orderId, List<OrderItemDTO> items, String reason);
    
    /**
     * 确认收货
     * @param orderId 订单ID
     * @return 确认结果
     */
    CommonResult<Boolean> confirmReceiveOrder(Long orderId);
    
    /**
     * 获取订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    Order getOrderById(Long orderId);
    
    /**
     * 获取用户订单列表
     * @param userId 用户ID
     * @param status 订单状态，null表示全部
     * @return 订单列表
     */
    CommonResult<List<Order>> getOrdersByUserId(Long userId, Integer status);

    // ... existing code ...
    /**
     * 更新订单信息
     */
    void updateOrder(Order order);

    /**
     * 更新订单状态
     * @param orderNumber 订单号
     * @param status 订单状态
     * @return 是否成功
     */
    boolean updateOrderStatus(String orderNumber, int status);

    // ... existing code ...
    /**
     * 创建订单（重载，供controller用）
     */
    Order createOrder(Order order, AddressDto addressDto);

    /**
     * 根据订单号获取订单
     */
    Order getOrderByNumber(String orderNumber);

    /**
     * 根据用户ID和状态分页获取订单
     */
    org.springframework.data.domain.Page<Order> getUserOrdersById(Long userId, Integer status, org.springframework.data.domain.Pageable pageable);

}
