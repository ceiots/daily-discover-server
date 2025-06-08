package com.example.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.common.exception.ApiException;
import com.example.mapper.ProductStatisticsMapper;
import com.example.model.ProductStatistics;
import com.example.service.ProductStatisticsService;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Slf4j
@Service
public class ProductStatisticsServiceImpl implements ProductStatisticsService {

    @Autowired
    private ProductStatisticsMapper statisticsMapper;
    
    @Override
    @Transactional
    public ProductStatistics initialize(Long productId) {
        ProductStatistics statistics = statisticsMapper.findByProductId(productId);
        if (statistics != null) {
            return statistics;
        }
        
        try {
            ProductStatistics newStatistics = ProductStatistics.create(productId);
            statisticsMapper.insert(newStatistics);
            return statisticsMapper.findByProductId(productId);
        } catch (Exception e) {
            log.error("初始化商品统计数据失败", e);
            throw new ApiException("初始化商品统计数据失败: " + e.getMessage());
        }
    }
    
    @Override
    public ProductStatistics getByProductId(Long productId) {
        ProductStatistics statistics = statisticsMapper.findByProductId(productId);
        if (statistics == null) {
            return initialize(productId);
        }
        return statistics;
    }
    
    @Override
    @Transactional
    public boolean incrementViewCount(Long productId) {
        try {
            // 确保统计记录存在
            ProductStatistics statistics = getByProductId(productId);
            
            // 增加浏览次数
            int rows = statisticsMapper.incrementViewCount(productId);
            
            // 更新日浏览趋势
            updateDailyViewsTrend(productId);
            
            return rows > 0;
        } catch (Exception e) {
            log.error("增加商品浏览次数失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean updateFavoriteCount(Long productId, int increment) {
        try {
            // 确保统计记录存在
            ProductStatistics statistics = getByProductId(productId);
            
            // 更新收藏次数
            int rows = statisticsMapper.updateFavoriteCount(productId, increment);
            
            return rows > 0;
        } catch (Exception e) {
            log.error("更新商品收藏次数失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean incrementShareCount(Long productId) {
        try {
            // 确保统计记录存在
            ProductStatistics statistics = getByProductId(productId);
            
            // 增加分享次数
            int rows = statisticsMapper.incrementShareCount(productId);
            
            return rows > 0;
        } catch (Exception e) {
            log.error("增加商品分享次数失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean updateReviewStats(Long productId, Double rating, Integer reviewCount,
                                   Integer positiveReviews, Integer negativeReviews) {
        try {
            // 确保统计记录存在
            ProductStatistics statistics = getByProductId(productId);
            
            // 更新评价统计
            int rows = statisticsMapper.updateReviewStats(productId, rating, reviewCount,
                                                         positiveReviews, negativeReviews);
            
            return rows > 0;
        } catch (Exception e) {
            log.error("更新商品评价统计失败", e);
            return false;
        }
    }
    
    @Override
    public List<ProductStatistics> getMostViewedProducts(int limit) {
        if (limit <= 0) {
            limit = 10;
        }
        return statisticsMapper.findMostViewed(limit);
    }
    
    @Override
    public List<ProductStatistics> getHighestRatedProducts(int limit) {
        if (limit <= 0) {
            limit = 10;
        }
        return statisticsMapper.findHighestRated(limit);
    }
    
    @Override
    @Transactional
    public boolean updateLastActiveTime(Long productId) {
        try {
            ProductStatistics statistics = getByProductId(productId);
            statistics.setLastActiveTime(new Date());
            
            int rows = statisticsMapper.updateByProductId(statistics);
            return rows > 0;
        } catch (Exception e) {
            log.error("更新商品最后活跃时间失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean updateDailyViewsTrend(Long productId) {
        try {
            ProductStatistics statistics = getByProductId(productId);
            
            // 获取当前日期
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String today = dateFormat.format(new Date());
            
            // 更新浏览趋势
            Map<String, Integer> viewsTrend = statistics.getDailyViewsTrend();
            if (viewsTrend == null) {
                viewsTrend = new HashMap<>();
            }
            
            Integer todayViews = viewsTrend.get(today);
            if (todayViews == null) {
                todayViews = 0;
            }
            viewsTrend.put(today, todayViews + 1);
            
            // 保留最近30天的数据
            if (viewsTrend.size() > 30) {
                List<String> dates = new ArrayList<>(viewsTrend.keySet());
                dates.sort(String::compareTo);
                while (dates.size() > 30) {
                    viewsTrend.remove(dates.get(0));
                    dates.remove(0);
                }
            }
            
            statistics.setDailyViewsTrend(viewsTrend);
            
            int rows = statisticsMapper.updateByProductId(statistics);
            return rows > 0;
        } catch (Exception e) {
            log.error("更新商品每日浏览趋势失败", e);
            return false;
        }
    }
    
    @Override
    public Map<String, Integer> getViewsTrend(Long productId) {
        ProductStatistics statistics = getByProductId(productId);
        return statistics.getDailyViewsTrend();
    }
} 