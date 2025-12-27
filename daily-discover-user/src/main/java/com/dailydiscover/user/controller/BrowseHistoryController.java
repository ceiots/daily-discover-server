package com.dailydiscover.user.controller;

import com.dailydiscover.user.dto.BrowseHistoryResponse;
import com.dailydiscover.user.entity.BrowseHistory;
import com.dailydiscover.user.service.BrowseHistoryService;
import com.dailydiscover.common.util.LogTracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户浏览历史控制器
 */
@Slf4j
@RestController
@RequestMapping("/browse-history")
@RequiredArgsConstructor
public class BrowseHistoryController {

    private final BrowseHistoryService browseHistoryService;

    /**
     * 添加浏览记录
     */
    @PostMapping
    public ResponseEntity<BrowseHistoryResponse> addBrowseHistory(@RequestBody BrowseHistory browseHistory) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("BrowseHistoryController.addBrowseHistory", "开始添加浏览记录", browseHistory);
        
        try {
            BrowseHistoryResponse response = browseHistoryService.addBrowseHistory(browseHistory);
            
            LogTracer.traceMethod("BrowseHistoryController.addBrowseHistory", "浏览记录添加成功", response);
            LogTracer.tracePerformance("BrowseHistoryController.addBrowseHistory", startTime, System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LogTracer.traceException("BrowseHistoryController.addBrowseHistory", "添加浏览记录失败", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取用户的浏览记录
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BrowseHistoryResponse>> getBrowseHistoryByUserId(@PathVariable Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("BrowseHistoryController.getBrowseHistoryByUserId", "开始获取浏览记录", userId);
        
        try {
            List<BrowseHistoryResponse> history = browseHistoryService.getBrowseHistoryByUserId(userId);
            
            LogTracer.traceMethod("BrowseHistoryController.getBrowseHistoryByUserId", "获取浏览记录成功", history);
            LogTracer.tracePerformance("BrowseHistoryController.getBrowseHistoryByUserId", startTime, System.currentTimeMillis());
            
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            LogTracer.traceException("BrowseHistoryController.getBrowseHistoryByUserId", "获取浏览记录失败", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除浏览记录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrowseHistory(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("BrowseHistoryController.deleteBrowseHistory", "开始删除浏览记录", id);
        
        try {
            boolean result = browseHistoryService.deleteBrowseHistory(id);
            
            LogTracer.traceMethod("BrowseHistoryController.deleteBrowseHistory", "删除浏览记录完成", result);
            LogTracer.tracePerformance("BrowseHistoryController.deleteBrowseHistory", startTime, System.currentTimeMillis());
            
            return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            LogTracer.traceException("BrowseHistoryController.deleteBrowseHistory", "删除浏览记录失败", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 清空用户浏览记录
     */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> clearBrowseHistory(@PathVariable Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("BrowseHistoryController.clearBrowseHistory", "开始清空用户浏览记录", userId);
        
        try {
            boolean result = browseHistoryService.clearBrowseHistory(userId);
            
            LogTracer.traceMethod("BrowseHistoryController.clearBrowseHistory", "清空用户浏览记录完成", result);
            LogTracer.tracePerformance("BrowseHistoryController.clearBrowseHistory", startTime, System.currentTimeMillis());
            
            return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            LogTracer.traceException("BrowseHistoryController.clearBrowseHistory", "清空用户浏览记录失败", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 统计用户浏览历史数量
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> countBrowseHistory(@PathVariable Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("BrowseHistoryController.countBrowseHistory", "开始获取浏览记录数量", userId);
        
        try {
            long count = browseHistoryService.countBrowseHistory(userId);
            
            LogTracer.traceMethod("BrowseHistoryController.countBrowseHistory", "获取浏览记录数量完成", count);
            LogTracer.tracePerformance("BrowseHistoryController.countBrowseHistory", startTime, System.currentTimeMillis());
            
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            LogTracer.traceException("BrowseHistoryController.countBrowseHistory", "获取浏览记录数量失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
}