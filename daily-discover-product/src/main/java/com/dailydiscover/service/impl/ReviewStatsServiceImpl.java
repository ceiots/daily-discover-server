package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ReviewStatsMapper;
import com.dailydiscover.model.ReviewStats;
import com.dailydiscover.service.ReviewStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ReviewStatsServiceImpl extends ServiceImpl<ReviewStatsMapper, ReviewStats> implements ReviewStatsService {
    
    @Autowired
    private ReviewStatsMapper reviewStatsMapper;
    
    @Override
    public ReviewStats getByProductId(Long productId) {
        return lambdaQuery().eq(ReviewStats::getProductId, productId).one();
    }
    
    @Override
    public boolean updateReviewStats(Long productId, Integer rating, String reviewType) {
        ReviewStats stats = getByProductId(productId);
        if (stats == null) {
            stats = new ReviewStats();
            stats.setProductId(productId);
            stats.setTotalReviews(1);
            stats.setAverageRating(rating.doubleValue());
            stats.setFiveStarCount(rating == 5 ? 1 : 0);
            stats.setFourStarCount(rating == 4 ? 1 : 0);
            stats.setThreeStarCount(rating == 3 ? 1 : 0);
            stats.setTwoStarCount(rating == 2 ? 1 : 0);
            stats.setOneStarCount(rating == 1 ? 1 : 0);
            return save(stats);
        } else {
            stats.setTotalReviews(stats.getTotalReviews() + 1);
            
            // 更新平均评分
            double totalScore = stats.getAverageRating() * (stats.getTotalReviews() - 1) + rating;
            stats.setAverageRating(totalScore / stats.getTotalReviews());
            
            // 更新星级计数
            switch (rating) {
                case 5: stats.setFiveStarCount(stats.getFiveStarCount() + 1); break;
                case 4: stats.setFourStarCount(stats.getFourStarCount() + 1); break;
                case 3: stats.setThreeStarCount(stats.getThreeStarCount() + 1); break;
                case 2: stats.setTwoStarCount(stats.getTwoStarCount() + 1); break;
                case 1: stats.setOneStarCount(stats.getOneStarCount() + 1); break;
            }
            
            return updateById(stats);
        }
    }
    
    @Override
    public List<ReviewStats> getTopRatedProducts(Integer limit) {
        return lambdaQuery()
                .orderByDesc(ReviewStats::getAverageRating)
                .last("LIMIT " + limit)
                .list();
    }
    
    @Override
    public List<ReviewStats> getMostReviewedProducts(Integer limit) {
        return lambdaQuery()
                .orderByDesc(ReviewStats::getTotalReviews)
                .last("LIMIT " + limit)
                .list();
    }
    
    @Override
    public java.util.Map<String, Object> getProductRatingDistribution(Long productId) {
        ReviewStats stats = getByProductId(productId);
        if (stats == null) {
            return java.util.Map.of(
                "totalReviews", 0,
                "averageRating", 0.0,
                "ratingDistribution", java.util.Map.of(
                    "fiveStar", 0,
                    "fourStar", 0,
                    "threeStar", 0,
                    "twoStar", 0,
                    "oneStar", 0
                )
            );
        }
        
        return java.util.Map.of(
            "totalReviews", stats.getTotalReviews(),
            "averageRating", stats.getAverageRating(),
            "ratingDistribution", java.util.Map.of(
                "fiveStar", stats.getFiveStarCount(),
                "fourStar", stats.getFourStarCount(),
                "threeStar", stats.getThreeStarCount(),
                "twoStar", stats.getTwoStarCount(),
                "oneStar", stats.getOneStarCount()
            )
        );
    }
}