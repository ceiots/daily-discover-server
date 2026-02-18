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

    @GetMapping("/user/{userId}")
    @ApiLog("根据用户ID查询评论统计")
    public ResponseEntity<UserReviewStats> getUserReviewStatsByUserId(@PathVariable Long userId) {
        try {
            UserReviewStats stats = userReviewStatsService.getByUserId(userId);
            return stats != null ? ResponseEntity.ok(stats) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/top-reviewers")
    @ApiLog("获取活跃评论用户排名")
    public ResponseEntity<List<UserReviewStats>> getTopReviewers(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<UserReviewStats> stats = userReviewStatsService.getTopReviewers(limit);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/high-quality")
    @ApiLog("获取高质量评论用户")
    public ResponseEntity<List<UserReviewStats>> getHighQualityReviewers(@RequestParam(defaultValue = "4.5") Double minAvgRating,
                                                                        @RequestParam(defaultValue = "10") int limit) {
        try {
            List<UserReviewStats> stats = userReviewStatsService.getHighQualityReviewers(minAvgRating, limit);
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
            stats.setId(id);
            boolean success = userReviewStatsService.updateById(stats);
            return success ? ResponseEntity.ok(stats) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/user/{userId}/increment")
    @ApiLog("增加用户评论统计")
    public ResponseEntity<Void> incrementUserReviewStats(@PathVariable Long userId, @RequestParam Integer rating) {
        try {
            boolean success = userReviewStatsService.incrementReviewStats(userId, rating);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/user/{userId}/decrement")
    @ApiLog("减少用户评论统计")
    public ResponseEntity<Void> decrementUserReviewStats(@PathVariable Long userId, @RequestParam Integer rating) {
        try {
            boolean success = userReviewStatsService.decrementReviewStats(userId, rating);
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