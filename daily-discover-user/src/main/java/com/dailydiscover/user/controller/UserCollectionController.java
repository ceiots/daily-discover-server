package com.dailydiscover.user.controller;

import com.dailydiscover.user.dto.UserCollectionResponse;
import com.dailydiscover.user.entity.UserCollection;
import com.dailydiscover.user.service.UserCollectionService;
import com.dailydiscover.common.util.LogTracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<UserCollectionResponse> addCollection(@RequestBody UserCollection userCollection) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserCollectionController.addCollection", "开始添加收藏", userCollection);
        
        try {
            UserCollectionResponse response = userCollectionService.addCollection(userCollection);
            
            LogTracer.traceMethod("UserCollectionController.addCollection", "收藏添加成功", response);
            LogTracer.tracePerformance("UserCollectionController.addCollection", startTime, System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LogTracer.traceException("UserCollectionController.addCollection", "添加收藏失败", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取用户的收藏列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserCollectionResponse>> getCollectionsByUserId(@PathVariable Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserCollectionController.getCollectionsByUserId", "开始获取收藏列表", userId);
        
        try {
            List<UserCollectionResponse> collections = userCollectionService.getCollectionsByUserId(userId);
            
            LogTracer.traceMethod("UserCollectionController.getCollectionsByUserId", "获取收藏列表成功", collections);
            LogTracer.tracePerformance("UserCollectionController.getCollectionsByUserId", startTime, System.currentTimeMillis());
            
            return ResponseEntity.ok(collections);
        } catch (Exception e) {
            LogTracer.traceException("UserCollectionController.getCollectionsByUserId", "获取收藏列表失败", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除收藏
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollection(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserCollectionController.deleteCollection", "开始删除收藏", id);
        
        try {
            boolean result = userCollectionService.deleteCollection(id);
            
            LogTracer.traceMethod("UserCollectionController.deleteCollection", "删除收藏完成", result);
            LogTracer.tracePerformance("UserCollectionController.deleteCollection", startTime, System.currentTimeMillis());
            
            return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            LogTracer.traceException("UserCollectionController.deleteCollection", "删除收藏失败", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 检查用户是否已收藏某个内容
     */
    @GetMapping("/check")
    public ResponseEntity<Boolean> isItemCollected(
            @RequestParam Long userId,
            @RequestParam String itemType,
            @RequestParam String itemId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserCollectionController.isItemCollected", "开始检查收藏状态", Map.of("userId", userId, "itemType", itemType, "itemId", itemId));
        
        try {
            boolean isCollected = userCollectionService.isItemCollected(userId, itemType, itemId);
            
            LogTracer.traceMethod("UserCollectionController.isItemCollected", "检查收藏状态完成", isCollected);
            LogTracer.tracePerformance("UserCollectionController.isItemCollected", startTime, System.currentTimeMillis());
            
            return ResponseEntity.ok(isCollected);
        } catch (Exception e) {
            LogTracer.traceException("UserCollectionController.isItemCollected", "检查收藏状态失败", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 统计用户收藏数量
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Integer> countCollections(@PathVariable Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserCollectionController.countCollections", "开始获取收藏数量", userId);
        
        try {
            int count = userCollectionService.countCollections(userId);
            
            LogTracer.traceMethod("UserCollectionController.countCollections", "获取收藏数量完成", count);
            LogTracer.tracePerformance("UserCollectionController.countCollections", startTime, System.currentTimeMillis());
            
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            LogTracer.traceException("UserCollectionController.countCollections", "获取收藏数量失败", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 清空用户的收藏
     */
    @DeleteMapping("/user/{userId}/clear")
    public ResponseEntity<Void> clearCollections(@PathVariable Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserCollectionController.clearCollections", "开始清空用户收藏", userId);
        
        try {
            boolean result = userCollectionService.clearCollections(userId);
            
            LogTracer.traceMethod("UserCollectionController.clearCollections", "清空用户收藏完成", result);
            LogTracer.tracePerformance("UserCollectionController.clearCollections", startTime, System.currentTimeMillis());
            
            return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            LogTracer.traceException("UserCollectionController.clearCollections", "清空用户收藏失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
}