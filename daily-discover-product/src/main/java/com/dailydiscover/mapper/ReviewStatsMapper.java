package com.dailydiscover.mapper;

import com.dailydiscover.model.ReviewStats;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 商品评价统计表 Mapper
 */
@Mapper
public interface ReviewStatsMapper extends BaseMapper<ReviewStats> {
    
    /**
     * 根据商品ID查询评价统计
     */
    @Select("SELECT * FROM review_stats WHERE product_id = #{productId}")
    ReviewStats findByProductId(@Param("productId") Long productId);
    
    /**
     * 查询高评分商品的统计信息
     */
    @Select("SELECT * FROM review_stats WHERE average_rating >= #{minRating} ORDER BY average_rating DESC")
    ReviewStats findHighRatingStats(@Param("minRating") Double minRating);
    
    /**
     * 查询热门商品的统计信息
     */
    @Select("SELECT * FROM review_stats WHERE total_reviews >= #{minReviews} ORDER BY total_reviews DESC")
    ReviewStats findPopularStats(@Param("minReviews") Integer minReviews);
}