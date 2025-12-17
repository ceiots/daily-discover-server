package com.dailydiscover.controller;

import com.dailydiscover.user.dto.UserCollectionResponse;
import com.dailydiscover.user.entity.UserCollection;
import com.dailydiscover.user.service.UserCollectionService;
import com.dailydiscover.util.LogTracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户收藏控制器
 */
@Slf4j
@RestController
@RequestMapping("/collections")
@RequiredArgsConstructor
public class UserCollectionController {

    private final UserCollectionService userCollectionService;

    /**
     * 添加收藏
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addCollection(@RequestBody UserCollection userCollection) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserCollectionController.addCollection", "开始添加收藏", userCollection);
        
        try {
            UserCollectionResponse response = userCollectionService.addCollection(userCollection);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "收藏添加成功");
            result.put("data", response);
            
            LogTracer.traceMethod("UserCollectionController.addCollection", "收藏添加成功", result);
            LogTracer.tracePerformance("UserCollectionController.addCollection", startTime, System.currentTimeMillis());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            LogTracer.traceException("UserCollectionController.addCollection", "添加收藏失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 获取用户的收藏列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getCollectionsByUserId(@PathVariable Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserCollectionController.getCollectionsByUserId", "开始获取收藏列表", userId);
        
        try {
            List<UserCollectionResponse> collections = userCollectionService.getCollectionsByUserId(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", collections);
            result.put("total", collections.size());
            
            LogTracer.traceMethod("UserCollectionController.getCollectionsByUserId", "获取收藏列表成功", result);
            LogTracer.tracePerformance("UserCollectionController.getCollectionsByUserId", startTime, System.currentTimeMillis());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            LogTracer.traceException("UserCollectionController.getCollectionsByUserId", "获取收藏列表失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 删除收藏
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCollection(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserCollectionController.deleteCollection", "开始删除收藏", id);
        
        try {
            boolean result = userCollectionService.deleteCollection(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result);
            response.put("message", result ? "收藏删除成功" : "收藏删除失败");
            
            LogTracer.traceMethod("UserCollectionController.deleteCollection", "删除收藏完成", response);
            LogTracer.tracePerformance("UserCollectionController.deleteCollection", startTime, System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LogTracer.traceException("UserCollectionController.deleteCollection", "删除收藏失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 检查用户是否已收藏某个内容
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> isItemCollected(
            @RequestParam Long userId,
            @RequestParam String itemType,
            @RequestParam String itemId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserCollectionController.isItemCollected", "开始检查收藏状态", Map.of("userId", userId, "itemType", itemType, "itemId", itemId));
        
        try {
            boolean isCollected = userCollectionService.isItemCollected(userId, itemType, itemId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("isCollected", isCollected);
            
            LogTracer.traceMethod("UserCollectionController.isItemCollected", "检查收藏状态完成", response);
            LogTracer.tracePerformance("UserCollectionController.isItemCollected", startTime, System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LogTracer.traceException("UserCollectionController.isItemCollected", "检查收藏状态失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 统计用户收藏数量
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Object>> countCollections(@PathVariable Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserCollectionController.countCollections", "开始获取收藏数量", userId);
        
        try {
            int count = userCollectionService.countCollections(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);
            
            LogTracer.traceMethod("UserCollectionController.countCollections", "获取收藏数量完成", response);
            LogTracer.tracePerformance("UserCollectionController.countCollections", startTime, System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LogTracer.traceException("UserCollectionController.countCollections", "获取收藏数量失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 清空用户的收藏
     */
    @DeleteMapping("/user/{userId}/clear")
    public ResponseEntity<Map<String, Object>> clearCollections(@PathVariable Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserCollectionController.clearCollections", "开始清空用户收藏", userId);
        
        try {
            boolean result = userCollectionService.clearCollections(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result);
            response.put("message", result ? "收藏清空成功" : "收藏清空失败");
            
            LogTracer.traceMethod("UserCollectionController.clearCollections", "清空用户收藏完成", response);
            LogTracer.tracePerformance("UserCollectionController.clearCollections", startTime, System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LogTracer.traceException("UserCollectionController.clearCollections", "清空用户收藏失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}