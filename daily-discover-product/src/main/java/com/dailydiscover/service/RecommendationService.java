package com.dailydiscover.service;

import com.dailydiscover.model.Product;
import com.dailydiscover.model.ProductRecommendation;
import com.dailydiscover.model.ScenarioRecommendation;
import com.dailydiscover.model.UserInterestProfile;
import java.util.List;
import java.util.Map;

public interface RecommendationService {
    
    // 商品推荐
    List<Product> getProductRecommendations(Long productId, String recommendationType);
    List<Product> getPersonalizedRecommendations(Long userId);
    List<Product> getDailyDiscoverProducts();
    List<Product> getTrendingProducts();
    List<Product> getNewArrivalProducts();
    List<Product> getLimitedTimeProducts();
    
    // 推荐算法管理
    List<ProductRecommendation> getRecommendationsByType(String recommendationType);
    void saveProductRecommendation(ProductRecommendation recommendation);
    void updateRecommendationScore(Long recommendationId, Double score);
    void expireRecommendation(Long recommendationId);
    
    // 场景推荐
    List<ScenarioRecommendation> getScenarioRecommendations(String scenarioType, String timeSlot);
    List<Product> getPersonalizedScenarioRecommendations(Long userId, String scenarioType);
    void saveScenarioRecommendation(ScenarioRecommendation recommendation);
    void updateScenarioRecommendation(Long scenarioId, String recommendedProducts);
    
    // 用户兴趣画像
    UserInterestProfile getUserInterestProfile(Long userId);
    void saveUserInterestProfile(UserInterestProfile profile);
    void updateUserInterestTags(Long userId, Map<String, Double> interestTags);
    void updateUserBehaviorPatterns(Long userId, Map<String, Object> behaviorPatterns);
    
    // 推荐效果追踪
    void recordRecommendationImpression(Long recommendationId, Long userId);
    void recordRecommendationClick(Long recommendationId, Long userId);
    void recordRecommendationConversion(Long recommendationId, Long userId);
    
    // 推荐算法分析
    Map<String, Object> getRecommendationEffectiveness(String recommendationType);
    Map<String, Object> getUserEngagementStats(Long userId);
    List<Map<String, Object>> getTopRecommendationTypes(int limit);
    
    // 实时推荐计算
    List<Product> calculateRealTimeRecommendations(Long userId, Map<String, Object> context);
    List<Product> calculateSimilarProducts(Long productId, int limit);
    List<Product> calculateComplementaryProducts(Long productId, int limit);
    
    // 推荐系统配置
    Map<String, Object> getRecommendationConfig();
    void updateRecommendationConfig(Map<String, Object> config);
    void refreshRecommendationCache();
}