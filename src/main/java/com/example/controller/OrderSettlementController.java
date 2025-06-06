package com.example.controller;

import com.example.common.api.CommonResult;
import com.example.model.Order;
import com.example.service.OrderSettlementService;
import com.example.util.UserIdExtractor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 订单结算控制器
 */
@RestController
@RequestMapping("/order-settlement")
public class OrderSettlementController {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderSettlementController.class);
    
    @Autowired
    private OrderSettlementService orderSettlementService;
    
    @Autowired
    private UserIdExtractor userIdExtractor;
    
    /**
     * 获取店铺待结算订单
     */
    @GetMapping("/pending")
    public CommonResult<List<Order>> getPendingSettlementOrders(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestParam("shopId") Long shopId,
            @RequestParam(value = "startDate", required = false) 
                @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(value = "endDate", required = false) 
                @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        
        // 验证用户权限
        Long userId = userIdExtractor.extractUserId(token, userIdHeader);
        if (userId == null) {
            return CommonResult.unauthorized(null);
        }
        
        // 获取待结算订单
        List<Order> orders = orderSettlementService.getPendingSettlementOrders(shopId, startDate, endDate);
        return CommonResult.success(orders);
    }
    
    /**
     * 获取店铺已结算订单
     */
    @GetMapping("/settled")
    public CommonResult<List<Order>> getSettledOrders(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestParam("shopId") Long shopId,
            @RequestParam(value = "startDate", required = false) 
                @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(value = "endDate", required = false) 
                @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        
        // 验证用户权限
        Long userId = userIdExtractor.extractUserId(token, userIdHeader);
        if (userId == null) {
            return CommonResult.unauthorized(null);
        }
        
        // 获取已结算订单
        List<Order> orders = orderSettlementService.getSettledOrders(shopId, startDate, endDate);
        return CommonResult.success(orders);
    }
    
    /**
     * 获取店铺结算汇总
     */
    @GetMapping("/summary")
    public CommonResult<OrderSettlementService.SettlementSummary> getSettlementSummary(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestParam("shopId") Long shopId,
            @RequestParam(value = "startDate", required = false) 
                @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(value = "endDate", required = false) 
                @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        
        // 验证用户权限
        Long userId = userIdExtractor.extractUserId(token, userIdHeader);
        if (userId == null) {
            return CommonResult.unauthorized(null);
        }
        
        // 获取结算汇总
        OrderSettlementService.SettlementSummary summary = 
                orderSettlementService.getSettlementSummary(shopId, startDate, endDate);
        return CommonResult.success(summary);
    }
    
    /**
     * 结算订单（管理员操作）
     */
    @PostMapping("/settle/{orderId}")
    public CommonResult<Boolean> settleOrder(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @PathVariable("orderId") Long orderId) {
        
        // 验证用户权限（实际项目中需要验证是否为管理员）
        Long userId = userIdExtractor.extractUserId(token, userIdHeader);
        if (userId == null) {
            return CommonResult.unauthorized(null);
        }
        
        // 结算订单
        boolean success = orderSettlementService.settleOrder(orderId);
        if (success) {
            return CommonResult.success(true, "订单结算成功");
        } else {
            return CommonResult.failed("订单结算失败");
        }
    }
    
    /**
     * 批量结算订单（管理员操作）
     */
    @PostMapping("/batch-settle")
    public CommonResult<Map<String, Object>> batchSettleOrders(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestBody List<Long> orderIds) {
        
        // 验证用户权限（实际项目中需要验证是否为管理员）
        Long userId = userIdExtractor.extractUserId(token, userIdHeader);
        if (userId == null) {
            return CommonResult.unauthorized(null);
        }
        
        // 批量结算订单
        int successCount = orderSettlementService.batchSettleOrders(orderIds);
        
        // 返回结果
        Map<String, Object> result = Map.of(
                "total", orderIds.size(),
                "success", successCount,
                "failed", orderIds.size() - successCount
        );
        
        return CommonResult.success(result, "批量结算完成");
    }
}