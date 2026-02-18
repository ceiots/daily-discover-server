package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;

import com.dailydiscover.model.ReviewReply;
import com.dailydiscover.model.ReviewStats;
import com.dailydiscover.model.UserReview;
import com.dailydiscover.model.UserReviewDetail;
import com.dailydiscover.model.UserReviewStats;
import com.dailydiscover.service.UserReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户评价控制器 - RESTful风格
 * 处理商品评价、点赞、回复等操作
 */
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class UserReviewController {
    
    private final UserReviewService userReviewService;

    // ==================== 评价基础操作 ====================
    
    @PostMapping
    @ApiLog("创建评价")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> createReview(@RequestBody Map<String, Object> request) {
        try {
            UserReview userReview = new UserReview();
            userReview.setProductId(Long.parseLong(request.get("productId").toString()));
            userReview.setUserId(Long.parseLong(request.get("userId").toString()));
            userReview.setOrderId(Long.parseLong(request.get("orderId").toString()));
            userReview.setRating(Integer.parseInt(request.get("rating").toString()));
            userReview.setTitle((String) request.get("title"));
            userReview.setIsAnonymous(Boolean.parseBoolean(request.get("isAnonymous").toString()));
            userReview.setIsVerifiedPurchase(Boolean.parseBoolean(request.get("isVerifiedPurchase").toString()));
            userReview.setStatus("pending");
            
            UserReviewDetail reviewDetail = new UserReviewDetail();
            reviewDetail.setUserAvatar((String) request.get("userAvatar"));
            reviewDetail.setComment((String) request.get("comment"));
            reviewDetail.setImageUrls((String) request.get("imageUrls"));
            reviewDetail.setVideoUrl((String) request.get("videoUrl"));
            
            userReviewService.save(userReview, reviewDetail);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("reviewId", userReview.getId());
            result.put("message", "评价创建成功");
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "创建评价失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
    
    @GetMapping("/{reviewId}")
    @ApiLog("获取评价详情")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, Object>> getReview(@PathVariable Long reviewId) {
        try {
            UserReview review = userReviewService.findById(reviewId);
            UserReviewDetail detail = userReviewService.getReviewDetailByReviewId(reviewId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("review", review);
            result.put("detail", detail);
            result.put("success", true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{reviewId}")
    @ApiLog("更新评价")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> updateReview(@PathVariable Long reviewId, @RequestBody Map<String, Object> request) {
        try {
            UserReview userReview = userReviewService.findById(reviewId);
            userReview.setRating(Integer.parseInt(request.get("rating").toString()));
            userReview.setTitle((String) request.get("title"));
            userReview.setIsAnonymous(Boolean.parseBoolean(request.get("isAnonymous").toString()));
            
            UserReviewDetail reviewDetail = userReviewService.getReviewDetailByReviewId(reviewId);
            reviewDetail.setComment((String) request.get("comment"));
            reviewDetail.setImageUrls((String) request.get("imageUrls"));
            reviewDetail.setVideoUrl((String) request.get("videoUrl"));
            
            userReviewService.update(userReview, reviewDetail);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "评价更新成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "更新评价失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
    
    @DeleteMapping("/{reviewId}")
    @ApiLog("删除评价")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> deleteReview(@PathVariable Long reviewId) {
        try {
            userReviewService.delete(reviewId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "评价删除成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "删除评价失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
    
    // ==================== 评价查询 ====================
    
    @GetMapping("/user/{userId}")
    @ApiLog("获取用户评价列表")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<UserReview>> getUserReviews(@PathVariable Long userId) {
        try {
            List<UserReview> reviews = userReviewService.findByUserId(userId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/product/{productId}")
    @ApiLog("获取商品评价列表")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<UserReview>> getProductReviews(@PathVariable Long productId) {
        try {
            List<UserReview> reviews = userReviewService.findByProductId(productId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/product/{productId}/recent")
    @ApiLog("获取商品最新评价")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<UserReview>> getRecentReviews(@PathVariable Long productId, 
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<UserReview> reviews = userReviewService.findRecentReviewsByProductId(productId, limit);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/product/{productId}/top")
    @ApiLog("获取商品热门评价")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<UserReview>> getTopReviews(@PathVariable Long productId, 
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<UserReview> reviews = userReviewService.findTopReviewsByProductId(productId, limit);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/product/{productId}/with-images")
    @ApiLog("获取商品带图评价")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<UserReview>> getReviewsWithImages(@PathVariable Long productId) {
        try {
            List<UserReview> reviews = userReviewService.findReviewsWithImagesByProductId(productId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/product/{productId}/verified")
    @ApiLog("获取商品已验证购买评价")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<UserReview>> getVerifiedPurchaseReviews(@PathVariable Long productId) {
        try {
            List<UserReview> reviews = userReviewService.findVerifiedPurchaseReviews(productId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/product/{productId}/anonymous")
    @ApiLog("获取商品匿名评价")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<UserReview>> getAnonymousReviews(@PathVariable Long productId) {
        try {
            List<UserReview> reviews = userReviewService.findAnonymousReviews(productId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ==================== 评价统计和分析 ====================
    
    @GetMapping("/product/{productId}/stats")
    @ApiLog("获取商品评价统计")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ReviewStats> getProductReviewStats(@PathVariable Long productId) {
        try {
            ReviewStats stats = userReviewService.getProductReviewStats(productId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{reviewId}/stats")
    @ApiLog("获取评价统计")
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserReviewStats> getReviewStats(@PathVariable Long reviewId) {
        try {
            UserReviewStats stats = userReviewService.getReviewStatsByReviewId(reviewId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/product/{productId}/analysis")
    @ApiLog("获取商品评价分析")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, Object>> getReviewAnalysis(@PathVariable Long productId) {
        try {
            Map<String, Object> analysis = userReviewService.getReviewAnalysisByProductId(productId);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ==================== 评价审核管理 ====================
    
    @GetMapping("/pending")
    @ApiLog("获取待审核评价列表")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserReview>> getPendingReviews() {
        try {
            List<UserReview> reviews = userReviewService.findPendingReviews();
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/{reviewId}/approve")
    @ApiLog("审核通过评价")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> approveReview(@PathVariable Long reviewId) {
        try {
            userReviewService.approveReview(reviewId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "评价审核通过成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "审核评价失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
    
    @PostMapping("/{reviewId}/reject")
    @ApiLog("审核拒绝评价")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> rejectReview(@PathVariable Long reviewId, @RequestBody Map<String, Object> request) {
        try {
            String reason = (String) request.get("reason");
            userReviewService.rejectReview(reviewId, reason);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "评价审核拒绝成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "拒绝评价失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
    
    @PostMapping("/{reviewId}/hide")
    @ApiLog("隐藏评价")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> hideReview(@PathVariable Long reviewId) {
        try {
            userReviewService.hideReview(reviewId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "评价隐藏成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "隐藏评价失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
    
    @PostMapping("/batch-approve")
    @ApiLog("批量审核通过评价")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> batchApproveReviews(@RequestBody Map<String, Object> request) {
        try {
            List<Long> reviewIds = (List<Long>) request.get("reviewIds");
            userReviewService.batchApproveReviews(reviewIds);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "批量审核通过成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "批量审核通过失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
    
    @PostMapping("/batch-reject")
    @ApiLog("批量审核拒绝评价")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> batchRejectReviews(@RequestBody Map<String, Object> request) {
        try {
            List<Long> reviewIds = (List<Long>) request.get("reviewIds");
            String reason = (String) request.get("reason");
            userReviewService.batchRejectReviews(reviewIds, reason);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "批量审核拒绝成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "批量审核拒绝失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
    
    // ==================== 评价回复 ====================
    
    @PostMapping("/{reviewId}/reply")
    @ApiLog("回复评价")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> replyReview(@PathVariable Long reviewId, @RequestBody ReviewReply reply) {
        try {
            reply.setReviewId(reviewId);
            userReviewService.replyReview(reply);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("replyId", reply.getId());
            result.put("message", "回复成功");
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "回复失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
    
    @GetMapping("/{reviewId}/replies")
    @ApiLog("获取评价回复列表")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ReviewReply>> getReviewReplies(@PathVariable Long reviewId) {
        try {
            List<ReviewReply> replies = userReviewService.findRepliesByReviewId(reviewId);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{reviewId}/seller-replies")
    @ApiLog("获取商家回复列表")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ReviewReply>> getSellerReplies(@PathVariable Long reviewId) {
        try {
            List<ReviewReply> replies = userReviewService.findSellerRepliesByReviewId(reviewId);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/replies/{replyId}")
    @ApiLog("删除回复")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> deleteReply(@PathVariable Long replyId) {
        try {
            userReviewService.deleteReply(replyId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "回复删除成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "删除回复失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
}