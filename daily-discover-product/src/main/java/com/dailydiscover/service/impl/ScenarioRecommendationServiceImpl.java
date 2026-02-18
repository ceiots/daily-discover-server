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
    public List<ScenarioRecommendation> getRecommendationsByScenario(String scenarioType) {
        return lambdaQuery().eq(ScenarioRecommendation::getScenarioType, scenarioType).orderByDesc(ScenarioRecommendation::getPriority).list();
    }
    
    @Override
    public List<ScenarioRecommendation> getActiveRecommendations() {
        return lambdaQuery().eq(ScenarioRecommendation::getStatus, "active").orderByDesc(ScenarioRecommendation::getPriority).list();
    }
    
    @Override
    public ScenarioRecommendation createRecommendation(String scenarioType, Long productId, Integer priority, String recommendationReason) {
        ScenarioRecommendation recommendation = new ScenarioRecommendation();
        recommendation.setScenarioType(scenarioType);
        recommendation.setProductId(productId);
        recommendation.setPriority(priority);
        recommendation.setRecommendationReason(recommendationReason);
        recommendation.setStatus("active");
        
        save(recommendation);
        return recommendation;
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
        List<ScenarioRecommendation> recommendations = getRecommendationsByScenario(scenarioType);
        return recommendations.stream()
                .limit(limit)
                .map(ScenarioRecommendation::getProductId)
                .collect(java.util.stream.Collectors.toList());
    }
}