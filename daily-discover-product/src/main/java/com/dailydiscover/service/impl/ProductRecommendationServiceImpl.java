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
    public java.util.List<ProductRecommendation> getRecommendationsByProductId(Long productId) {
        return lambdaQuery().eq(ProductRecommendation::getProductId, productId).orderByDesc(ProductRecommendation::getScore).list();
    }
    
    @Override
    public java.util.List<ProductRecommendation> getPersonalizedRecommendations(Long userId) {
        return lambdaQuery().eq(ProductRecommendation::getUserId, userId).orderByDesc(ProductRecommendation::getScore).list();
    }
    
    @Override
    public java.util.List<ProductRecommendation> getRecommendationsByType(String recommendationType) {
        return lambdaQuery().eq(ProductRecommendation::getRecommendationType, recommendationType).orderByDesc(ProductRecommendation::getScore).list();
    }
    

}