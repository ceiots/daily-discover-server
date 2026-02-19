package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ScenarioRecommendationMapper;
import com.dailydiscover.model.ScenarioRecommendation;
import com.dailydiscover.service.ScenarioRecommendationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ScenarioRecommendationServiceImpl extends ServiceImpl<ScenarioRecommendationMapper, ScenarioRecommendation> implements ScenarioRecommendationService {
    
    @Autowired
    private ScenarioRecommendationMapper scenarioRecommendationMapper;
    
    @Override
    public List<ScenarioRecommendation> getRecommendationsByScenarioType(String scenarioType) {
        return scenarioRecommendationMapper.findByScenarioType(scenarioType);
    }
    
    @Override
    public List<ScenarioRecommendation> getPersonalizedScenarioRecommendations(Long userId) {
        return scenarioRecommendationMapper.findByUserId(userId);
    }
    
    @Override
    public List<ScenarioRecommendation> getRecommendationsByProductId(Long productId) {
        // 由于表结构中没有 product_id 字段，此方法无法实现
        log.warn("表结构中没有 product_id 字段，无法根据商品ID查询场景推荐");
        return java.util.Collections.emptyList();
    }
    
    @Override
    public ScenarioRecommendation createScenarioRecommendation(String scenarioType, String timeSlot, 
                                                               java.util.List<Long> recommendedProducts, 
                                                               String scenarioStory) {
        ScenarioRecommendation recommendation = new ScenarioRecommendation();
        recommendation.setScenarioType(scenarioType);
        recommendation.setTimeSlot(timeSlot);
        recommendation.setScenarioStory(scenarioStory);
        
        // 将推荐商品列表转换为JSON字符串
        if (recommendedProducts != null && !recommendedProducts.isEmpty()) {
            try {
                recommendation.setRecommendedProducts(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(recommendedProducts));
            } catch (JsonProcessingException e) {
                log.error("JSON序列化失败: {}", e.getMessage());
                // 如果序列化失败，可以设置为空字符串或记录错误
                recommendation.setRecommendedProducts("");
            }
        }
        
        save(recommendation);
        return recommendation;
    }
    
    @Override
    public List<ScenarioRecommendation> getByScenarioType(String scenarioType) {
        return getRecommendationsByScenarioType(scenarioType);
    }
    
    @Override
    public List<ScenarioRecommendation> getByUserId(Long userId) {
        return getPersonalizedScenarioRecommendations(userId);
    }
    
    @Override
    public List<ScenarioRecommendation> getActiveRecommendations() {
        // 由于表结构中没有 status 字段，返回所有推荐
        return list();
    }
}