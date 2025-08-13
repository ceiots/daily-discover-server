package com.dailydiscover.service;

import com.dailydiscover.model.Product;
import com.dailydiscover.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    
    @Autowired
    private ProductMapper productMapper;
    
    /**
     * 获取智能推荐商品
     * 基于热销、高质量和快速配送等特性进行推荐
     */
    public List<Product> getSmartRecommendations(int limit) {
        // 获取所有活跃商品
        List<Product> allProducts = productMapper.findAllByIsActiveTrue();
        
        if (allProducts.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 获取具有特定特性的商品（热销、高质量或快速配送）
        List<Product> featuredProducts = productMapper.findFeaturedProducts();
        
        // 如果没有特色商品，随机选择一些普通商品
        if (featuredProducts.isEmpty()) {
            Collections.shuffle(allProducts);
            return allProducts.stream()
                    .limit(limit)
                    .collect(Collectors.toList());
        }
        
        // 如果特色商品不足，添加一些普通商品
        if (featuredProducts.size() < limit) {
            List<Product> regularProducts = allProducts.stream()
                    .filter(p -> !featuredProducts.contains(p))
                    .collect(Collectors.toList());
            Collections.shuffle(regularProducts);
            
            int remaining = limit - featuredProducts.size();
            featuredProducts.addAll(regularProducts.stream()
                    .limit(remaining)
                    .collect(Collectors.toList()));
        }
        
        // 如果特色商品超过限制，随机选择一部分
        if (featuredProducts.size() > limit) {
            Collections.shuffle(featuredProducts);
            return featuredProducts.stream()
                    .limit(limit)
                    .collect(Collectors.toList());
        }
        
        return featuredProducts;
    }
    
    /**
     * 基于用户浏览历史的个性化推荐
     * 这里使用模拟数据，实际应用中可以接入用户行为分析系统
     */
    public List<Product> getPersonalizedRecommendations(String userId, int limit) {
        // 获取所有活跃商品
        List<Product> allProducts = productMapper.findAllByIsActiveTrue();
        
        if (allProducts.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 模拟个性化推荐逻辑
        // 实际应用中，这里应该基于用户的浏览历史、购买记录等数据进行推荐
        Collections.shuffle(allProducts);
        return allProducts.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
}