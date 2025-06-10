package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.common.result.Result;
import com.example.dao.UserPreferenceDao;
import com.example.model.UserPreference;
import com.example.util.UserIdExtractor;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户偏好控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserPreferenceController {

    @Autowired
    private UserPreferenceDao userPreferenceDao;
    
    @Autowired
    private UserIdExtractor userIdExtractor;
    
    /**
     * 设置用户偏好
     */
    @PostMapping("/preference/set")
    public ResponseEntity<Result<Void>> setUserPreferences(@RequestBody List<UserPreference> preferences) {
        try {
            for (UserPreference preference : preferences) {
                preference.setCreatedAt(new Date());
                preference.setUpdatedAt(new Date());
                userPreferenceDao.insert(preference);
            }
            return ResponseEntity.ok(Result.success());
        } catch (Exception e) {
            log.error("设置用户偏好失败", e);
            return ResponseEntity.ok(Result.failed("设置用户偏好失败：" + e.getMessage()));
        }
    }
    
    /**
     * 保存用户类别偏好
     */
    @PostMapping("/preferences")
    public CommonResult<Void> saveUserCategoryPreferences(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestBody Map<String, List<Long>> requestBody) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            List<Long> categoryIds = requestBody.get("categoryIds");
            if (categoryIds == null || categoryIds.isEmpty()) {
                return CommonResult.failed("类别ID列表不能为空");
            }
            
            // 先删除用户现有的偏好
            userPreferenceDao.deleteByUserId(userId);
            
            // 保存新的偏好
            List<UserPreference> preferences = new ArrayList<>();
            for (Long categoryId : categoryIds) {
                UserPreference preference = new UserPreference();
                preference.setUserId(userId);
                preference.setCategoryId(categoryId);
                preference.setPreferenceLevel(5); // 默认最高偏好等级
                preference.setCreatedAt(new Date());
                preference.setUpdatedAt(new Date());
                preferences.add(preference);
            }
            
            for (UserPreference preference : preferences) {
                userPreferenceDao.insert(preference);
            }
            
            return CommonResult.success(null);
        } catch (Exception e) {
            log.error("保存用户类别偏好失败", e);
            return CommonResult.failed("保存用户类别偏好失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户偏好
     */
    @GetMapping("/preferences")
    public CommonResult<List<UserPreference>> getUserPreferences(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            List<UserPreference> preferences = userPreferenceDao.findByUserId(userId);
            return CommonResult.success(preferences);
        } catch (Exception e) {
            log.error("获取用户偏好失败", e);
            return CommonResult.failed("获取用户偏好失败：" + e.getMessage());
        }
    }
}
