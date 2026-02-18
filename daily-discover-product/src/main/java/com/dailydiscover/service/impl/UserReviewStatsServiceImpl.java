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
    public boolean updateUserReviewStats(Long userId, Integer rating) {
        UserReviewStats stats = getByUserId(userId);
        if (stats == null) {
            stats = new UserReviewStats();
            stats.setUserId(userId);
            stats.setTotalReviews(1);
            stats.setAverageRating(rating.doubleValue());
            return save(stats);
        } else {
            stats.setTotalReviews(stats.getTotalReviews() + 1);
            
            // 更新平均评分
            double totalScore = stats.getAverageRating() * (stats.getTotalReviews() - 1) + rating;
            stats.setAverageRating(totalScore / stats.getTotalReviews());
            
            return updateById(stats);
        }
    }
    
    @Override
    public List<UserReviewStats> getTopReviewers(Integer limit) {
        return lambdaQuery()
                .orderByDesc(UserReviewStats::getTotalReviews)
                .last("LIMIT " + limit)
                .list();
    }
    
    @Override
    public List<UserReviewStats> getMostHelpfulReviewers(Integer limit) {
        return lambdaQuery()
                .orderByDesc(UserReviewStats::getHelpfulVotes)
                .last("LIMIT " + limit)
                .list();
    }
    
    @Override
    public boolean incrementHelpfulVotes(Long userId) {
        UserReviewStats stats = getByUserId(userId);
        if (stats != null) {
            stats.setHelpfulVotes(stats.getHelpfulVotes() + 1);
            return updateById(stats);
        }
        return false;
    }
    
    @Override
    public java.util.Map<String, Object> getUserReviewStats(Long userId) {
        UserReviewStats stats = getByUserId(userId);
        if (stats == null) {
            return java.util.Map.of(
                "totalReviews", 0,
                "averageRating", 0.0,
                "helpfulVotes", 0
            );
        }
        
        return java.util.Map.of(
            "totalReviews", stats.getTotalReviews(),
            "averageRating", stats.getAverageRating(),
            "helpfulVotes", stats.getHelpfulVotes()
        );
    }
}