package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ProductRecommendationMapper;
import com.dailydiscover.model.ProductRecommendation;
import com.dailydiscover.service.ProductRecommendationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductRecommendationServiceImpl extends ServiceImpl<ProductRecommendationMapper, ProductRecommendation> implements ProductRecommendationService {
    
    @Autowired
    private ProductRecommendationMapper productRecommendationMapper;
    
    @Override
    public java.util.List<ProductRecommendation> getRecommendationsByProductId(Long productId) {
        // 使用 MyBatis-Plus 的 lambda 查询实现
        return lambdaQuery()
                .eq(ProductRecommendation::getProductId, productId)
                .eq(ProductRecommendation::getIsActive, true)
                .orderByAsc(ProductRecommendation::getPosition)
                .list();
    }
    
    @Override
    public java.util.List<ProductRecommendation> getPersonalizedRecommendations(Long userId) {
        // 使用 Mapper 方法查询
        return productRecommendationMapper.findByUserId(userId);
    }
    
    @Override
    public java.util.List<ProductRecommendation> getRecommendationsByType(String recommendationType) {
        // 使用 MyBatis-Plus 的 lambda 查询实现
        return lambdaQuery()
                .eq(ProductRecommendation::getRecommendationType, recommendationType)
                .eq(ProductRecommendation::getIsActive, true)
                .orderByDesc(ProductRecommendation::getRecommendationScore)
                .list();
    }
    

}