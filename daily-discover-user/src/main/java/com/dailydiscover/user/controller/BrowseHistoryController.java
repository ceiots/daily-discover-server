package com.dailydiscover.user.controller;

import com.dailydiscover.common.result.Result;
import com.dailydiscover.user.dto.BrowseHistoryResponse;
import com.dailydiscover.user.entity.BrowseHistory;
import com.dailydiscover.user.service.BrowseHistoryService;
import com.dailydiscover.common.util.LogTracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<Result<BrowseHistoryResponse>> addBrowseHistory(@RequestBody BrowseHistory browseHistory) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("BrowseHistoryController.addBrowseHistory", "开始添加浏览记录", browseHistory);
        
        try {
            BrowseHistoryResponse response = browseHistoryService.addBrowseHistory(browseHistory);
            Result<BrowseHistoryResponse> result = Result.success("浏览记录添加成功", response);
            
            LogTracer.traceMethod("BrowseHistoryController.addBrowseHistory", "浏览记录添加成功", result);
            LogTracer.tracePerformance("BrowseHistoryController.addBrowseHistory", startTime, System.currentTimeMillis());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            LogTracer.traceException("BrowseHistoryController.addBrowseHistory", "添加浏览记录失败", e);
            Result<BrowseHistoryResponse> result = Result.failure(e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 获取用户的浏览记录
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Result<List<BrowseHistoryResponse>>> getBrowseHistoryByUserId(@PathVariable Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("BrowseHistoryController.getBrowseHistoryByUserId", "开始获取浏览记录", userId);
        
        try {
            List<BrowseHistoryResponse> history = browseHistoryService.getBrowseHistoryByUserId(userId);
            Result<List<BrowseHistoryResponse>> result = Result.success(history);
            
            LogTracer.traceMethod("BrowseHistoryController.getBrowseHistoryByUserId", "获取浏览记录成功", result);
            LogTracer.tracePerformance("BrowseHistoryController.getBrowseHistoryByUserId", startTime, System.currentTimeMillis());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            LogTracer.traceException("BrowseHistoryController.getBrowseHistoryByUserId", "获取浏览记录失败", e);
            Result<List<BrowseHistoryResponse>> result = Result.failure(e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 删除浏览记录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Boolean>> deleteBrowseHistory(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("BrowseHistoryController.deleteBrowseHistory", "开始删除浏览记录", id);
        
        try {
            boolean result = browseHistoryService.deleteBrowseHistory(id);
            Result<Boolean> response = result ? Result.success("浏览记录删除成功", true) : Result.failure("浏览记录删除失败");
            
            LogTracer.traceMethod("BrowseHistoryController.deleteBrowseHistory", "删除浏览记录完成", response);
            LogTracer.tracePerformance("BrowseHistoryController.deleteBrowseHistory", startTime, System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LogTracer.traceException("BrowseHistoryController.deleteBrowseHistory", "删除浏览记录失败", e);
            Result<Boolean> response = Result.failure(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 清空用户浏览记录
     */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Result<Boolean>> clearBrowseHistory(@PathVariable Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("BrowseHistoryController.clearBrowseHistory", "开始清空用户浏览记录", userId);
        
        try {
            boolean result = browseHistoryService.clearBrowseHistory(userId);
            Result<Boolean> response = result ? Result.success("浏览记录清空成功", true) : Result.failure("浏览记录清空失败");
            
            LogTracer.traceMethod("BrowseHistoryController.clearBrowseHistory", "清空用户浏览记录完成", response);
            LogTracer.tracePerformance("BrowseHistoryController.clearBrowseHistory", startTime, System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LogTracer.traceException("BrowseHistoryController.clearBrowseHistory", "清空用户浏览记录失败", e);
            Result<Boolean> response = Result.failure(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 统计用户浏览历史数量
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Result<Long>> countBrowseHistory(@PathVariable Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("BrowseHistoryController.countBrowseHistory", "开始获取浏览记录数量", userId);
        
        try {
            long count = browseHistoryService.countBrowseHistory(userId);
            Result<Long> result = Result.success(count);
            
            LogTracer.traceMethod("BrowseHistoryController.countBrowseHistory", "获取浏览记录数量完成", result);
            LogTracer.tracePerformance("BrowseHistoryController.countBrowseHistory", startTime, System.currentTimeMillis());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            LogTracer.traceException("BrowseHistoryController.countBrowseHistory", "获取浏览记录数量失败", e);
            Result<Long> result = Result.failure(e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}