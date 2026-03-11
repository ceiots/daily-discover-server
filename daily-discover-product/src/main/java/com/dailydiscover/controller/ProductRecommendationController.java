package com.dailydiscover.controller;

import com.dailydiscover.common.logging.ApiLog;
import com.dailydiscover.model.ProductRecommendation;
import com.dailydiscover.dto.RelatedProductDTO;
import com.dailydiscover.dto.DailyDiscoveryResponseDTO;
import com.dailydiscover.dto.LifeScenarioResponseDTO;
import com.dailydiscover.dto.CommunityHotListResponseDTO;
import com.dailydiscover.dto.PersonalizedDiscoveryResponseDTO;
import com.dailydiscover.dto.GuidedOptionDTO;
import com.dailydiscover.dto.GuidedProductDTO;
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

    /**
     * 根据推荐ID获取推荐详情（管理后台用）
     */
    @GetMapping("/id/{id}")
    @ApiLog("根据ID获取商品推荐")
    public ResponseEntity<ProductRecommendation> getProductRecommendationById(@PathVariable Long id) {
        try {
            ProductRecommendation recommendation = productRecommendationService.getById(id);
            return recommendation != null ? ResponseEntity.ok(recommendation) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 商品详情页推荐接口（前端统一调用此接口）
     * 路径：/product-recommendations/product/{productId}/detail
     */
    @GetMapping("/product/{productId}/detail")
    @ApiLog("商品详情页推荐")
    public ResponseEntity<List<RelatedProductDTO>> getProductDetailRecommendations(
            @PathVariable Long productId,
            @RequestParam(required = false) Double currentPrice,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<RelatedProductDTO> recommendations = productRecommendationService.getProductDetailRecommendations(productId, currentPrice, limit);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/product/{productId}/complementary")
    @ApiLog("根据商品ID获取搭配商品推荐")
    public ResponseEntity<List<ProductRecommendation>> getComplementaryRecommendationsByProductId(@PathVariable Long productId) {
        try {
            List<ProductRecommendation> recommendations = productRecommendationService.getComplementaryRecommendations(productId);
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



    @GetMapping("/general")
    @ApiLog("获取通用推荐")
    public ResponseEntity<List<ProductRecommendation>> getGeneralRecommendations(@RequestParam(defaultValue = "20") int limit) {
        try {
            List<ProductRecommendation> recommendations = productRecommendationService.getGeneralRecommendations(limit);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/active")
    @ApiLog("获取活跃推荐")
    public ResponseEntity<List<ProductRecommendation>> getActiveRecommendations() {
        try {
            List<ProductRecommendation> recommendations = productRecommendationService.list();
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/recommended")
    @ApiLog("获取推荐商品")
    public ResponseEntity<List<ProductRecommendation>> getRecommendedProducts() {
        try {
            List<ProductRecommendation> recommendations = productRecommendationService.getGeneralRecommendations(10);
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

    // ==================== 首页推荐四模块 ====================

    @GetMapping("/daily-discovery")
    @ApiLog("获取今日发现推荐")
    public ResponseEntity<List<DailyDiscoveryResponseDTO>> getDailyDiscoveryRecommendations(@RequestParam(required = false) Long userId, @RequestParam(required = false) Integer limit, @RequestParam(required = false) Integer page) {
        try {
            List<DailyDiscoveryResponseDTO> recommendations = productRecommendationService.getDailyDiscoveryRecommendations(userId, limit, page);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/life-scenarios")
    @ApiLog("获取生活场景推荐")
    public ResponseEntity<List<LifeScenarioResponseDTO>> getLifeScenarioRecommendations(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String dateTime) {
        try {
            List<LifeScenarioResponseDTO> recommendations = productRecommendationService.getLifeScenarioRecommendations(userId, dateTime);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/community-hot-list")
    @ApiLog("获取社区热榜推荐")
    public ResponseEntity<List<CommunityHotListResponseDTO>> getCommunityHotList() {
        try {
            List<CommunityHotListResponseDTO> recommendations = productRecommendationService.getCommunityHotList();
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/personalized-discovery")
    @ApiLog("获取个性化发现流推荐")
    public ResponseEntity<List<PersonalizedDiscoveryResponseDTO>> getPersonalizedDiscoveryStream(@RequestParam Long userId) {
        try {
            List<PersonalizedDiscoveryResponseDTO> recommendations = productRecommendationService.getPersonalizedDiscoveryStream(userId);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== 引导推荐接口 ====================

    @GetMapping("/guided-options")
    @ApiLog("获取引导推荐选项")
    public ResponseEntity<List<GuidedOptionDTO>> getGuidedOptions() {
        try {
            List<GuidedOptionDTO> options = productRecommendationService.getGuidedOptions();
            return ResponseEntity.ok(options);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/guided-products")
    @ApiLog("获取引导推荐商品")
    public ResponseEntity<List<GuidedProductDTO>> getGuidedProducts(
            @RequestParam String sessionId,
            @RequestParam String optionId,
            @RequestParam(required = false) List<String> path,
            @RequestParam(defaultValue = "6") Integer limit) {
        try {
            List<String> effectivePath = path != null ? path : Collections.emptyList();
            
            List<GuidedProductDTO> products = productRecommendationService.getGuidedProducts(
                sessionId,
                optionId, 
                effectivePath, 
                limit
            );
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/guided-recommendation")
    @ApiLog("获取引导推荐（商品+下一级推荐词）")
    public ResponseEntity<Map<String, Object>> getGuidedRecommendation(
            @RequestBody Map<String, Object> request) {
        try {
            String optionId = (String) request.get("optionId");
            List<String> path = (List<String>) request.get("path");
            Integer round = request.get("round") != null ? (Integer) request.get("round") : 1;
            Integer limit = request.get("limit") != null ? (Integer) request.get("limit") : 6;
            
            // 获取商品
            List<GuidedProductDTO> products = productRecommendationService.getGuidedProducts(
                optionId, path, limit
            );
            
            // 基于商品生成下一级推荐词
            List<GuidedOptionDTO> nextOptions = productRecommendationService.generateNextOptions(
                products, path, round
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("products", products);
            response.put("nextOptions", nextOptions);
            response.put("currentPath", path);
            response.put("round", round);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}