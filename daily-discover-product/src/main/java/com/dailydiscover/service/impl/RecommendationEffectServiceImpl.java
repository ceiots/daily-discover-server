package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.RecommendationEffectMapper;
import com.dailydiscover.model.RecommendationEffect;
import com.dailydiscover.service.RecommendationEffectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RecommendationEffectServiceImpl extends ServiceImpl<RecommendationEffectMapper, RecommendationEffect> implements RecommendationEffectService {
    
    @Autowired
    private RecommendationEffectMapper recommendationEffectMapper;
    
    @Override
    public List<RecommendationEffect> getEffectsByUserId(Long userId) {
        return lambdaQuery().eq(RecommendationEffect::getUserId, userId).orderByDesc(RecommendationEffect::getInteractionTime).list();
    }
    
    @Override
    public List<RecommendationEffect> getEffectsByRecommendationId(Long recommendationId) {
        return lambdaQuery().eq(RecommendationEffect::getRecommendationId, recommendationId).orderByDesc(RecommendationEffect::getInteractionTime).list();
    }
    
    @Override
    public RecommendationEffect recordInteraction(Long userId, Long recommendationId, String interactionType, String interactionResult) {
        RecommendationEffect effect = new RecommendationEffect();
        effect.setUserId(userId);
        effect.setRecommendationId(recommendationId);
        effect.setInteractionType(interactionType);
        effect.setInteractionResult(interactionResult);
        effect.setInteractionTime(new java.util.Date());
        
        save(effect);
        return effect;
    }
    
    @Override
    public boolean updateInteractionResult(Long effectId, String interactionResult) {
        RecommendationEffect effect = getById(effectId);
        if (effect != null) {
            effect.setInteractionResult(interactionResult);
            return updateById(effect);
        }
        return false;
    }
    
    @Override
    public List<RecommendationEffect> getEffectStatsByTimeRange(java.time.LocalDateTime startTime, java.time.LocalDateTime endTime) {
        return lambdaQuery()
                .between(RecommendationEffect::getInteractionTime, startTime, endTime)
                .list();
    }
    
    @Override
    public java.util.Map<String, Object> getRecommendationEffectiveness(Long recommendationId) {
        List<RecommendationEffect> effects = getEffectsByRecommendationId(recommendationId);
        
        long totalInteractions = effects.size();
        long positiveInteractions = effects.stream()
                .filter(effect -> "positive".equals(effect.getInteractionResult()))
                .count();
        
        double effectivenessRate = totalInteractions > 0 ? (double) positiveInteractions / totalInteractions : 0.0;
        
        return java.util.Map.of(
            "totalInteractions", totalInteractions,
            "positiveInteractions", positiveInteractions,
            "effectivenessRate", effectivenessRate
        );
    }
    
    @Override
    public boolean recordImpression(Long recommendationId, Long userId) {
        RecommendationEffect effect = new RecommendationEffect();
        effect.setUserId(userId);
        effect.setRecommendationId(recommendationId);
        effect.setInteractionType("impression");
        effect.setInteractionResult("viewed");
        effect.setInteractionTime(new java.util.Date());
        
        return save(effect);
    }
    
    @Override
    public boolean recordClick(Long recommendationId, Long userId) {
        RecommendationEffect effect = new RecommendationEffect();
        effect.setUserId(userId);
        effect.setRecommendationId(recommendationId);
        effect.setInteractionType("click");
        effect.setInteractionResult("clicked");
        effect.setInteractionTime(new java.util.Date());
        
        return save(effect);
    }
    
    @Override
    public boolean recordConversion(Long recommendationId, Long userId) {
        RecommendationEffect effect = new RecommendationEffect();
        effect.setUserId(userId);
        effect.setRecommendationId(recommendationId);
        effect.setInteractionType("conversion");
        effect.setInteractionResult("converted");
        effect.setInteractionTime(new java.util.Date());
        
        return save(effect);
    }
}