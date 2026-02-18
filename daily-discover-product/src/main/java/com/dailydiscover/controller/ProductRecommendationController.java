package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.ProductRecommendation;
import com.dailydiscover.service.ProductRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product-recommendations")
@RequiredArgsConstructor
public class ProductRecommendationController {

    private final ProductRecommendationService productRecommendationService;

    @GetMapping
    @ApiLog("获取所有商品推荐")
    public ResponseEntity<List<ProductRecommendation>> getAllProductRecommendations() {
        try {
            List<ProductRecommendation> recommendations = productRecommendationService.list();
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取商品推荐")
    public ResponseEntity<ProductRecommendation> getProductRecommendationById(@PathVariable Long id) {
        try {
            ProductRecommendation recommendation = productRecommendationService.getById(id);
            return recommendation != null ? ResponseEntity.ok(recommendation) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    @ApiLog("根据用户ID获取个性化推荐")
    public ResponseEntity<List<ProductRecommendation>> getProductRecommendationsByUserId(@PathVariable Long userId) {
        try {
            List<ProductRecommendation> recommendations = productRecommendationService.getPersonalizedRecommendations(userId);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/product/{productId}")
    @ApiLog("根据商品ID获取推荐")
    public ResponseEntity<List<ProductRecommendation>> getProductRecommendationsByProductId(@PathVariable Long productId) {
        try {
            List<ProductRecommendation> recommendations = productRecommendationService.getRecommendationsByProductId(productId);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/type/{recommendationType}")
    @ApiLog("根据推荐类型获取推荐")
    public ResponseEntity<List<ProductRecommendation>> getProductRecommendationsByType(@PathVariable String recommendationType) {
        try {
            List<ProductRecommendation> recommendations = productRecommendationService.getRecommendationsByType(recommendationType);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/active")
    @ApiLog("获取活跃推荐")
    public ResponseEntity<List<ProductRecommendation>> getActiveRecommendations() {
        try {
            List<ProductRecommendation> recommendations = productRecommendationService.getActiveRecommendations();
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建商品推荐")
    public ResponseEntity<ProductRecommendation> createProductRecommendation(@RequestBody ProductRecommendation recommendation) {
        try {
            boolean success = productRecommendationService.save(recommendation);
            return success ? ResponseEntity.ok(recommendation) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新商品推荐")
    public ResponseEntity<ProductRecommendation> updateProductRecommendation(@PathVariable Long id, @RequestBody ProductRecommendation recommendation) {
        try {
            recommendation.setId(id);
            boolean success = productRecommendationService.updateById(recommendation);
            return success ? ResponseEntity.ok(recommendation) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除商品推荐")
    public ResponseEntity<Void> deleteProductRecommendation(@PathVariable Long id) {
        try {
            boolean success = productRecommendationService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}