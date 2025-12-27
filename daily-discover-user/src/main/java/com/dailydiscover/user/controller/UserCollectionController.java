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
        LogTracer.traceBusinessMethod(userCollection);
        
        try {
            UserCollectionResponse response = userCollectionService.addCollection(userCollection);
            
            LogTracer.traceBusinessMethod(response);
            LogTracer.traceBusinessPerformance(startTime);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取用户的收藏列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserCollectionResponse>> getCollectionsByUserId(@PathVariable Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(userId);
        
        try {
            List<UserCollectionResponse> collections = userCollectionService.getCollectionsByUserId(userId);
            
            LogTracer.traceBusinessMethod(collections);
            LogTracer.traceBusinessPerformance(startTime);
            
            return ResponseEntity.ok(collections);
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除收藏
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollection(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(id);
        
        try {
            boolean result = userCollectionService.deleteCollection(id);
            
            LogTracer.traceBusinessMethod(result);
            LogTracer.traceBusinessPerformance(startTime);
            
            return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
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
        LogTracer.traceBusinessMethod(Map.of("userId", userId, "itemType", itemType, "itemId", itemId));
        
        try {
            boolean isCollected = userCollectionService.isItemCollected(userId, itemType, itemId);
            
            LogTracer.traceBusinessMethod(isCollected);
            LogTracer.traceBusinessPerformance(startTime);
            
            return ResponseEntity.ok(isCollected);
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 统计用户收藏数量
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Integer> countCollections(@PathVariable Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(userId, null);
        
        try {
            int count = userCollectionService.countCollections(userId);
            
            LogTracer.traceBusinessMethod(userId, count);
            LogTracer.traceBusinessPerformance(startTime);
            
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 清空用户的收藏
     */
    @DeleteMapping("/user/{userId}/clear")
    public ResponseEntity<Void> clearCollections(@PathVariable Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(userId, null);
        
        try {
            boolean result = userCollectionService.clearCollections(userId);
            
            LogTracer.traceBusinessMethod(userId, result);
            LogTracer.traceBusinessPerformance(startTime);
            
            return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            LogTracer.traceException("UserCollectionController.clearCollections", "清空用户收藏失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
}