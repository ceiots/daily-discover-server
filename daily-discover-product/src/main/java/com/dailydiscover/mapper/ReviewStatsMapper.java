package com.dailydiscover.mapper;

import com.dailydiscover.model.ReviewStats;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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
    @Select("SELECT * FROM review_stats WHERE average_rating >= #{minRating} ORDER BY average_rating DESC LIMIT #{limit}")
    List<ReviewStats> findHighRatingStats(@Param("minRating") Double minRating, @Param("limit") Integer limit);
    
    /**
     * 查询热门商品的统计信息
     */
    @Select("SELECT * FROM review_stats ORDER BY total_reviews DESC LIMIT #{limit}")
    List<ReviewStats> findPopularStats(@Param("limit") Integer limit);
    
    /**
     * 批量更新评价统计
     */
    @Select("<script>" +
            "INSERT INTO review_stats (product_id, total_reviews, average_rating, rating_distribution) " +
            "VALUES " +
            "<foreach collection='statsList' item='stats' separator=','>" +
            "(#{stats.productId}, #{stats.totalReviews}, #{stats.averageRating}, #{stats.ratingDistribution})" +
            "</foreach> " +
            "ON DUPLICATE KEY UPDATE " +
            "total_reviews = VALUES(total_reviews), " +
            "average_rating = VALUES(average_rating), " +
            "rating_distribution = VALUES(rating_distribution)" +
            "</script>")
    int batchUpdateReviewStats(@Param("statsList") List<ReviewStats> statsList);
}