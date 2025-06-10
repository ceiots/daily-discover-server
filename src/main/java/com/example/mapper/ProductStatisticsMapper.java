package com.example.mapper;

import org.apache.ibatis.annotations.*;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.ProductStatistics;

import java.util.List;

@Mapper
public interface ProductStatisticsMapper extends BaseMapper<ProductStatistics> {

    @Insert("INSERT INTO product_statistics(product_id, view_count, favorite_count, share_count, " +
            "rating, review_count, positive_reviews, negative_reviews, last_active_time, daily_views_trend) " +
            "VALUES(#{productId}, #{viewCount}, #{favoriteCount}, #{shareCount}, " +
            "#{rating}, #{reviewCount}, #{positiveReviews}, #{negativeReviews}, " +
            "#{lastActiveTime}, #{dailyViewsTrend,typeHandler=com.example.util.JsonTypeHandler})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductStatistics statistics);
    
    @Select("SELECT * FROM product_statistics WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "viewCount", column = "view_count"),
        @Result(property = "favoriteCount", column = "favorite_count"),
        @Result(property = "shareCount", column = "share_count"),
        @Result(property = "rating", column = "rating"),
        @Result(property = "reviewCount", column = "review_count"),
        @Result(property = "positiveReviews", column = "positive_reviews"),
        @Result(property = "negativeReviews", column = "negative_reviews"),
        @Result(property = "lastActiveTime", column = "last_active_time"),
        @Result(property = "dailyViewsTrend", column = "daily_views_trend", 
                typeHandler = com.example.common.util.JsonTypeHandler.class),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    ProductStatistics findById(@Param("id") Long id);
    
    @Select("SELECT * FROM product_statistics WHERE product_id = #{productId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "viewCount", column = "view_count"),
        @Result(property = "favoriteCount", column = "favorite_count"),
        @Result(property = "shareCount", column = "share_count"),
        @Result(property = "rating", column = "rating"),
        @Result(property = "reviewCount", column = "review_count"),
        @Result(property = "positiveReviews", column = "positive_reviews"),
        @Result(property = "negativeReviews", column = "negative_reviews"),
        @Result(property = "lastActiveTime", column = "last_active_time"),
        @Result(property = "dailyViewsTrend", column = "daily_views_trend", 
                typeHandler = com.example.common.util.JsonTypeHandler.class),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    ProductStatistics findByProductId(@Param("productId") Long productId);
    
    @Update("UPDATE product_statistics SET " +
            "view_count = #{viewCount}, " +
            "favorite_count = #{favoriteCount}, " +
            "share_count = #{shareCount}, " +
            "rating = #{rating}, " +
            "review_count = #{reviewCount}, " +
            "positive_reviews = #{positiveReviews}, " +
            "negative_reviews = #{negativeReviews}, " +
            "last_active_time = #{lastActiveTime}, " +
            "daily_views_trend = #{dailyViewsTrend,typeHandler=com.example.util.JsonTypeHandler} " +
            "WHERE product_id = #{productId}")
    int updateByProductId(ProductStatistics statistics);
    
    @Update("UPDATE product_statistics SET " +
            "view_count = view_count + 1, " +
            "last_active_time = NOW() " +
            "WHERE product_id = #{productId}")
    int incrementViewCount(@Param("productId") Long productId);
    
    @Update("UPDATE product_statistics SET " +
            "favorite_count = favorite_count + #{increment} " +
            "WHERE product_id = #{productId}")
    int updateFavoriteCount(@Param("productId") Long productId, @Param("increment") int increment);
    
    @Update("UPDATE product_statistics SET " +
            "share_count = share_count + 1 " +
            "WHERE product_id = #{productId}")
    int incrementShareCount(@Param("productId") Long productId);
    
    @Update("UPDATE product_statistics SET " +
            "rating = #{rating}, " +
            "review_count = #{reviewCount}, " +
            "positive_reviews = #{positiveReviews}, " +
            "negative_reviews = #{negativeReviews} " +
            "WHERE product_id = #{productId}")
    int updateReviewStats(@Param("productId") Long productId,
                          @Param("rating") Double rating,
                          @Param("reviewCount") Integer reviewCount,
                          @Param("positiveReviews") Integer positiveReviews,
                          @Param("negativeReviews") Integer negativeReviews);
    
    @Select("SELECT * FROM product_statistics " +
            "ORDER BY view_count DESC LIMIT #{limit}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "viewCount", column = "view_count"),
        @Result(property = "favoriteCount", column = "favorite_count"),
        @Result(property = "shareCount", column = "share_count"),
        @Result(property = "rating", column = "rating"),
        @Result(property = "reviewCount", column = "review_count"),
        @Result(property = "positiveReviews", column = "positive_reviews"),
        @Result(property = "negativeReviews", column = "negative_reviews"),
        @Result(property = "lastActiveTime", column = "last_active_time"),
        @Result(property = "dailyViewsTrend", column = "daily_views_trend", 
                typeHandler = com.example.common.util.JsonTypeHandler.class),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<ProductStatistics> findMostViewed(@Param("limit") int limit);
    
    @Select("SELECT * FROM product_statistics " +
            "WHERE review_count > 0 " +
            "ORDER BY rating DESC, review_count DESC LIMIT #{limit}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "viewCount", column = "view_count"),
        @Result(property = "favoriteCount", column = "favorite_count"),
        @Result(property = "shareCount", column = "share_count"),
        @Result(property = "rating", column = "rating"),
        @Result(property = "reviewCount", column = "review_count"),
        @Result(property = "positiveReviews", column = "positive_reviews"),
        @Result(property = "negativeReviews", column = "negative_reviews"),
        @Result(property = "lastActiveTime", column = "last_active_time"),
        @Result(property = "dailyViewsTrend", column = "daily_views_trend", 
                typeHandler = com.example.common.util.JsonTypeHandler.class),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<ProductStatistics> findHighestRated(@Param("limit") int limit);
} 