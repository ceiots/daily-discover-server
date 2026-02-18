package com.dailydiscover.mapper;

import com.dailydiscover.model.UserReviewStats;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户评价统计表 Mapper
 */
@Mapper
public interface UserReviewStatsMapper extends BaseMapper<UserReviewStats> {
    
    /**
     * 根据评价ID查询评价统计
     */
    @Select("SELECT * FROM user_review_stats WHERE review_id = #{reviewId}")
    UserReviewStats findByReviewId(@Param("reviewId") Long reviewId);
    
    /**
     * 查询高有用数量的评价统计
     */
    @Select("SELECT * FROM user_review_stats WHERE helpful_count >= #{minHelpfulCount} ORDER BY helpful_count DESC")
    UserReviewStats findHighHelpfulReviews(@Param("minHelpfulCount") Integer minHelpfulCount);
    
    /**
     * 查询高点赞数量的评价统计
     */
    @Select("SELECT * FROM user_review_stats WHERE like_count >= #{minLikeCount} ORDER BY like_count DESC")
    UserReviewStats findHighLikeReviews(@Param("minLikeCount") Integer minLikeCount);
}