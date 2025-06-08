package com.example.service;

import org.springframework.transaction.annotation.Transactional;

import com.example.model.ProductStatistics;

import java.util.List;
import java.util.Map;

public interface ProductStatisticsService {

    /**
     * 初始化商品统计数据
     */
    @Transactional
    ProductStatistics initialize(Long productId);
    
    /**
     * 获取商品统计数据
     */
    ProductStatistics getByProductId(Long productId);
    
    /**
     * 增加商品浏览次数
     */
    @Transactional
    boolean incrementViewCount(Long productId);
    
    /**
     * 更新商品收藏次数
     * @param increment 增加(正数)或减少(负数)的数量
     */
    @Transactional
    boolean updateFavoriteCount(Long productId, int increment);
    
    /**
     * 增加商品分享次数
     */
    @Transactional
    boolean incrementShareCount(Long productId);
    
    /**
     * 更新商品评价统计数据
     */
    @Transactional
    boolean updateReviewStats(Long productId, Double rating, Integer reviewCount,
                            Integer positiveReviews, Integer negativeReviews);
    
    /**
     * 获取浏览量最高的商品
     */
    List<ProductStatistics> getMostViewedProducts(int limit);
    
    /**
     * 获取评分最高的商品
     */
    List<ProductStatistics> getHighestRatedProducts(int limit);
    
    /**
     * 更新商品最后活跃时间
     */
    @Transactional
    boolean updateLastActiveTime(Long productId);
    
    /**
     * 更新商品每日浏览趋势
     */
    @Transactional
    boolean updateDailyViewsTrend(Long productId);
    
    /**
     * 获取商品浏览趋势数据
     */
    Map<String, Integer> getViewsTrend(Long productId);
} 