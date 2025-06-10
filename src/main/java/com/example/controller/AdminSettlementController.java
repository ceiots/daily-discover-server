package com.example.controller;

import com.example.common.result.Result;
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
 * 管理员订单结算控制器
 */
@RestController
@RequestMapping("/admin/settlement")
public class AdminSettlementController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminSettlementController.class);
    
    @Autowired
    private OrderSettlementService orderSettlementService;
    
    @Autowired
    private UserIdExtractor userIdExtractor;
    
    /**
     * 获取所有待结算订单
     */
    @GetMapping("/pending")
    public Result<List<Order>> getPendingSettlementOrders(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestParam(value = "shopId", required = false) Long shopId,
            @RequestParam(value = "startDate", required = false) 
                @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(value = "endDate", required = false) 
                @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        
        // 验证用户权限（实际项目中需要验证是否为管理员）
        Long userId = userIdExtractor.extractUserId(token, userIdHeader);
        if (userId == null) {
            return Result.unauthorized(null);
        }
        
        // 获取待结算订单
        List<Order> orders;
        if (shopId != null) {
            orders = orderSettlementService.getPendingSettlementOrders(shopId, startDate, endDate);
        } else {
            // 获取所有店铺的待结算订单
            orders = orderSettlementService.getAllPendingSettlementOrders(startDate, endDate);
        }
        
        return Result.success(orders);
    }
    
    /**
     * 获取所有已结算订单
     */
    @GetMapping("/settled")
    public Result<List<Order>> getSettledOrders(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestParam(value = "shopId", required = false) Long shopId,
            @RequestParam(value = "startDate", required = false) 
                @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(value = "endDate", required = false) 
                @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        
        // 验证用户权限（实际项目中需要验证是否为管理员）
        Long userId = userIdExtractor.extractUserId(token, userIdHeader);
        if (userId == null) {
            return Result.unauthorized(null);
        }
        
        // 获取已结算订单
        List<Order> orders;
        if (shopId != null) {
            orders = orderSettlementService.getSettledOrders(shopId, startDate, endDate);
        } else {
            // 获取所有店铺的已结算订单
            orders = orderSettlementService.getAllSettledOrders(startDate, endDate);
        }
        
        return Result.success(orders);
    }
    
    /**
     * 结算订单（管理员操作）
     */
    @PostMapping("/settle/{orderId}")
    public Result<Boolean> settleOrder(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @PathVariable("orderId") Long orderId) {
        
        // 验证用户权限（实际项目中需要验证是否为管理员）
        Long userId = userIdExtractor.extractUserId(token, userIdHeader);
        if (userId == null) {
            return Result.unauthorized(null);
        }
        
        // 结算订单
        boolean success = orderSettlementService.settleOrder(orderId);
        if (success) {
            return Result.success(true, "订单结算成功");
        } else {
            return Result.failed("订单结算失败");
        }
    }
    
    /**
     * 批量结算订单（管理员操作）
     */
    @PostMapping("/batch-settle")
    public Result<Map<String, Object>> batchSettleOrders(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestBody List<Long> orderIds) {
        
        // 验证用户权限（实际项目中需要验证是否为管理员）
        Long userId = userIdExtractor.extractUserId(token, userIdHeader);
        if (userId == null) {
            return Result.unauthorized(null);
        }
        
        // 批量结算订单
        int successCount = orderSettlementService.batchSettleOrders(orderIds);
        
        // 返回结果
        Map<String, Object> result = Map.of(
                "total", orderIds.size(),
                "success", successCount,
                "failed", orderIds.size() - successCount
        );
        
        return Result.success(result, "批量结算完成");
    }
    
    /**
     * 获取结算统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getSettlementStatistics(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestParam(value = "startDate", required = false) 
                @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(value = "endDate", required = false) 
                @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        
        // 验证用户权限（实际项目中需要验证是否为管理员）
        Long userId = userIdExtractor.extractUserId(token, userIdHeader);
        if (userId == null) {
            return Result.unauthorized(null);
        }
        
        // 获取结算统计数据（实际项目中需要实现）
        Map<String, Object> statistics = Map.of(
                "pendingCount", 0,
                "settledCount", 0,
                "pendingAmount", 0.00,
                "settledAmount", 0.00,
                "commissionAmount", 0.00
        );
        
        return Result.success(statistics);
    }
}