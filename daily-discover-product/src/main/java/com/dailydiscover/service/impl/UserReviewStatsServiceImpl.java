package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.UserReviewStatsMapper;
import com.dailydiscover.model.UserReviewStats;
import com.dailydiscover.service.UserReviewStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserReviewStatsServiceImpl extends ServiceImpl<UserReviewStatsMapper, UserReviewStats> implements UserReviewStatsService {
    
    @Autowired
    private UserReviewStatsMapper userReviewStatsMapper;
    
    @Override
    public UserReviewStats getByUserId(Long userId) {
        return lambdaQuery().eq(UserReviewStats::getUserId, userId).one();
    }
    
    @Override
    public boolean updateUserReviewStats(Long userId, Integer totalReviews, Integer helpfulReviews, 
                                        Integer unhelpfulReviews, Double averageRating) {
        UserReviewStats stats = getByUserId(userId);
        if (stats == null) {
            stats = new UserReviewStats();
            stats.setUserId(userId);
            stats.setTotalReviews(totalReviews);
            stats.setHelpfulReviews(helpfulReviews);
            stats.setUnhelpfulReviews(unhelpfulReviews);
            stats.setAverageRating(averageRating);
            return save(stats);
        } else {
            stats.setTotalReviews(totalReviews);
            stats.setHelpfulReviews(helpfulReviews);
            stats.setUnhelpfulReviews(unhelpfulReviews);
            stats.setAverageRating(averageRating);
            return updateById(stats);
        }
    }
    
    @Override
    public boolean incrementUserReviewStats(Long userId, Integer rating, Boolean isHelpful) {
        UserReviewStats stats = getByUserId(userId);
        if (stats == null) {
            stats = new UserReviewStats();
            stats.setUserId(userId);
            stats.setTotalReviews(1);
            stats.setHelpfulReviews(isHelpful ? 1 : 0);
            stats.setUnhelpfulReviews(isHelpful ? 0 : 1);
            stats.setAverageRating(rating.doubleValue());
            return save(stats);
        } else {
            stats.setTotalReviews(stats.getTotalReviews() + 1);
            if (isHelpful) {
                stats.setHelpfulReviews(stats.getHelpfulReviews() + 1);
            } else {
                stats.setUnhelpfulReviews(stats.getUnhelpfulReviews() + 1);
            }
            
            // 更新平均评分
            double totalScore = stats.getAverageRating() * (stats.getTotalReviews() - 1) + rating;
            stats.setAverageRating(totalScore / stats.getTotalReviews());
            
            return updateById(stats);
        }
    }
    
    @Override
    public List<UserReviewStats> getActiveReviewers(Integer limit) {
        return lambdaQuery()
                .orderByDesc(UserReviewStats::getTotalReviews)
                .last(limit != null ? "LIMIT " + limit : "")
                .list();
    }
    
    @Override
    public List<UserReviewStats> getHighQualityReviewers(Double minRating, Integer minHelpfulReviews, Integer limit) {
        return lambdaQuery()
                .ge(UserReviewStats::getAverageRating, minRating)
                .ge(UserReviewStats::getHelpfulReviews, minHelpfulReviews)
                .orderByDesc(UserReviewStats::getHelpfulReviews)
                .last(limit != null ? "LIMIT " + limit : "")
                .list();
    }
}