package com.dailydiscover.recommendation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.recommendation.model.ProductRecommendation;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductRecommendationMapper extends BaseMapper<ProductRecommendation> {
    
    // ==================== 场景推荐相关方法 ====================
    
    /**
     * 根据日期和时段获取场景推荐
     */
    @Select("SELECT sr.id, sr.target_date, sr.time_period, sr.scene_sentence, sr.scene_subtitle, " +
            "sr.cover_image, sr.is_active, sr.created_at, sr.updated_at " +
            "FROM scene_recommendations sr " +
            "WHERE sr.target_date = #{targetDate} AND sr.time_period = #{timePeriod} " +
            "AND sr.is_active = true " +
            "ORDER BY sr.created_at DESC " +
            "LIMIT 1")
    Map<String, Object> findSceneByDateAndPeriod(@Param("targetDate") String targetDate, @Param("timePeriod") String timePeriod);
    
    /**
     * 获取场景关联的商品列表
     */
    @Select("SELECT spr.product_id, spr.product_reason, spr.display_order, " +
            "p.title, p.main_image_url, p.max_price, p.min_price, " +
            "COALESCE(p.goods_slogan, '') as goods_slogan " +
            "FROM scene_product_relation spr " +
            "JOIN products p ON spr.product_id = p.id " +
            "WHERE spr.scene_id = #{sceneId} AND spr.is_active = true " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "ORDER BY spr.display_order ASC")
    List<Map<String, Object>> findProductsBySceneId(@Param("sceneId") Long sceneId);
    
    /**
     * 获取今日场景推荐（基于当前日期和时间）
     */
    @Select("SELECT sr.id, sr.target_date, sr.time_period, sr.scene_sentence, sr.scene_subtitle, " +
            "sr.cover_image, sr.is_active " +
            "FROM scene_recommendations sr " +
            "WHERE sr.target_date = CURDATE() AND sr.time_period = #{timePeriod} " +
            "AND sr.is_active = true " +
            "ORDER BY sr.created_at DESC " +
            "LIMIT 1")
    Map<String, Object> findTodaySceneByPeriod(@Param("timePeriod") String timePeriod);
    
    /**
     * 获取未来几天的场景推荐（用于预览）
     */
    @Select("SELECT sr.target_date, sr.time_period, sr.scene_sentence, sr.scene_subtitle, " +
            "sr.cover_image " +
            "FROM scene_recommendations sr " +
            "WHERE sr.target_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL #{days} DAY) " +
            "AND sr.is_active = true " +
            "ORDER BY sr.target_date, sr.time_period")
    List<Map<String, Object>> findFutureScenes(@Param("days") int days);
    
    // ==================== 首页推荐四模块 ====================
    
    /**
     * 今日发现推荐 - 基于场景的商品推荐
     */
    @Select("SELECT p.id as item_id, p.title as title, p.main_image_url as image_url, " +
            "p.max_price as price, p.min_price as original_price, " +
            "COALESCE(p.goods_slogan, '') as goods_slogan, " +
            "spr.product_reason as recommendation_reason, " +
            "sr.scene_sentence as scene_description " +
            "FROM scene_recommendations sr " +
            "JOIN scene_product_relation spr ON sr.id = spr.scene_id " +
            "JOIN products p ON spr.product_id = p.id " +
            "WHERE sr.target_date = CURDATE() " +
            "AND sr.is_active = true AND spr.is_active = true " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "ORDER BY sr.time_period, spr.display_order " +
            "LIMIT #{limit}")
    List<Map<String, Object>> findDailyDiscoveryProducts(@Param("limit") int limit);
    
    /**
     * 生活场景推荐 - 基于时间和日期的场景推荐
     */
    @Select("SELECT sr.id as scene_id, sr.target_date, sr.time_period, " +
            "sr.scene_sentence as title, sr.scene_subtitle as description, " +
            "sr.cover_image, COUNT(spr.product_id) as product_count " +
            "FROM scene_recommendations sr " +
            "LEFT JOIN scene_product_relation spr ON sr.id = spr.scene_id AND spr.is_active = true " +
            "WHERE sr.target_date = #{targetDate} AND sr.time_period = #{timePeriod} " +
            "AND sr.is_active = true " +
            "GROUP BY sr.id, sr.target_date, sr.time_period, sr.scene_sentence, sr.scene_subtitle, sr.cover_image " +
            "ORDER BY sr.created_at DESC " +
            "LIMIT 4")
    List<Map<String, Object>> findLifeScenarioRecommendations(@Param("targetDate") String targetDate, @Param("timePeriod") String timePeriod);
    
    /**
     * 社区热榜推荐（基于销量统计）
     */
    @Select("SELECT p.id as item_id, p.title as title, p.main_image_url as image_url, " +
            "ps.sales_count, ps.view_count, ps.avg_rating, " +
            "COALESCE(p.goods_slogan, '') as goods_slogan, " +
            "p.max_price as price, p.min_price as original_price " +
            "FROM products p " +
            "JOIN product_sales_stats ps ON p.id = ps.product_id " +
            "WHERE ps.time_granularity = 'daily' AND ps.stat_date = CURDATE() " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "ORDER BY ps.sales_count DESC, ps.view_count DESC " +
            "LIMIT 6")
    List<Map<String, Object>> findCommunityHotList();
    
    /**
     * 个性化发现流推荐（基于用户兴趣）
     */
    @Select("SELECT p.id as item_id, p.title as title, p.main_image_url as image_url, " +
            "p.max_price as price, p.min_price as original_price, " +
            "COALESCE(p.goods_slogan, '') as goods_slogan, " +
            "'personalized' as recommendation_type " +
            "FROM products p " +
            "JOIN user_interest_profiles uip ON p.category_id = uip.category_id " +
            "WHERE uip.user_id = #{userId} " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "ORDER BY uip.interest_score DESC, p.max_price DESC " +
            "LIMIT 10")
    List<Map<String, Object>> findPersonalizedDiscoveryStream(@Param("userId") Long userId);
    
    // ==================== 商品详情页推荐场景 ====================
    
    /**
     * 相似商品推荐（基于商品标签）
     */
    @Select("SELECT p2.id, p2.title as name, p2.main_image_url, " +
            "p2.max_price as price, COUNT(*) as similarity_score " +
            "FROM products p1 " +
            "JOIN product_tag_relations ptr1 ON p1.id = ptr1.product_id " +
            "JOIN product_tag_relations ptr2 ON ptr1.tag_id = ptr2.tag_id " +
            "JOIN products p2 ON ptr2.product_id = p2.id " +
            "WHERE p1.id = #{productId} AND p2.id != #{productId} " +
            "AND p2.status = 1 AND p2.is_deleted = 0 " +
            "GROUP BY p2.id, p2.title, p2.main_image_url, p2.max_price " +
            "ORDER BY similarity_score DESC, p2.max_price ASC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> findSimilarProducts(@Param("productId") Long productId, @Param("limit") int limit);
    
    /**
     * 搭配商品推荐（基于场景关联）
     */
    @Select("SELECT DISTINCT p2.id, p2.title as name, p2.main_image_url, " +
            "p2.max_price as price, 'complementary' as recommendation_type " +
            "FROM products p1 " +
            "JOIN scene_product_relation spr1 ON p1.id = spr1.product_id " +
            "JOIN scene_recommendations sr ON spr1.scene_id = sr.id " +
            "JOIN scene_product_relation spr2 ON sr.id = spr2.scene_id " +
            "JOIN products p2 ON spr2.product_id = p2.id " +
            "WHERE p1.id = #{productId} AND p2.id != #{productId} " +
            "AND sr.is_active = true AND spr1.is_active = true AND spr2.is_active = true " +
            "AND p2.status = 1 AND p2.is_deleted = 0 " +
            "ORDER BY spr2.display_order ASC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> findComplementaryProducts(@Param("productId") Long productId, @Param("limit") int limit);
    
    /**
     * 价格敏感推荐
     */
    @Select("SELECT p.id, p.title as name, p.main_image_url, p.max_price, " +
            "ABS(p.max_price - #{currentPrice}) as price_difference " +
            "FROM products p " +
            "WHERE p.category_id = (SELECT category_id FROM products WHERE id = #{productId}) " +
            "AND p.max_price BETWEEN #{currentPrice} * 0.8 AND #{currentPrice} * 1.2 " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "AND p.id != #{productId} " +
            "ORDER BY price_difference ASC, p.max_price ASC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> findPriceSensitiveProducts(@Param("productId") Long productId, @Param("currentPrice") Double currentPrice, @Param("limit") int limit);
    
    // ==================== 引导推荐接口 ====================
    
    /**
     * 通用引导推荐商品
     */
    @Select("SELECT p.id, p.title, p.main_image_url as image_url, p.max_price as price, " +
            "p.max_price as current_price, p.min_price as original_price, " +
            "ps.avg_rating as rating, ps.review_count as reviews, " +
            "pt.name as category " +
            "FROM products p " +
            "LEFT JOIN product_sales_stats ps ON p.id = ps.product_id " +
            "LEFT JOIN product_tag_relations ptr ON p.id = ptr.product_id " +
            "LEFT JOIN product_tags pt ON ptr.tag_id = pt.id AND pt.tag_type = 'category' " +
            "WHERE p.status = 1 AND p.is_deleted = 0 " +
            "AND (ps.time_granularity = 'daily' OR ps.time_granularity IS NULL) " +
            "ORDER BY ps.sales_count DESC, ps.view_count DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> findGeneralGuidedProducts(@Param("limit") int limit);
    
    /**
     * 个性化引导推荐商品
     */
    @Select("SELECT p.id, p.title, p.main_image_url as image_url, p.max_price as price, " +
            "p.max_price as current_price, p.min_price as original_price, " +
            "ps.avg_rating as rating, ps.review_count as reviews, " +
            "pt.name as category, " +
            "uip.interest_score as recommendation_score " +
            "FROM products p " +
            "LEFT JOIN product_sales_stats ps ON p.id = ps.product_id " +
            "LEFT JOIN product_tag_relations ptr ON p.id = ptr.product_id " +
            "LEFT JOIN product_tags pt ON ptr.tag_id = pt.id AND pt.tag_type = 'category' " +
            "INNER JOIN user_interest_profiles uip ON p.category_id = uip.category_id " +
            "WHERE p.status = 1 AND p.is_deleted = 0 " +
            "AND uip.user_id = #{userId} " +
            "AND (ps.time_granularity = 'daily' OR ps.time_granularity IS NULL) " +
            "ORDER BY uip.interest_score DESC, ps.sales_count DESC, ps.view_count DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> findPersonalizedGuidedProducts(@Param("userId") Long userId, @Param("limit") int limit);
    
    // ==================== 基础推荐方法 ====================
    
    /**
     * 根据商品ID获取相关推荐
     */
    @Select("SELECT * FROM product_recommendations WHERE product_id = #{productId} AND is_active = true " +
            "ORDER BY recommendation_score DESC LIMIT #{limit}")
    List<ProductRecommendation> findRelatedProductsByProductId(@Param("productId") Long productId, @Param("limit") int limit);
    
    /**
     * 根据推荐类型获取推荐
     */
    @Select("SELECT * FROM product_recommendations WHERE recommendation_type = #{recommendationType} AND is_active = true " +
            "ORDER BY recommendation_score DESC LIMIT #{limit}")
    List<ProductRecommendation> findRecommendationsByType(@Param("recommendationType") String recommendationType, @Param("limit") int limit);
    
    /**
     * 获取通用推荐
     */
    @Select("SELECT * FROM product_recommendations WHERE user_id IS NULL AND is_active = true " +
            "ORDER BY recommendation_score DESC LIMIT #{limit}")
    List<ProductRecommendation> findGeneralRecommendations(@Param("limit") int limit);
    
    /**
     * 获取搭配推荐
     */
    @Select("SELECT * FROM product_recommendations WHERE product_id = #{productId} AND recommendation_type = 'complementary' AND is_active = true " +
            "ORDER BY recommendation_score DESC LIMIT #{limit}")
    List<ProductRecommendation> findComplementaryRecommendations(@Param("productId") Long productId, @Param("limit") int limit);
    
    /**
     * 插入推荐记录
     */
    @Insert("INSERT INTO product_recommendations (product_id, recommended_product_id, recommendation_type, recommendation_score, user_id, is_active) " +
            "VALUES (#{productId}, #{recommendedProductId}, #{recommendationType}, #{recommendationScore}, #{userId}, #{isActive})")
    int insertRecommendation(@Param("productId") Long productId, @Param("recommendedProductId") Long recommendedProductId, 
                            @Param("recommendationType") String recommendationType, @Param("recommendationScore") Double recommendationScore, 
                            @Param("userId") Long userId, @Param("isActive") Boolean isActive);
}