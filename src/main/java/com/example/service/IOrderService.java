package com.example.service;

import java.util.List;

import com.example.common.api.CommonResult;
import com.example.dto.OrderCreateDTO;
import com.example.dto.OrderItemDTO;
import com.example.model.Order;

/**
 * 订单服务接口
 */
public interface IOrderService {
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
    CommonResult<Order> getOrderById(Long orderId);
    
    /**
     * 获取用户订单列表
     * @param userId 用户ID
     * @param status 订单状态，null表示全部
     * @return 订单列表
     */
    CommonResult<List<Order>> getOrdersByUserId(Long userId, Integer status);
}