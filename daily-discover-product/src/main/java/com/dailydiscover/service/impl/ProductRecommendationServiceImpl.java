package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ProductRecommendationMapper;
import com.dailydiscover.model.ProductRecommendation;
import com.dailydiscover.service.ProductRecommendationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductRecommendationServiceImpl extends ServiceImpl<ProductRecommendationMapper, ProductRecommendation> implements ProductRecommendationService {
    
    @Autowired
    private ProductRecommendationMapper productRecommendationMapper;
    
    @Override
    public List<ProductRecommendation> getRecommendationsByUserId(Long userId) {
        return lambdaQuery().eq(ProductRecommendation::getUserId, userId).orderByDesc(ProductRecommendation::getScore).list();
    }
    
    @Override
    public List<ProductRecommendation> getPopularRecommendations(Integer limit) {
        return lambdaQuery().orderByDesc(ProductRecommendation::getScore).last("LIMIT " + limit).list();
    }
    
    @Override
    public List<ProductRecommendation> getSimilarProductRecommendations(Long productId, Integer limit) {
        return lambdaQuery()
                .ne(ProductRecommendation::getProductId, productId)
                .orderByDesc(ProductRecommendation::getScore)
                .last("LIMIT " + limit)
                .list();
    }
    
    @Override
    public ProductRecommendation createRecommendation(Long userId, Long productId, String recommendationType, Double score, String reason) {
        ProductRecommendation recommendation = new ProductRecommendation();
        recommendation.setUserId(userId);
        recommendation.setProductId(productId);
        recommendation.setRecommendationType(recommendationType);
        recommendation.setScore(score);
        recommendation.setReason(reason);
        
        save(recommendation);
        return recommendation;
    }
    
    @Override
    public boolean updateRecommendationScore(Long recommendationId, Double score) {
        ProductRecommendation recommendation = getById(recommendationId);
        if (recommendation != null) {
            recommendation.setScore(score);
            return updateById(recommendation);
        }
        return false;
    }
    
    @Override
    public boolean deleteUserRecommendations(Long userId) {
        return lambdaUpdate()
                .eq(ProductRecommendation::getUserId, userId)
                .remove();
    }
    
    @Override
    public List<Long> getRecommendedProductIds(Long userId, Integer limit) {
        List<ProductRecommendation> recommendations = getRecommendationsByUserId(userId);
        return recommendations.stream()
                .limit(limit)
                .map(ProductRecommendation::getProductId)
                .collect(java.util.stream.Collectors.toList());
    }
}