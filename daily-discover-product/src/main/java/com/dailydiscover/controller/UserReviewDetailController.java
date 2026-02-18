package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.UserReviewDetail;
import com.dailydiscover.service.UserReviewDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-review-details")
@RequiredArgsConstructor
public class UserReviewDetailController {

    private final UserReviewDetailService userReviewDetailService;

    @GetMapping
    @ApiLog("获取所有用户评论详情")
    public ResponseEntity<List<UserReviewDetail>> getAllUserReviewDetails() {
        try {
            List<UserReviewDetail> details = userReviewDetailService.list();
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取用户评论详情")
    public ResponseEntity<UserReviewDetail> getUserReviewDetailById(@PathVariable Long id) {
        try {
            UserReviewDetail detail = userReviewDetailService.getById(id);
            return detail != null ? ResponseEntity.ok(detail) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/review/{reviewId}")
    @ApiLog("根据评论ID查询评论详情")
    public ResponseEntity<UserReviewDetail> getUserReviewDetailByReviewId(@PathVariable Long reviewId) {
        try {
            UserReviewDetail detail = userReviewDetailService.getByReviewId(reviewId);
            return detail != null ? ResponseEntity.ok(detail) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/with-images")
    @ApiLog("获取带图片的评论列表")
    public ResponseEntity<List<UserReviewDetail>> getReviewsWithImages(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<UserReviewDetail> details = userReviewDetailService.getReviewsWithImages(limit);
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建用户评论详情")
    public ResponseEntity<UserReviewDetail> createUserReviewDetail(@RequestBody UserReviewDetail detail) {
        try {
            boolean success = userReviewDetailService.save(detail);
            return success ? ResponseEntity.ok(detail) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新用户评论详情")
    public ResponseEntity<UserReviewDetail> updateUserReviewDetail(@PathVariable Long id, @RequestBody UserReviewDetail detail) {
        try {
            detail.setId(id);
            boolean success = userReviewDetailService.updateById(detail);
            return success ? ResponseEntity.ok(detail) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/review/{reviewId}/images")
    @ApiLog("添加评论图片")
    public ResponseEntity<Void> addReviewImages(@PathVariable Long reviewId, @RequestParam String imageUrls) {
        try {
            boolean success = userReviewDetailService.addReviewImages(reviewId, imageUrls);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除用户评论详情")
    public ResponseEntity<Void> deleteUserReviewDetail(@PathVariable Long id) {
        try {
            boolean success = userReviewDetailService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}