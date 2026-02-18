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
    public boolean updateReviewStats(Long productId, Integer totalReviews, Integer positiveReviews, 
                                    Integer neutralReviews, Integer negativeReviews, Double averageRating) {
        ReviewStats stats = getByProductId(productId);
        if (stats == null) {
            stats = new ReviewStats();
            stats.setProductId(productId);
            stats.setTotalReviews(totalReviews);
            stats.setPositiveReviews(positiveReviews);
            stats.setNeutralReviews(neutralReviews);
            stats.setNegativeReviews(negativeReviews);
            stats.setAverageRating(averageRating);
            return save(stats);
        } else {
            stats.setTotalReviews(totalReviews);
            stats.setPositiveReviews(positiveReviews);
            stats.setNeutralReviews(neutralReviews);
            stats.setNegativeReviews(negativeReviews);
            stats.setAverageRating(averageRating);
            return updateById(stats);
        }
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
    public boolean incrementReviewStats(Long productId, Integer rating) {
        ReviewStats stats = getByProductId(productId);
        if (stats == null) {
            stats = new ReviewStats();
            stats.setProductId(productId);
            stats.setTotalReviews(1);
            stats.setAverageRating(rating.doubleValue());
            
            // 根据评分设置正面/中性/负面评论计数
            if (rating >= 4) {
                stats.setPositiveReviews(1);
                stats.setNeutralReviews(0);
                stats.setNegativeReviews(0);
            } else if (rating == 3) {
                stats.setPositiveReviews(0);
                stats.setNeutralReviews(1);
                stats.setNegativeReviews(0);
            } else {
                stats.setPositiveReviews(0);
                stats.setNeutralReviews(0);
                stats.setNegativeReviews(1);
            }
            return save(stats);
        } else {
            stats.setTotalReviews(stats.getTotalReviews() + 1);
            
            // 更新平均评分
            double totalScore = stats.getAverageRating() * (stats.getTotalReviews() - 1) + rating;
            stats.setAverageRating(totalScore / stats.getTotalReviews());
            
            // 更新评论类型计数
            if (rating >= 4) {
                stats.setPositiveReviews(stats.getPositiveReviews() + 1);
            } else if (rating == 3) {
                stats.setNeutralReviews(stats.getNeutralReviews() + 1);
            } else {
                stats.setNegativeReviews(stats.getNegativeReviews() + 1);
            }
            return updateById(stats);
        }
    }
    
    @Override
    public boolean decrementReviewStats(Long productId, Integer rating) {
        ReviewStats stats = getByProductId(productId);
        if (stats != null && stats.getTotalReviews() > 0) {
            stats.setTotalReviews(stats.getTotalReviews() - 1);
            
            // 更新平均评分
            if (stats.getTotalReviews() > 0) {
                double totalScore = stats.getAverageRating() * (stats.getTotalReviews() + 1) - rating;
                stats.setAverageRating(totalScore / stats.getTotalReviews());
            } else {
                stats.setAverageRating(0.0);
            }
            
            // 更新评论类型计数
            if (rating >= 4) {
                stats.setPositiveReviews(Math.max(0, stats.getPositiveReviews() - 1));
            } else if (rating == 3) {
                stats.setNeutralReviews(Math.max(0, stats.getNeutralReviews() - 1));
            } else {
                stats.setNegativeReviews(Math.max(0, stats.getNegativeReviews() - 1));
            }
            return updateById(stats);
        }
        return false;
    }
    
    @Override
    public java.util.List<ReviewStats> getHighRatedProducts(Double minRating, Integer limit) {
        return lambdaQuery()
                .ge(ReviewStats::getAverageRating, minRating)
                .orderByDesc(ReviewStats::getAverageRating)
                .last(limit != null ? "LIMIT " + limit : "")
                .list();
    }
    
    @Override
    public java.util.List<ReviewStats> getReviewStatsRanking(Integer limit) {
        return lambdaQuery()
                .orderByDesc(ReviewStats::getTotalReviews)
                .last(limit != null ? "LIMIT " + limit : "")
                .list();
    }
}