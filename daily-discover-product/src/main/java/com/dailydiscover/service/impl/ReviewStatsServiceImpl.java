package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ReviewStatsMapper;
import com.dailydiscover.model.ReviewStats;
import com.dailydiscover.service.ReviewStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReviewStatsServiceImpl extends ServiceImpl<ReviewStatsMapper, ReviewStats> implements ReviewStatsService {
    
    @Autowired
    private ReviewStatsMapper reviewStatsMapper;
    
    @Override
    public ReviewStats getByProductId(Long productId) {
        return reviewStatsMapper.findByProductId(productId);
    }
    
    @Override
    public boolean updateReviewStats(Long productId, Integer totalReviews, Integer positiveReviews, 
                                    Integer neutralReviews, Integer negativeReviews, Double averageRating) {
        ReviewStats stats = getByProductId(productId);
        if (stats == null) {
            stats = new ReviewStats();
            stats.setProductId(productId);
            stats.setTotalReviews(totalReviews);
            // 使用 rating_distribution 字段存储评分分布
            String ratingDistribution = String.format("{\"5\": %d, \"4\": %d, \"3\": %d, \"2\": %d, \"1\": %d}", 
                positiveReviews, neutralReviews, negativeReviews, 0, 0);
            stats.setRatingDistribution(ratingDistribution);
            stats.setAverageRating(new java.math.BigDecimal(averageRating));
            return reviewStatsMapper.insert(stats) > 0;
        } else {
            stats.setTotalReviews(totalReviews);
            // 使用 rating_distribution 字段存储评分分布
            String ratingDistribution = String.format("{\"5\": %d, \"4\": %d, \"3\": %d, \"2\": %d, \"1\": %d}", 
                positiveReviews, neutralReviews, negativeReviews, 0, 0);
            stats.setRatingDistribution(ratingDistribution);
            stats.setAverageRating(new java.math.BigDecimal(averageRating));
            return reviewStatsMapper.updateById(stats) > 0;
        }
    }
    
    @Override
    public boolean incrementReviewStats(Long productId, Integer rating) {
        ReviewStats stats = getByProductId(productId);
        if (stats == null) {
            stats = new ReviewStats();
            stats.setProductId(productId);
            stats.setTotalReviews(1);
            stats.setAverageRating(new java.math.BigDecimal(rating.doubleValue()));
            
            // 根据评分设置评分分布
            String ratingDistribution = "{\"5\": 0, \"4\": 0, \"3\": 0, \"2\": 0, \"1\": 0}";
            if (rating >= 4) {
                ratingDistribution = String.format("{\"5\": %d, \"4\": %d, \"3\": 0, \"2\": 0, \"1\": 0}", 
                    rating == 5 ? 1 : 0, rating == 4 ? 1 : 0);
            } else if (rating == 3) {
                ratingDistribution = "{\"5\": 0, \"4\": 0, \"3\": 1, \"2\": 0, \"1\": 0}";
            } else {
                ratingDistribution = String.format("{\"5\": 0, \"4\": 0, \"3\": 0, \"2\": %d, \"1\": %d}", 
                    rating == 2 ? 1 : 0, rating == 1 ? 1 : 0);
            }
            stats.setRatingDistribution(ratingDistribution);
            return reviewStatsMapper.insert(stats) > 0;
        } else {
            stats.setTotalReviews(stats.getTotalReviews() + 1);
            
            // 更新平均评分
            double totalScore = stats.getAverageRating().doubleValue() * (stats.getTotalReviews() - 1) + rating;
            stats.setAverageRating(new java.math.BigDecimal(totalScore / stats.getTotalReviews()));
            
            // 更新评分分布（这里简化处理，实际应该解析JSON并更新）
            // 由于rating_distribution是JSON字段，这里只更新总评分，分布需要更复杂的逻辑
            return reviewStatsMapper.updateById(stats) > 0;
        }
    }
    
    @Override
    public boolean decrementReviewStats(Long productId, Integer rating) {
        ReviewStats stats = getByProductId(productId);
        if (stats != null && stats.getTotalReviews() > 0) {
            stats.setTotalReviews(stats.getTotalReviews() - 1);
            
            // 更新平均评分
            if (stats.getTotalReviews() > 0) {
                double totalScore = stats.getAverageRating().doubleValue() * (stats.getTotalReviews() + 1) - rating;
                stats.setAverageRating(new java.math.BigDecimal(totalScore / stats.getTotalReviews()));
            } else {
                stats.setAverageRating(new java.math.BigDecimal(0.0));
            }
            
            // 更新评分分布（这里简化处理，实际应该解析JSON并更新）
            // 由于rating_distribution是JSON字段，这里只更新总评分，分布需要更复杂的逻辑
            return reviewStatsMapper.updateById(stats) > 0;
        }
        return false;
    }
    
    @Override
    public java.util.List<ReviewStats> getHighRatedProducts(Double minRating, Integer limit) {
        return reviewStatsMapper.findHighRatingStats(minRating, limit);
    }
    
    @Override
    public java.util.List<ReviewStats> getReviewStatsRanking(Integer limit) {
        return reviewStatsMapper.findPopularStats(limit);
    }
    
    /**
     * 批量更新评价统计
     */
    public boolean batchUpdateReviewStats(java.util.List<ReviewStats> statsList) {
        return reviewStatsMapper.batchUpdateReviewStats(statsList) > 0;
    }
}