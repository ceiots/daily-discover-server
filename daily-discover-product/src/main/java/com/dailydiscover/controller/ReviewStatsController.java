package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.ReviewStats;
import com.dailydiscover.service.ReviewStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review-stats")
@RequiredArgsConstructor
public class ReviewStatsController {

    private final ReviewStatsService reviewStatsService;

    @GetMapping
    @ApiLog("获取所有评论统计")
    public ResponseEntity<List<ReviewStats>> getAllReviewStats() {
        try {
            List<ReviewStats> stats = reviewStatsService.list();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取评论统计")
    public ResponseEntity<ReviewStats> getReviewStatsById(@PathVariable Long id) {
        try {
            ReviewStats stats = reviewStatsService.getById(id);
            return stats != null ? ResponseEntity.ok(stats) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/product/{productId}")
    @ApiLog("根据商品ID查询评论统计")
    public ResponseEntity<ReviewStats> getReviewStatsByProductId(@PathVariable Long productId) {
        try {
            ReviewStats stats = reviewStatsService.getByProductId(productId);
            return stats != null ? ResponseEntity.ok(stats) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/high-rated")
    @ApiLog("获取高评分商品列表")
    public ResponseEntity<List<ReviewStats>> getHighRatedProducts(@RequestParam(defaultValue = "4.0") Double minRating,
                                                                 @RequestParam(defaultValue = "10") int limit) {
        try {
            List<ReviewStats> stats = reviewStatsService.getHighRatedProducts(minRating, limit);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/ranking")
    @ApiLog("获取评论统计排名")
    public ResponseEntity<List<ReviewStats>> getReviewStatsRanking(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<ReviewStats> stats = reviewStatsService.getReviewStatsRanking(limit);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建评论统计")
    public ResponseEntity<ReviewStats> createReviewStats(@RequestBody ReviewStats stats) {
        try {
            boolean success = reviewStatsService.save(stats);
            return success ? ResponseEntity.ok(stats) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新评论统计")
    public ResponseEntity<ReviewStats> updateReviewStats(@PathVariable Long id, @RequestBody ReviewStats stats) {
        try {
            stats.setId(id);
            boolean success = reviewStatsService.updateById(stats);
            return success ? ResponseEntity.ok(stats) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/product/{productId}/increment")
    @ApiLog("增加评论统计")
    public ResponseEntity<Void> incrementReviewStats(@PathVariable Long productId, @RequestParam Integer rating) {
        try {
            boolean success = reviewStatsService.incrementReviewStats(productId, rating);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/product/{productId}/decrement")
    @ApiLog("减少评论统计")
    public ResponseEntity<Void> decrementReviewStats(@PathVariable Long productId, @RequestParam Integer rating) {
        try {
            boolean success = reviewStatsService.decrementReviewStats(productId, rating);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除评论统计")
    public ResponseEntity<Void> deleteReviewStats(@PathVariable Long id) {
        try {
            boolean success = reviewStatsService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}