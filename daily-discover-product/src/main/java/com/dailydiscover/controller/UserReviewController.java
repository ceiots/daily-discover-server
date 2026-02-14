package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.ReviewLike;
import com.dailydiscover.model.ReviewReply;
import com.dailydiscover.model.ReviewStats;
import com.dailydiscover.model.UserReview;
import com.dailydiscover.service.UserReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 用户评价控制器 - RESTful风格
 * 处理商品评价、点赞、回复等操作
 */
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class UserReviewController {
    
    private final UserReviewService userReviewService;

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

    @GetMapping("/{reviewId}")
    @ApiLog("获取评价详情")
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserReview> getReviewById(@PathVariable Long reviewId) {
        try {
            UserReview review = userReviewService.findById(reviewId);
            if (review != null) {
                return ResponseEntity.ok(review);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

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

    @PostMapping
    @ApiLog("创建评价")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserReview> createReview(@RequestBody UserReview review) {
        try {
            userReviewService.save(review);
            return ResponseEntity.status(HttpStatus.CREATED).body(review);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{reviewId}")
    @ApiLog("更新评价")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserReview> updateReview(@PathVariable Long reviewId, @RequestBody UserReview review) {
        try {
            review.setId(reviewId);
            userReviewService.update(review);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{reviewId}")
    @ApiLog("删除评价")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        try {
            userReviewService.delete(reviewId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{reviewId}/like")
    @ApiLog("点赞评价")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewLike> likeReview(@PathVariable Long reviewId, @RequestBody ReviewLike like) {
        try {
            like.setReviewId(reviewId);
            userReviewService.likeReview(like);
            return ResponseEntity.status(HttpStatus.CREATED).body(like);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    @ApiLog("取消点赞")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unlikeReview(@PathVariable Long reviewId, @PathVariable Long userId) {
        try {
            userReviewService.unlikeReview(reviewId, userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{reviewId}/reply")
    @ApiLog("回复评价")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewReply> replyReview(@PathVariable Long reviewId, @RequestBody ReviewReply reply) {
        try {
            reply.setReviewId(reviewId);
            userReviewService.replyReview(reply);
            return ResponseEntity.status(HttpStatus.CREATED).body(reply);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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

    @GetMapping("/product/{productId}/top")
    @ApiLog("获取商品热门评价")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<UserReview>> getTopReviews(@PathVariable Long productId, 
                                                          @RequestParam(defaultValue = "5") int limit) {
        try {
            List<UserReview> reviews = userReviewService.findTopReviewsByProductId(productId, limit);
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

    @GetMapping("/product/{productId}/with-images")
    @ApiLog("获取带图片的评价")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<UserReview>> getReviewsWithImages(@PathVariable Long productId) {
        try {
            List<UserReview> reviews = userReviewService.findReviewsWithImagesByProductId(productId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}