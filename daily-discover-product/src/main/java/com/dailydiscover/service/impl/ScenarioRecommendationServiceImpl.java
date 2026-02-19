package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ScenarioRecommendationMapper;
import com.dailydiscover.model.ScenarioRecommendation;
import com.dailydiscover.service.ScenarioRecommendationService;
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
        return lambdaQuery().eq(ScenarioRecommendation::getProductId, productId).orderByDesc(ScenarioRecommendation::getSuccessRate).list();
    }
    
    @Override
    public ScenarioRecommendation createScenarioRecommendation(String scenarioType, String timeSlot, 
                                                               java.util.List<Long> recommendedProducts, 
                                                               String scenarioStory) {
        ScenarioRecommendation recommendation = new ScenarioRecommendation();
        recommendation.setScenarioType(scenarioType);
        recommendation.setTimeSlot(timeSlot);
        recommendation.setScenarioStory(scenarioStory);
        recommendation.setStatus("active");
        
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
        return scenarioRecommendationMapper.findActiveRecommendations();
    }
    
    @Override
    public boolean updateRecommendationPriority(Long recommendationId, Integer priority) {
        ScenarioRecommendation recommendation = getById(recommendationId);
        if (recommendation != null) {
            recommendation.setPriority(priority);
            return updateById(recommendation);
        }
        return false;
    }
    
    @Override
    public boolean toggleRecommendationStatus(Long recommendationId, String status) {
        ScenarioRecommendation recommendation = getById(recommendationId);
        if (recommendation != null) {
            recommendation.setStatus(status);
            return updateById(recommendation);
        }
        return false;
    }
    
    @Override
    public List<Long> getRecommendedProductIdsByScenario(String scenarioType, Integer limit) {
        List<ScenarioRecommendation> recommendations = getRecommendationsByScenarioType(scenarioType);
        return recommendations.stream()
                .limit(limit)
                .map(ScenarioRecommendation::getProductId)
                .collect(java.util.stream.Collectors.toList());
    }
}