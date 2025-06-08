package com.example.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.common.exception.ApiException;
import com.example.mapper.ProductMarketingMapper;
import com.example.model.ProductMarketing;
import com.example.service.ProductMarketingService;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ProductMarketingServiceImpl implements ProductMarketingService {

    @Autowired
    private ProductMarketingMapper marketingMapper;
    
    @Override
    @Transactional
    public ProductMarketing initialize(Long productId) {
        ProductMarketing marketing = marketingMapper.findByProductId(productId);
        if (marketing != null) {
            return marketing;
        }
        
        try {
            ProductMarketing newMarketing = ProductMarketing.create(productId);
            marketingMapper.insert(newMarketing);
            return marketingMapper.findByProductId(productId);
        } catch (Exception e) {
            log.error("初始化商品营销信息失败", e);
            throw new ApiException("初始化商品营销信息失败: " + e.getMessage());
        }
    }
    
    @Override
    public ProductMarketing getByProductId(Long productId) {
        ProductMarketing marketing = marketingMapper.findByProductId(productId);
        if (marketing == null) {
            return initialize(productId);
        }
        return marketing;
    }
    
    @Override
    @Transactional
    public boolean update(ProductMarketing marketing) {
        try {
            int rows = marketingMapper.updateByProductId(marketing);
            return rows > 0;
        } catch (Exception e) {
            log.error("更新商品营销信息失败", e);
            throw new ApiException("更新商品营销信息失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean setHot(Long productId, boolean isHot) {
        try {
            ProductMarketing marketing = getByProductId(productId);
            marketing.setIsHot(isHot);
            if (isHot && (marketing.getSortWeight() == null || marketing.getSortWeight() == 0)) {
                marketing.setSortWeight(100); // 默认给热门商品较高权重
            }
            return update(marketing);
        } catch (Exception e) {
            log.error("设置商品热门状态失败", e);
            throw new ApiException("设置商品热门状态失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean setNew(Long productId, boolean isNew) {
        try {
            ProductMarketing marketing = getByProductId(productId);
            marketing.setIsNew(isNew);
            if (isNew && (marketing.getSortWeight() == null || marketing.getSortWeight() == 0)) {
                marketing.setSortWeight(90); // 默认给新品较高权重
            }
            return update(marketing);
        } catch (Exception e) {
            log.error("设置商品新品状态失败", e);
            throw new ApiException("设置商品新品状态失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean setRecommended(Long productId, boolean isRecommended) {
        try {
            ProductMarketing marketing = getByProductId(productId);
            marketing.setIsRecommended(isRecommended);
            if (isRecommended && (marketing.getSortWeight() == null || marketing.getSortWeight() == 0)) {
                marketing.setSortWeight(80); // 默认给推荐商品较高权重
            }
            return update(marketing);
        } catch (Exception e) {
            log.error("设置商品推荐状态失败", e);
            throw new ApiException("设置商品推荐状态失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean setShowInHomepage(Long productId, boolean showInHomepage) {
        try {
            ProductMarketing marketing = getByProductId(productId);
            marketing.setShowInHomepage(showInHomepage);
            if (showInHomepage && (marketing.getSortWeight() == null || marketing.getSortWeight() == 0)) {
                marketing.setSortWeight(70); // 默认给首页展示商品较高权重
            }
            return update(marketing);
        } catch (Exception e) {
            log.error("设置商品首页展示状态失败", e);
            throw new ApiException("设置商品首页展示状态失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean updateLabels(Long productId, List<String> labels) {
        try {
            ProductMarketing marketing = getByProductId(productId);
            marketing.setLabels(labels);
            return update(marketing);
        } catch (Exception e) {
            log.error("更新商品标签失败", e);
            throw new ApiException("更新商品标签失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean updateRelatedProducts(Long productId, List<Long> relatedProductIds) {
        try {
            ProductMarketing marketing = getByProductId(productId);
            marketing.setRelatedProductIds(relatedProductIds);
            return update(marketing);
        } catch (Exception e) {
            log.error("更新相关商品列表失败", e);
            throw new ApiException("更新相关商品列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<ProductMarketing> getHotProducts(int limit) {
        if (limit <= 0) {
            limit = 10;
        }
        try {
            return marketingMapper.findHotProducts(limit);
        } catch (Exception e) {
            log.error("获取热门商品列表失败", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<ProductMarketing> getNewProducts(int limit) {
        if (limit <= 0) {
            limit = 10;
        }
        try {
            return marketingMapper.findNewProducts(limit);
        } catch (Exception e) {
            log.error("获取新品列表失败", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<ProductMarketing> getRecommendedProducts(int limit) {
        if (limit <= 0) {
            limit = 10;
        }
        try {
            return marketingMapper.findRecommendedProducts(limit);
        } catch (Exception e) {
            log.error("获取推荐商品列表失败", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<ProductMarketing> getHomepageProducts(int limit) {
        if (limit <= 0) {
            limit = 10;
        }
        try {
            return marketingMapper.findHomepageProducts(limit);
        } catch (Exception e) {
            log.error("获取首页展示商品列表失败", e);
            return new ArrayList<>();
        }
    }
} 