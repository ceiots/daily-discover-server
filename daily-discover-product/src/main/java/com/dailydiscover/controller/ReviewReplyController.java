package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.ReviewReply;
import com.dailydiscover.service.ReviewReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review-replies")
@RequiredArgsConstructor
public class ReviewReplyController {

    private final ReviewReplyService reviewReplyService;

    @GetMapping
    @ApiLog("获取所有评论回复")
    public ResponseEntity<List<ReviewReply>> getAllReviewReplies() {
        try {
            List<ReviewReply> replies = reviewReplyService.list();
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取评论回复")
    public ResponseEntity<ReviewReply> getReviewReplyById(@PathVariable Long id) {
        try {
            ReviewReply reply = reviewReplyService.getById(id);
            return reply != null ? ResponseEntity.ok(reply) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/review/{reviewId}")
    @ApiLog("根据评论ID查询回复")
    public ResponseEntity<List<ReviewReply>> getReviewRepliesByReviewId(@PathVariable Long reviewId) {
        try {
            List<ReviewReply> replies = reviewReplyService.getByReviewId(reviewId);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/replier/{replierId}")
    @ApiLog("根据回复者ID查询回复")
    public ResponseEntity<List<ReviewReply>> getReviewRepliesByReplierId(@PathVariable Long replierId) {
        try {
            List<ReviewReply> replies = reviewReplyService.getByReplierId(replierId);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/review/{reviewId}/count")
    @ApiLog("获取评论的回复统计")
    public ResponseEntity<Integer> getReplyCountByReviewId(@PathVariable Long reviewId) {
        try {
            Integer count = reviewReplyService.getReplyCountByReviewId(reviewId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("添加评论回复")
    public ResponseEntity<ReviewReply> addReviewReply(@RequestBody ReviewReply reply) {
        try {
            boolean success = reviewReplyService.save(reply);
            return success ? ResponseEntity.ok(reply) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新评论回复")
    public ResponseEntity<ReviewReply> updateReviewReply(@PathVariable Long id, @RequestBody ReviewReply reply) {
        try {
            reply.setId(id);
            boolean success = reviewReplyService.updateById(reply);
            return success ? ResponseEntity.ok(reply) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除评论回复")
    public ResponseEntity<Void> deleteReviewReply(@PathVariable Long id) {
        try {
            boolean success = reviewReplyService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}