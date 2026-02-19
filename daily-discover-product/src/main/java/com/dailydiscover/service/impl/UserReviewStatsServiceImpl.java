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
    public UserReviewStats getByReviewId(Long reviewId) {
        return userReviewStatsMapper.findByReviewId(reviewId);
    }
    
    @Override
    public boolean updateReviewStats(Long reviewId, Integer helpfulCount, Integer replyCount, Integer likeCount) {
        UserReviewStats stats = getByReviewId(reviewId);
        if (stats == null) {
            stats = new UserReviewStats();
            stats.setReviewId(reviewId);
            stats.setHelpfulCount(helpfulCount);
            stats.setReplyCount(replyCount);
            stats.setLikeCount(likeCount);
            return save(stats);
        } else {
            stats.setHelpfulCount(helpfulCount);
            stats.setReplyCount(replyCount);
            stats.setLikeCount(likeCount);
            return updateById(stats);
        }
    }
    
    @Override
    public boolean incrementHelpfulCount(Long reviewId) {
        UserReviewStats stats = getByReviewId(reviewId);
        if (stats == null) {
            stats = new UserReviewStats();
            stats.setReviewId(reviewId);
            stats.setHelpfulCount(1);
            stats.setReplyCount(0);
            stats.setLikeCount(0);
            return save(stats);
        } else {
            stats.setHelpfulCount(stats.getHelpfulCount() + 1);
            return updateById(stats);
        }
    }
    
    @Override
    public boolean incrementReplyCount(Long reviewId) {
        UserReviewStats stats = getByReviewId(reviewId);
        if (stats == null) {
            stats = new UserReviewStats();
            stats.setReviewId(reviewId);
            stats.setHelpfulCount(0);
            stats.setReplyCount(1);
            stats.setLikeCount(0);
            return save(stats);
        } else {
            stats.setReplyCount(stats.getReplyCount() + 1);
            return updateById(stats);
        }
    }
    
    @Override
    public boolean incrementLikeCount(Long reviewId) {
        UserReviewStats stats = getByReviewId(reviewId);
        if (stats == null) {
            stats = new UserReviewStats();
            stats.setReviewId(reviewId);
            stats.setHelpfulCount(0);
            stats.setReplyCount(0);
            stats.setLikeCount(1);
            return save(stats);
        } else {
            stats.setLikeCount(stats.getLikeCount() + 1);
            return updateById(stats);
        }
    }
    
    @Override
    public List<UserReviewStats> getTopHelpfulReviews(Integer limit) {
        UserReviewStats highHelpfulStats = userReviewStatsMapper.findHighHelpfulReviews(1);
        if (highHelpfulStats != null) {
            return lambdaQuery()
                    .orderByDesc(UserReviewStats::getHelpfulCount)
                    .last(limit != null ? "LIMIT " + limit : "")
                    .list();
        }
        return List.of();
    }
    
    @Override
    public List<UserReviewStats> getTopLikedReviews(Integer limit) {
        UserReviewStats highLikeStats = userReviewStatsMapper.findHighLikeReviews(1);
        if (highLikeStats != null) {
            return lambdaQuery()
                    .orderByDesc(UserReviewStats::getLikeCount)
                    .last(limit != null ? "LIMIT " + limit : "")
                    .list();
        }
        return List.of();
    }
}