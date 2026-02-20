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
        // 使用 Mapper 方法查询
        return recommendationEffectMapper.findByUserId(userId);
    }
    
    @Override
    public List<RecommendationEffect> getEffectsByRecommendationId(Long recommendationId) {
        // 使用 Mapper 方法查询
        return recommendationEffectMapper.findByRecommendationId(recommendationId);
    }
    
    @Override
    public boolean recordImpression(Long recommendationId, Long userId) {
        RecommendationEffect effect = new RecommendationEffect();
        effect.setUserId(userId);
        effect.setRecommendationId(recommendationId);
        effect.setInteractionType("impression");
        effect.setInteractionResult("viewed");
        effect.setInteractionTime(java.time.LocalDateTime.now());
        
        return save(effect);
    }
    
    @Override
    public boolean recordClick(Long recommendationId, Long userId) {
        RecommendationEffect effect = new RecommendationEffect();
        effect.setUserId(userId);
        effect.setRecommendationId(recommendationId);
        effect.setInteractionType("click");
        effect.setInteractionResult("clicked");
        effect.setInteractionTime(java.time.LocalDateTime.now());
        
        return save(effect);
    }
    
    @Override
    public boolean recordConversion(Long recommendationId, Long userId) {
        RecommendationEffect effect = new RecommendationEffect();
        effect.setUserId(userId);
        effect.setRecommendationId(recommendationId);
        effect.setInteractionType("conversion");
        effect.setInteractionResult("converted");
        effect.setInteractionTime(java.time.LocalDateTime.now());
        
        return save(effect);
    }
}