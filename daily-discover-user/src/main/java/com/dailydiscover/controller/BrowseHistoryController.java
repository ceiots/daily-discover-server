package com.dailydiscover.controller;

import com.dailydiscover.dto.BrowseHistoryResponse;
import com.dailydiscover.entity.BrowseHistory;
import com.dailydiscover.service.BrowseHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户浏览历史控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/browse-history")
@RequiredArgsConstructor
public class BrowseHistoryController {

    private final BrowseHistoryService browseHistoryService;

    /**
     * 添加浏览记录
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addBrowseHistory(@RequestBody BrowseHistory browseHistory) {
        try {
            BrowseHistoryResponse response = browseHistoryService.addBrowseHistory(browseHistory);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "浏览记录添加成功");
            result.put("data", response);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("添加浏览记录失败: {}", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 获取用户的浏览历史
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getBrowseHistoryByUserId(@PathVariable Long userId) {
        try {
            List<BrowseHistoryResponse> browseHistories = browseHistoryService.getBrowseHistoryByUserId(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", browseHistories);
            result.put("total", browseHistories.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取浏览历史失败: {}", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 删除浏览记录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBrowseHistory(@PathVariable Long id) {
        try {
            boolean result = browseHistoryService.deleteBrowseHistory(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result);
            response.put("message", result ? "浏览记录删除成功" : "浏览记录删除失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("删除浏览记录失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 清空用户的浏览历史
     */
    @DeleteMapping("/user/{userId}/clear")
    public ResponseEntity<Map<String, Object>> clearBrowseHistory(@PathVariable Long userId) {
        try {
            boolean result = browseHistoryService.clearBrowseHistory(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result);
            response.put("message", result ? "浏览历史清空成功" : "浏览历史清空失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("清空浏览历史失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 统计用户浏览历史数量
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Object>> countBrowseHistory(@PathVariable Long userId) {
        try {
            int count = browseHistoryService.countBrowseHistory(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("统计浏览历史数量失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}