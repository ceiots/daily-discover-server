package com.example.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.result.Result;
import com.example.model.UserBehavior;
import com.example.service.UserBehaviorService;
import com.example.util.UserIdExtractor;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户行为控制器
 */
@Slf4j
@RestController
@RequestMapping("/user/behavior")
public class UserBehaviorController {

    @Autowired
    private UserBehaviorService userBehaviorService;
    
    @Autowired
    private UserIdExtractor userIdExtractor;
    
    /**
     * 记录用户行为
     */
    @PostMapping("/record")
    public ResponseEntity<Result<Void>> recordBehavior(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestBody UserBehavior behavior) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return ResponseEntity.ok(Result.unauthorized(null));
            }
            
            behavior.setUserId(userId);
            userBehaviorService.recordBehavior(behavior);
            return ResponseEntity.ok(Result.success());
        } catch (Exception e) {
            log.error("记录用户行为失败", e);
            return ResponseEntity.ok(Result.failed("记录用户行为失败：" + e.getMessage()));
        }
    }
    

    /**
     * 获取用户行为历史
     */
    @GetMapping("/history")
    public ResponseEntity<Result<List<UserBehavior>>> getUserBehaviorHistory(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return ResponseEntity.ok(Result.unauthorized(null));
            }
            
            List<UserBehavior> history = userBehaviorService.getUserBehaviorHistory(userId, limit);
            return ResponseEntity.ok(Result.success(history));
        } catch (Exception e) {
            log.error("获取用户行为历史失败", e);
            return ResponseEntity.ok(Result.failed("获取用户行为历史失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取用户特定类型的行为历史
     */
    @GetMapping("/history/type")
    public ResponseEntity<Result<List<UserBehavior>>> getUserBehaviorHistoryByType(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestParam("type") String behaviorType,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return ResponseEntity.ok(Result.unauthorized(null));
            }
            
            List<UserBehavior> history = userBehaviorService.getUserBehaviorHistoryByType(userId, behaviorType, limit);
            return ResponseEntity.ok(Result.success(history));
        } catch (Exception e) {
            log.error("获取用户行为历史失败", e);
            return ResponseEntity.ok(Result.failed("获取用户行为历史失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取用户在特定时间范围内的行为历史
     */
    @GetMapping("/history/time-range")
    public ResponseEntity<Result<List<UserBehavior>>> getUserBehaviorHistoryByTimeRange(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestParam("startTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam("endTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return ResponseEntity.ok(Result.unauthorized(null));
            }
            
            List<UserBehavior> history = userBehaviorService.getUserBehaviorHistoryByTimeRange(userId, startTime, endTime);
            return ResponseEntity.ok(Result.success(history));
        } catch (Exception e) {
            log.error("获取用户行为历史失败", e);
            return ResponseEntity.ok(Result.failed("获取用户行为历史失败：" + e.getMessage()));
        }
    }
    
    /**
     * 手动更新用户偏好
     */
    @PostMapping("/update-preferences")
    public ResponseEntity<Result<Void>> updateUserPreferences(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return ResponseEntity.ok(Result.unauthorized(null));
            }
            
            userBehaviorService.updateUserPreferences(userId);
            return ResponseEntity.ok(Result.success());
        } catch (Exception e) {
            log.error("更新用户偏好失败", e);
            return ResponseEntity.ok(Result.failed("更新用户偏好失败：" + e.getMessage()));
        }
    }
    
    
} 