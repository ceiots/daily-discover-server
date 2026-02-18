package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.UserBehaviorLog;
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
            List<UserBehaviorLog> logs = userBehaviorLogService.list();
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取用户行为日志")
    public ResponseEntity<UserBehaviorLog> getUserBehaviorLogById(@PathVariable Long id) {
        try {
            UserBehaviorLog log = userBehaviorLogService.getById(id);
            return log != null ? ResponseEntity.ok(log) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    @ApiLog("根据用户ID获取行为日志")
    public ResponseEntity<List<UserBehaviorLog>> getUserBehaviorLogsByUserId(@PathVariable Long userId) {
        try {
            List<UserBehaviorLog> logs = userBehaviorLogService.getByUserId(userId);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/behavior/{behaviorType}")
    @ApiLog("根据行为类型获取日志")
    public ResponseEntity<List<UserBehaviorLog>> getUserBehaviorLogsByBehaviorType(@PathVariable String behaviorType) {
        try {
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
            List<UserBehaviorLog> logs = userBehaviorLogService.getByProductId(productId);
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
}