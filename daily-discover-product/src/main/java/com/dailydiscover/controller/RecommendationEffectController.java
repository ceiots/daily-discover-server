package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.RecommendationEffect;
import com.dailydiscover.service.RecommendationEffectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendation-effects")
@RequiredArgsConstructor
public class RecommendationEffectController {

    private final RecommendationEffectService recommendationEffectService;

    @GetMapping
    @ApiLog("获取所有推荐效果追踪记录")
    public ResponseEntity<List<RecommendationEffect>> getAllRecommendationEffects() {
        try {
            List<RecommendationEffect> effects = recommendationEffectService.list();
            return ResponseEntity.ok(effects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取推荐效果追踪记录")
    public ResponseEntity<RecommendationEffect> getRecommendationEffectById(@PathVariable Long id) {
        try {
            RecommendationEffect effect = recommendationEffectService.getById(id);
            return effect != null ? ResponseEntity.ok(effect) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/recommendation/{recommendationId}")
    @ApiLog("根据推荐ID获取效果数据")
    public ResponseEntity<List<RecommendationEffect>> getRecommendationEffectsByRecommendationId(@PathVariable Long recommendationId) {
        try {
            List<RecommendationEffect> effects = recommendationEffectService.getEffectsByRecommendationId(recommendationId);
            return ResponseEntity.ok(effects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    @ApiLog("根据用户ID获取推荐效果")
    public ResponseEntity<List<RecommendationEffect>> getRecommendationEffectsByUserId(@PathVariable Long userId) {
        try {
            List<RecommendationEffect> effects = recommendationEffectService.getEffectsByUserId(userId);
            return ResponseEntity.ok(effects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{recommendationId}/impression")
    @ApiLog("记录推荐展示")
    public ResponseEntity<Void> recordImpression(@PathVariable Long recommendationId, @RequestParam Long userId) {
        try {
            boolean success = recommendationEffectService.recordImpression(recommendationId, userId);
            return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{recommendationId}/click")
    @ApiLog("记录推荐点击")
    public ResponseEntity<Void> recordClick(@PathVariable Long recommendationId, @RequestParam Long userId) {
        try {
            boolean success = recommendationEffectService.recordClick(recommendationId, userId);
            return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{recommendationId}/conversion")
    @ApiLog("记录推荐转化")
    public ResponseEntity<Void> recordConversion(@PathVariable Long recommendationId, @RequestParam Long userId) {
        try {
            boolean success = recommendationEffectService.recordConversion(recommendationId, userId);
            return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建推荐效果追踪记录")
    public ResponseEntity<RecommendationEffect> createRecommendationEffect(@RequestBody RecommendationEffect effect) {
        try {
            boolean success = recommendationEffectService.save(effect);
            return success ? ResponseEntity.ok(effect) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新推荐效果追踪记录")
    public ResponseEntity<RecommendationEffect> updateRecommendationEffect(@PathVariable Long id, @RequestBody RecommendationEffect effect) {
        try {
            effect.setId(id);
            boolean success = recommendationEffectService.updateById(effect);
            return success ? ResponseEntity.ok(effect) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除推荐效果追踪记录")
    public ResponseEntity<Void> deleteRecommendationEffect(@PathVariable Long id) {
        try {
            boolean success = recommendationEffectService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}