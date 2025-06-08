package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.common.api.CommonResult;
import com.example.model.ProductReview;
import com.example.service.ProductReviewService;
import com.example.util.UserIdExtractor;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 商品评价控制器
 */
@Slf4j
@RestController
@RequestMapping("/product/review")
public class ProductReviewController {

    @Autowired
    private ProductReviewService productReviewService;
    
    @Autowired
    private UserIdExtractor userIdExtractor;
    
    /**
     * 创建商品评价
     */
    @PostMapping("/create")
    public CommonResult<ProductReview> createReview(
            @RequestBody ProductReview review,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        
        Long userId = userIdExtractor.extractUserId(token, userIdHeader);
        if (userId == null) {
            return CommonResult.unauthorized(null);
        }
        
        review.setUserId(userId);
        ProductReview savedReview = productReviewService.createReview(review);
        return CommonResult.success(savedReview);
    }
    
    /**
     * 获取商品评价列表
     */
    @GetMapping("/list/{productId}")
    public CommonResult<Map<String, Object>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "hasImage", required = false) Boolean hasImage,
            @RequestParam(value = "rating", required = false) Integer rating) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<ProductReview> reviewPage = productReviewService.getProductReviews(productId, hasImage, rating, pageable);
        
        Map<String, Object> result = new HashMap<>();
        result.put("reviews", reviewPage.getContent());
        result.put("totalPages", reviewPage.getTotalPages());
        result.put("totalElements", reviewPage.getTotalElements());
        result.put("currentPage", reviewPage.getNumber());
        
        // 添加评价统计信息
        Map<String, Object> stats = productReviewService.getProductReviewStats(productId);
        result.put("stats", stats);
        
        return CommonResult.success(result);
    }
    
    /**
     * 商家回复评价
     */
    @PostMapping("/reply/{reviewId}")
    public CommonResult<ProductReview> replyReview(
            @PathVariable Long reviewId,
            @RequestParam String replyContent,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        
        Long userId = userIdExtractor.extractUserId(token, userIdHeader);
        if (userId == null) {
            return CommonResult.unauthorized(null);
        }
        
        ProductReview review = productReviewService.replyReview(reviewId, replyContent, userId);
        return CommonResult.success(review);
    }
    
    /**
     * 获取用户的评价列表
     */
    @GetMapping("/user")
    public CommonResult<List<ProductReview>> getUserReviews(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        
        Long userId = userIdExtractor.extractUserId(token, userIdHeader);
        if (userId == null) {
            return CommonResult.unauthorized(null);
        }
        
        List<ProductReview> reviews = productReviewService.getUserReviews(userId);
        return CommonResult.success(reviews);
    }
    
    /**
     * 修改评价状态（显示/隐藏）
     */
    @PostMapping("/status/{reviewId}")
    public CommonResult<ProductReview> updateReviewStatus(
            @PathVariable Long reviewId,
            @RequestParam Integer status,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        
        Long userId = userIdExtractor.extractUserId(token, userIdHeader);
        if (userId == null) {
            return CommonResult.unauthorized(null);
        }
        
        ProductReview review = productReviewService.updateReviewStatus(reviewId, status, userId);
        return CommonResult.success(review);
    }
    
    /**
     * 删除评价
     */
    @DeleteMapping("/{reviewId}")
    public CommonResult<Void> deleteReview(
            @PathVariable Long reviewId,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        
        Long userId = userIdExtractor.extractUserId(token, userIdHeader);
        if (userId == null) {
            return CommonResult.unauthorized(null);
        }
        
        boolean success = productReviewService.deleteReview(reviewId, userId);
        if (success) {
            return CommonResult.success(null);
        } else {
            return CommonResult.failed("删除评价失败");
        }
    }
} 