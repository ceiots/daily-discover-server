package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.common.api.CommonResult;
import com.example.service.ProductAuditLogService;
import com.example.service.ProductCategoryRelationService;
import com.example.service.ProductReviewService;
import com.example.service.ProductService;
import com.example.util.UserIdExtractor;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/product/statistics")
public class ProductStatisticsController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private ProductReviewService productReviewService;
    
    @Autowired
    private ProductAuditLogService productAuditLogService;
    
    @Autowired
    private ProductCategoryRelationService productCategoryRelationService;
    
    @Autowired
    private UserIdExtractor userIdExtractor;
    
    /**
     * 获取商品统计数据总览
     */
    @GetMapping("/overview")
    public CommonResult<Map<String, Object>> getStatisticsOverview(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        
        Long userId = userIdExtractor.extractUserId(token, userIdHeader);
        if (userId == null) {
            return CommonResult.unauthorized(null);
        }
        
        Map<String, Object> result = new HashMap<>();
        
        // 商品总数
        int totalProducts = productService.countProducts();
        result.put("totalProducts", totalProducts);
        
        // 已上线商品数
        int approvedProducts = productService.countApprovedProducts();
        result.put("approvedProducts", approvedProducts);
        
        // 审核状态统计
        Map<Integer, Integer> auditStatusCounts = productAuditLogService.countByAuditStatus();
        result.put("auditStatusCounts", auditStatusCounts);
        
        // 其他统计数据...
        
        return CommonResult.success(result);
    }
    
    /**
     * 获取商品评价统计
     */
    @GetMapping("/reviews/{productId}")
    public CommonResult<Map<String, Object>> getProductReviewStatistics(@PathVariable Long productId) {
        Map<String, Object> stats = productReviewService.getProductReviewStats(productId);
        return CommonResult.success(stats);
    }
    
    /**
     * 获取商品按分类分布统计
     */
    @GetMapping("/category-distribution")
    public CommonResult<Map<String, Object>> getCategoryDistribution(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        
        Long userId = userIdExtractor.extractUserId(token, userIdHeader);
        if (userId == null) {
            return CommonResult.unauthorized(null);
        }
        
        // 这里需要实现分类分布统计逻辑
        Map<String, Object> result = new HashMap<>();
        // ...
        
        return CommonResult.success(result);
    }
    
    /**
     * 获取商品审核统计
     */
    @GetMapping("/audit")
    public CommonResult<Map<String, Object>> getAuditStatistics(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        
        Long userId = userIdExtractor.extractUserId(token, userIdHeader);
        if (userId == null) {
            return CommonResult.unauthorized(null);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("auditStatusCounts", productAuditLogService.countByAuditStatus());
        
        return CommonResult.success(result);
    }
} 