package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.PromotionActivity;
import com.dailydiscover.service.PromotionActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/promotion-activities")
@RequiredArgsConstructor
public class PromotionActivityController {

    private final PromotionActivityService promotionActivityService;

    @GetMapping
    @ApiLog("获取所有促销活动")
    public ResponseEntity<List<PromotionActivity>> getAllPromotionActivities() {
        try {
            List<PromotionActivity> activities = promotionActivityService.list();
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取促销活动")
    public ResponseEntity<PromotionActivity> getPromotionActivityById(@PathVariable Long id) {
        try {
            PromotionActivity activity = promotionActivityService.getById(id);
            return activity != null ? ResponseEntity.ok(activity) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/active")
    @ApiLog("获取当前有效的促销活动")
    public ResponseEntity<List<PromotionActivity>> getActivePromotionActivities() {
        try {
            List<PromotionActivity> activities = promotionActivityService.findActiveActivities();
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/type/{activityType}")
    @ApiLog("根据活动类型获取促销活动")
    public ResponseEntity<List<PromotionActivity>> getPromotionActivitiesByType(@PathVariable String activityType) {
        try {
            List<PromotionActivity> activities = promotionActivityService.findByActivityType(activityType);
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/status/{status}")
    @ApiLog("根据状态获取促销活动")
    public ResponseEntity<List<PromotionActivity>> getPromotionActivitiesByStatus(@PathVariable String status) {
        try {
            List<PromotionActivity> activities = promotionActivityService.findByStatus(status);
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/product/{productId}")
    @ApiLog("根据商品ID获取促销活动")
    public ResponseEntity<List<PromotionActivity>> getPromotionActivitiesByProductId(@PathVariable Long productId) {
        try {
            List<PromotionActivity> activities = promotionActivityService.findByProductId(productId);
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建促销活动")
    public ResponseEntity<PromotionActivity> createPromotionActivity(@RequestBody PromotionActivity activity) {
        try {
            boolean success = promotionActivityService.save(activity);
            return success ? ResponseEntity.ok(activity) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新促销活动")
    public ResponseEntity<PromotionActivity> updatePromotionActivity(@PathVariable Long id, @RequestBody PromotionActivity activity) {
        try {
            activity.setId(id);
            boolean success = promotionActivityService.updateById(activity);
            return success ? ResponseEntity.ok(activity) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除促销活动")
    public ResponseEntity<Void> deletePromotionActivity(@PathVariable Long id) {
        try {
            boolean success = promotionActivityService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}