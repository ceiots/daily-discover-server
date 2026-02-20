package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.UserBehaviorLog;
import com.dailydiscover.model.UserBehaviorLogDetails;
import com.dailydiscover.service.UserBehaviorLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-behavior-logs")
@RequiredArgsConstructor
public class UserBehaviorLogController {

    private final UserBehaviorLogService userBehaviorLogService;

    @GetMapping
    @ApiLog("获取所有用户行为日志")
    public ResponseEntity<List<UserBehaviorLog>> getAllUserBehaviorLogs() {
        try {
            // 使用新的完整行为历史方法，限制返回100条记录
            List<UserBehaviorLog> logs = userBehaviorLogService.getCompleteUserBehaviorHistory(null, 100);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取用户行为日志")
    public ResponseEntity<UserBehaviorLog> getUserBehaviorLogById(@PathVariable Long id) {
        try {
            // 先获取行为详情，然后构建完整对象
            UserBehaviorLogDetails details = userBehaviorLogService.getBehaviorDetails(id);
            if (details != null && details.getId() != null) {
                // 这里需要从核心表获取基础信息，简化处理返回详情对象
                UserBehaviorLog log = new UserBehaviorLog();
                log.setId(details.getId());
                log.setReferrerUrl(details.getReferrerUrl());
                log.setBehaviorContext(details.getBehaviorContext());
                log.setCreatedAt(details.getCreatedAt());
                return ResponseEntity.ok(log);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    @ApiLog("根据用户ID获取行为日志")
    public ResponseEntity<List<UserBehaviorLog>> getUserBehaviorLogsByUserId(@PathVariable Long userId) {
        try {
            List<UserBehaviorLog> logs = userBehaviorLogService.getUserBehaviorHistory(userId, 100);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/behavior/{behaviorType}")
    @ApiLog("根据行为类型获取日志")
    public ResponseEntity<List<UserBehaviorLog>> getUserBehaviorLogsByBehaviorType(@PathVariable String behaviorType) {
        try {
            // 使用按行为类型查询的方法
            List<UserBehaviorLog> logs = userBehaviorLogService.getByBehaviorType(behaviorType);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/product/{productId}")
    @ApiLog("根据商品ID获取行为日志")
    public ResponseEntity<List<UserBehaviorLog>> getUserBehaviorLogsByProductId(@PathVariable Long productId) {
        try {
            List<UserBehaviorLog> logs = userBehaviorLogService.getProductBehaviorHistory(productId, 100);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建用户行为日志")
    public ResponseEntity<UserBehaviorLog> createUserBehaviorLog(@RequestBody UserBehaviorLog log) {
        try {
            boolean success = userBehaviorLogService.save(log);
            return success ? ResponseEntity.ok(log) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新用户行为日志")
    public ResponseEntity<UserBehaviorLog> updateUserBehaviorLog(@PathVariable Long id, @RequestBody UserBehaviorLog log) {
        try {
            log.setId(id);
            boolean success = userBehaviorLogService.updateById(log);
            return success ? ResponseEntity.ok(log) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除用户行为日志")
    public ResponseEntity<Void> deleteUserBehaviorLog(@PathVariable Long id) {
        try {
            boolean success = userBehaviorLogService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/with-details")
    @ApiLog("记录用户行为（包含详情）")
    public ResponseEntity<Void> recordUserBehaviorWithDetails(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam String behaviorType,
            @RequestParam(required = false) String sessionId,
            @RequestParam(required = false) String referrerUrl,
            @RequestParam(required = false) String behaviorContext) {
        try {
            boolean success = userBehaviorLogService.recordUserBehaviorWithDetails(
                    userId, productId, behaviorType, sessionId, referrerUrl, behaviorContext);
            return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/user/{userId}/complete")
    @ApiLog("获取用户完整行为历史（包含详情）")
    public ResponseEntity<List<UserBehaviorLog>> getCompleteUserBehaviorHistory(@PathVariable Long userId) {
        try {
            List<UserBehaviorLog> logs = userBehaviorLogService.getCompleteUserBehaviorHistory(userId, 100);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/product/{productId}/complete")
    @ApiLog("获取商品完整行为历史（包含详情）")
    public ResponseEntity<List<UserBehaviorLog>> getCompleteProductBehaviorHistory(@PathVariable Long productId) {
        try {
            List<UserBehaviorLog> logs = userBehaviorLogService.getCompleteProductBehaviorHistory(productId, 100);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/details")
    @ApiLog("获取行为详情")
    public ResponseEntity<UserBehaviorLogDetails> getBehaviorDetails(@PathVariable Long id) {
        try {
            UserBehaviorLogDetails details = userBehaviorLogService.getBehaviorDetails(id);
            return details != null ? ResponseEntity.ok(details) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}