package com.example.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import com.example.dto.BrowsingDurationDTO;
import com.example.common.api.CommonResult;
import com.example.common.Result;
import com.example.model.UserBrowsingHistory;
import com.example.service.UserBrowsingHistoryService;
import com.example.util.UserIdExtractor;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户浏览历史控制器
 */
@Slf4j
@RestController
@RequestMapping("/browsing-history")
public class UserBrowsingHistoryController {

    @Autowired
    private UserBrowsingHistoryService userBrowsingHistoryService;
    
    @Autowired
    private UserIdExtractor userIdExtractor;
    
    /**
     * 记录用户浏览商品
     */
    @PostMapping("/record")
    public CommonResult<Void> recordBrowsing(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestBody UserBrowsingHistory browsingHistory) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            browsingHistory.setUserId(userId);
            browsingHistory.setBrowseTime(new Date());
            
            userBrowsingHistoryService.recordBrowsing(browsingHistory);
            return CommonResult.success(null);
        } catch (Exception e) {
            log.error("记录浏览历史失败", e);
            return CommonResult.failed("记录浏览历史失败：" + e.getMessage());
        }
    }

    /**
     * 记录用户浏览时长
     */
    @PostMapping("/duration")
    public ResponseEntity<Result<Void>> recordBrowsingDuration(@RequestBody BrowsingDurationDTO dto) {
        try {
            // 可以记录浏览时长，进一步分析用户兴趣
            // 实现略...
            return ResponseEntity.ok(Result.success());
        } catch (Exception e) {
            log.error("记录浏览时长失败", e);
            return ResponseEntity.ok(Result.failed("记录浏览时长失败：" + e.getMessage()));
        }
    }
    
   
} 