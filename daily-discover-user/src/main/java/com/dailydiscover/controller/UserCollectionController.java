package com.dailydiscover.controller;

import com.dailydiscover.dto.UserCollectionResponse;
import com.dailydiscover.entity.UserCollection;
import com.dailydiscover.service.UserCollectionService;
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
@RequestMapping("/api/collections")
@RequiredArgsConstructor
public class UserCollectionController {

    private final UserCollectionService userCollectionService;

    /**
     * 添加收藏
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addCollection(@RequestBody UserCollection userCollection) {
        try {
            UserCollectionResponse response = userCollectionService.addCollection(userCollection);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "收藏添加成功");
            result.put("data", response);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("添加收藏失败: {}", e.getMessage());
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
        try {
            List<UserCollectionResponse> collections = userCollectionService.getCollectionsByUserId(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", collections);
            result.put("total", collections.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取收藏列表失败: {}", e.getMessage());
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
        try {
            boolean result = userCollectionService.deleteCollection(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result);
            response.put("message", result ? "收藏删除成功" : "收藏删除失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("删除收藏失败: {}", e.getMessage());
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
        try {
            boolean isCollected = userCollectionService.isItemCollected(userId, itemType, itemId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("isCollected", isCollected);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("检查收藏状态失败: {}", e.getMessage());
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
        try {
            int count = userCollectionService.countCollections(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("统计收藏数量失败: {}", e.getMessage());
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
        try {
            boolean result = userCollectionService.clearCollections(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result);
            response.put("message", result ? "收藏清空成功" : "收藏清空失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("清空收藏失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}