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
        LogTracer.traceBusinessMethod(browseHistory, null);
        
        try {
            BrowseHistoryResponse response = browseHistoryService.addBrowseHistory(browseHistory);
            
            LogTracer.traceBusinessMethod(browseHistory, response);
            LogTracer.traceBusinessPerformance(startTime);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取用户的浏览记录
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BrowseHistoryResponse>> getBrowseHistoryByUserId(@PathVariable Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(userId, null);
        
        try {
            List<BrowseHistoryResponse> history = browseHistoryService.getBrowseHistoryByUserId(userId);
            
            LogTracer.traceBusinessMethod(userId, history);
            LogTracer.traceBusinessPerformance(startTime);
            
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除浏览记录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrowseHistory(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(id, null);
        
        try {
            boolean result = browseHistoryService.deleteBrowseHistory(id);
            
            LogTracer.traceBusinessMethod(id, result);
            LogTracer.traceBusinessPerformance(startTime);
            
            return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 清空用户浏览记录
     */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> clearBrowseHistory(@PathVariable Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(userId, null);
        
        try {
            boolean result = browseHistoryService.clearBrowseHistory(userId);
            
            LogTracer.traceBusinessMethod(userId, result);
            LogTracer.traceBusinessPerformance(startTime);
            
            return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 统计用户浏览历史数量
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> countBrowseHistory(@PathVariable Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(userId, null);
        
        try {
            long count = browseHistoryService.countBrowseHistory(userId);
            
            LogTracer.traceBusinessMethod(userId, count);
            LogTracer.traceBusinessPerformance(startTime);
            
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            return ResponseEntity.badRequest().build();
        }
    }
}