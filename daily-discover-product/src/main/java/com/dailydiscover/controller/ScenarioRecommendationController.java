package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.ScenarioRecommendation;
import com.dailydiscover.service.ScenarioRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scenario-recommendations")
@RequiredArgsConstructor
public class ScenarioRecommendationController {

    private final ScenarioRecommendationService scenarioRecommendationService;

    @GetMapping
    @ApiLog("获取所有场景推荐")
    public ResponseEntity<List<ScenarioRecommendation>> getAllScenarioRecommendations() {
        try {
            List<ScenarioRecommendation> recommendations = scenarioRecommendationService.list();
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取场景推荐")
    public ResponseEntity<ScenarioRecommendation> getScenarioRecommendationById(@PathVariable Long id) {
        try {
            ScenarioRecommendation recommendation = scenarioRecommendationService.getById(id);
            return recommendation != null ? ResponseEntity.ok(recommendation) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/scenario/{scenarioType}")
    @ApiLog("根据场景类型获取推荐")
    public ResponseEntity<List<ScenarioRecommendation>> getScenarioRecommendationsByScenarioType(@PathVariable String scenarioType) {
        try {
            List<ScenarioRecommendation> recommendations = scenarioRecommendationService.getByScenarioType(scenarioType);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    @ApiLog("根据用户ID获取场景推荐")
    public ResponseEntity<List<ScenarioRecommendation>> getScenarioRecommendationsByUserId(@PathVariable Long userId) {
        try {
            List<ScenarioRecommendation> recommendations = scenarioRecommendationService.getByUserId(userId);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/active")
    @ApiLog("获取活跃场景推荐")
    public ResponseEntity<List<ScenarioRecommendation>> getActiveScenarioRecommendations() {
        try {
            List<ScenarioRecommendation> recommendations = scenarioRecommendationService.getActiveRecommendations();
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建场景推荐")
    public ResponseEntity<ScenarioRecommendation> createScenarioRecommendation(@RequestBody ScenarioRecommendation recommendation) {
        try {
            boolean success = scenarioRecommendationService.save(recommendation);
            return success ? ResponseEntity.ok(recommendation) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新场景推荐")
    public ResponseEntity<ScenarioRecommendation> updateScenarioRecommendation(@PathVariable Long id, @RequestBody ScenarioRecommendation recommendation) {
        try {
            recommendation.setId(id);
            boolean success = scenarioRecommendationService.updateById(recommendation);
            return success ? ResponseEntity.ok(recommendation) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除场景推荐")
    public ResponseEntity<Void> deleteScenarioRecommendation(@PathVariable Long id) {
        try {
            boolean success = scenarioRecommendationService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}