package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.UserInterestProfile;
import com.dailydiscover.service.UserInterestProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-interest-profiles")
@RequiredArgsConstructor
public class UserInterestProfileController {

    private final UserInterestProfileService userInterestProfileService;

    @GetMapping
    @ApiLog("获取所有用户兴趣画像")
    public ResponseEntity<List<UserInterestProfile>> getAllUserInterestProfiles() {
        try {
            List<UserInterestProfile> profiles = userInterestProfileService.list();
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取用户兴趣画像")
    public ResponseEntity<UserInterestProfile> getUserInterestProfileById(@PathVariable Long id) {
        try {
            UserInterestProfile profile = userInterestProfileService.getById(id);
            return profile != null ? ResponseEntity.ok(profile) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    @ApiLog("根据用户ID查询兴趣画像")
    public ResponseEntity<UserInterestProfile> getUserInterestProfileByUserId(@PathVariable Long userId) {
        try {
            UserInterestProfile profile = userInterestProfileService.getByUserId(userId);
            return profile != null ? ResponseEntity.ok(profile) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建用户兴趣画像")
    public ResponseEntity<UserInterestProfile> createUserInterestProfile(@RequestBody UserInterestProfile profile) {
        try {
            boolean success = userInterestProfileService.save(profile);
            return success ? ResponseEntity.ok(profile) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/user/{userId}")
    @ApiLog("更新用户兴趣画像")
    public ResponseEntity<UserInterestProfile> updateUserInterestProfile(@PathVariable Long userId, @RequestBody UserInterestProfile profile) {
        try {
            profile.setUserId(userId);
            boolean success = userInterestProfileService.updateById(profile);
            return success ? ResponseEntity.ok(profile) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/user/{userId}/interest-tags")
    @ApiLog("更新用户兴趣标签")
    public ResponseEntity<Void> updateUserInterestTags(@PathVariable Long userId, @RequestParam String interestTags) {
        try {
            boolean success = userInterestProfileService.updateInterestTags(userId, interestTags);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/user/{userId}/behavior-patterns")
    @ApiLog("更新用户行为模式")
    public ResponseEntity<Void> updateUserBehaviorPatterns(@PathVariable Long userId, @RequestParam String behaviorPatterns) {
        try {
            boolean success = userInterestProfileService.updateBehaviorPatterns(userId, behaviorPatterns);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除用户兴趣画像")
    public ResponseEntity<Void> deleteUserInterestProfile(@PathVariable Long id) {
        try {
            boolean success = userInterestProfileService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}