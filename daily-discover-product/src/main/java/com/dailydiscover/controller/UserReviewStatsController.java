package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.UserReviewStats;
import com.dailydiscover.service.UserReviewStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-review-stats")
@RequiredArgsConstructor
public class UserReviewStatsController {

    private final UserReviewStatsService userReviewStatsService;

    @GetMapping
    @ApiLog("获取所有用户评论统计")
    public ResponseEntity<List<UserReviewStats>> getAllUserReviewStats() {
        try {
            List<UserReviewStats> stats = userReviewStatsService.list();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取用户评论统计")
    public ResponseEntity<UserReviewStats> getUserReviewStatsById(@PathVariable Long id) {
        try {
            UserReviewStats stats = userReviewStatsService.getById(id);
            return stats != null ? ResponseEntity.ok(stats) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/review/{reviewId}")
    @ApiLog("根据评价ID查询评论统计")
    public ResponseEntity<UserReviewStats> getUserReviewStatsByReviewId(@PathVariable Long reviewId) {
        try {
            UserReviewStats stats = userReviewStatsService.getByReviewId(reviewId);
            return stats != null ? ResponseEntity.ok(stats) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/top-helpful")
    @ApiLog("获取高有用数量评论排名")
    public ResponseEntity<List<UserReviewStats>> getTopHelpfulReviews(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<UserReviewStats> stats = userReviewStatsService.getTopHelpfulReviews(limit);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/top-liked")
    @ApiLog("获取高点赞数量评论排名")
    public ResponseEntity<List<UserReviewStats>> getTopLikedReviews(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<UserReviewStats> stats = userReviewStatsService.getTopLikedReviews(limit);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建用户评论统计")
    public ResponseEntity<UserReviewStats> createUserReviewStats(@RequestBody UserReviewStats stats) {
        try {
            boolean success = userReviewStatsService.save(stats);
            return success ? ResponseEntity.ok(stats) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新用户评论统计")
    public ResponseEntity<UserReviewStats> updateUserReviewStats(@PathVariable Long id, @RequestBody UserReviewStats stats) {
        try {
            stats.setReviewId(id);
            boolean success = userReviewStatsService.updateById(stats);
            return success ? ResponseEntity.ok(stats) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/review/{reviewId}/helpful")
    @ApiLog("增加有用数量统计")
    public ResponseEntity<Void> incrementHelpfulCount(@PathVariable Long reviewId) {
        try {
            boolean success = userReviewStatsService.incrementHelpfulCount(reviewId);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/review/{reviewId}/reply")
    @ApiLog("增加回复数量统计")
    public ResponseEntity<Void> incrementReplyCount(@PathVariable Long reviewId) {
        try {
            boolean success = userReviewStatsService.incrementReplyCount(reviewId);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/review/{reviewId}/like")
    @ApiLog("增加点赞数量统计")
    public ResponseEntity<Void> incrementLikeCount(@PathVariable Long reviewId) {
        try {
            boolean success = userReviewStatsService.incrementLikeCount(reviewId);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除用户评论统计")
    public ResponseEntity<Void> deleteUserReviewStats(@PathVariable Long id) {
        try {
            boolean success = userReviewStatsService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}