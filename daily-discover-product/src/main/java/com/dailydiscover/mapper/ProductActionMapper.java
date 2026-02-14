package com.dailydiscover.mapper;

import org.apache.ibatis.annotations.*;
import java.util.Map;

@Mapper
public interface ProductActionMapper {
    
    @Insert("INSERT INTO user_favorites (user_id, product_id, created_at) " +
            "VALUES (#{userId}, #{productId}, CURRENT_TIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addFavorite(Map<String, Object> favorite);
    
    @Delete("DELETE FROM user_favorites WHERE user_id = #{userId} AND product_id = #{productId}")
    void removeFavorite(@Param("userId") String userId, @Param("productId") Long productId);
    
    @Select("SELECT COUNT(*) FROM user_favorites WHERE user_id = #{userId} AND product_id = #{productId}")
    Integer getFavoriteStatus(@Param("userId") String userId, @Param("productId") Long productId);
    
    @Insert("INSERT INTO product_shares (product_id, share_count, created_at) " +
            "VALUES (#{productId}, 1, CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE share_count = share_count + 1")
    void incrementShareCount(Long productId);
    
    @Select("SELECT share_count FROM product_shares WHERE product_id = #{productId}")
    Integer getShareCount(Long productId);
    
    @Insert("INSERT INTO user_view_history (user_id, product_id, viewed_at) " +
            "VALUES (#{userId}, #{productId}, CURRENT_TIMESTAMP)")
    void addToViewHistory(@Param("userId") String userId, @Param("productId") Long productId);
    
    @Select("SELECT product_id, MAX(viewed_at) as last_viewed FROM user_view_history " +
            "WHERE user_id = #{userId} GROUP BY product_id ORDER BY last_viewed DESC LIMIT 50")
    Map<String, Object> getUserViewHistory(String userId);
    
    // 新增方法 - 用于ProductActionServiceImpl
    @Select("SELECT COUNT(*) > 0 FROM user_favorites WHERE user_id = #{userId} AND product_id = #{productId}")
    boolean isProductFavorited(@Param("userId") String userId, @Param("productId") Long productId);
    
    @Delete("DELETE FROM user_favorites WHERE user_id = #{userId} AND product_id = #{productId}")
    void removeFromFavorites(@Param("userId") String userId, @Param("productId") Long productId);
    
    @Insert("INSERT INTO user_favorites (user_id, product_id, created_at) " +
            "VALUES (#{userId}, #{productId}, CURRENT_TIMESTAMP)")
    void addToFavorites(@Param("userId") String userId, @Param("productId") Long productId);
    
    @Insert("INSERT INTO product_share_logs (user_id, product_id, shared_at) " +
            "VALUES (#{userId}, #{productId}, CURRENT_TIMESTAMP)")
    void recordProductShare(@Param("userId") String userId, @Param("productId") Long productId);
    
    @Select("SELECT " +
            "COUNT(*) as totalShares, " +
            "SUM(CASE WHEN DATE(shared_at) = CURDATE() THEN 1 ELSE 0 END) as todayShares, " +
            "(SELECT share_count FROM product_shares WHERE product_id = #{productId}) as shareCount " +
            "FROM product_share_logs WHERE product_id = #{productId}")
    Map<String, Object> getProductShareStats(@Param("productId") Long productId);
}