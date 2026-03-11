package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.ProductRecommendation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductRecommendationMapper extends BaseMapper<ProductRecommendation> {
    
    // ==================== 首页推荐场景 ====================
    
    /**
     * 今日发现推荐 - 第一步：查询推荐的商品ID集合
     */
    @Select("SELECT pr.recommended_product_id as item_id, MAX(pr.recommendation_score) as recommendation_score " +
            "FROM product_recommendations pr " +
            "INNER JOIN products p ON pr.recommended_product_id = p.id " +
            "WHERE pr.recommendation_type = 'daily_discovery' AND pr.is_active = true " +
            "AND (pr.user_id IS NULL OR pr.user_id = #{userId}) " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "GROUP BY pr.recommended_product_id " +
            "ORDER BY MAX(pr.recommendation_score) DESC " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<Map<String, Object>> findDailyDiscoverProductIds(@Param("userId") Long userId, @Param("limit") int limit, @Param("offset") int offset);

    /**
     * 今日发现推荐 - 支持用户意图过滤
     */
    @Select("SELECT DISTINCT pr.recommended_product_id as item_id, 'product' as item_type, p.title as title, p.main_image_url as image_url, ps.view_count, ps.avg_rating, COALESCE(p.goods_slogan, '') as goods_slogan, p.max_price as price, p.original_price as original_price " +
            "FROM product_recommendations pr " +
            "LEFT JOIN products p ON pr.recommended_product_id = p.id " +
            "LEFT JOIN product_sales_stats ps ON pr.recommended_product_id = ps.product_id " +
            "WHERE pr.recommendation_type = 'daily_discovery' AND pr.is_active = true " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "${userIntentCondition} " +
            "ORDER BY pr.recommendation_score DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> findDailyDiscoverProductsWithIntent(@Param("limit") int limit, @Param("userIntentCondition") String userIntentCondition);

    /**
     * 社区热榜推荐
     */
    @Select("SELECT p.id as item_id, p.title as title, p.main_image_url as image_url, ps.sales_count, ps.view_count, ps.avg_rating, COALESCE(p.goods_slogan, '') as goods_slogan " +
            "FROM products p " +
            "JOIN product_sales_stats ps ON p.id = ps.product_id " +
            "WHERE ps.time_granularity = 'daily' AND ps.stat_date = CURDATE() " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "ORDER BY ps.sales_count DESC, ps.view_count DESC LIMIT #{limit}")
    List<Map<String, Object>> findCommunityHotProducts(@Param("limit") int limit);
    
    /**
     * 个性化发现流推荐
     */
    @Select("SELECT pr.recommended_product_id as item_id, p.title as title, p.main_image_url as image_url, pr.recommendation_score, COALESCE(p.goods_slogan, '') as goods_slogan " +
            "FROM product_recommendations pr " +
            "JOIN products p ON pr.recommended_product_id = p.id " +
            "JOIN user_interest_profiles uip ON pr.user_id = uip.user_id " +
            "WHERE pr.user_id = #{userId} AND pr.recommendation_type = 'personalized' AND pr.is_active = true " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "ORDER BY pr.recommendation_score DESC LIMIT #{limit}")
    List<Map<String, Object>> findPersonalizedDiscoverProducts(@Param("userId") Long userId, @Param("limit") int limit);
    
    // ==================== 商品详情页推荐场景 ====================
    
    /**
     * 相似商品推荐
     */
    @Select("SELECT pr.recommended_product_id, pr.recommendation_score, p.title as name, p.main_image_url " +
            "FROM product_recommendations pr " +
            "JOIN products p ON pr.recommended_product_id = p.id " +
            "WHERE pr.product_id = #{productId} AND pr.recommendation_type = 'similar' AND pr.is_active = true " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "ORDER BY pr.recommendation_score DESC LIMIT #{limit}")
    List<Map<String, Object>> findSimilarProducts(@Param("productId") Long productId, @Param("limit") int limit);
    
    /**
     * 搭配商品推荐
     */
    @Select("SELECT pr.recommended_product_id, pr.recommendation_score, p.title as name, p.main_image_url " +
            "FROM product_recommendations pr " +
            "JOIN products p ON pr.recommended_product_id = p.id " +
            "WHERE pr.product_id = #{productId} AND pr.recommendation_type = 'complementary' AND pr.is_active = true " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "ORDER BY pr.recommendation_score DESC LIMIT #{limit}")
    List<Map<String, Object>> findComplementaryProducts(@Param("productId") Long productId, @Param("limit") int limit);
    
    /**
     * 价格敏感推荐
     */
    @Select("SELECT pr.recommended_product_id, pr.recommendation_score, p.title as name, p.main_image_url, p.max_price " +
            "FROM product_recommendations pr " +
            "JOIN products p ON pr.recommended_product_id = p.id " +
            "WHERE pr.product_id = #{productId} AND pr.recommendation_type = 'price_sensitive' " +
            "AND pr.is_active = true " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "AND p.max_price <= #{currentPrice} * 1.2 AND p.max_price >= #{currentPrice} * 0.8 " +
            "ORDER BY pr.recommendation_score DESC LIMIT #{limit}")
    List<Map<String, Object>> findPriceSensitiveProducts(@Param("productId") Long productId, @Param("currentPrice") Double currentPrice, @Param("limit") int limit);
    
    // ==================== 搜索页面推荐场景 ====================
    
    /**
     * 搜索结果为空时的兜底推荐
     */
    @Select("SELECT p.id, p.title as name, p.main_image_url, ps.sales_count, ps.avg_rating " +
            "FROM products p " +
            "JOIN product_sales_stats ps ON p.id = ps.product_id " +
            "JOIN product_tag_relations ptr ON p.id = ptr.product_id " +
            "JOIN product_tags pt ON ptr.tag_id = pt.id " +
            "WHERE ps.time_granularity = 'daily' AND ps.stat_date = CURDATE() " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "AND pt.tag_name IN (SELECT tag_name FROM product_tags WHERE tag_type = 'trending') " +
            "ORDER BY ps.sales_count DESC, ps.view_count DESC LIMIT #{limit}")
    List<Map<String, Object>> findFallbackRecommendations(@Param("limit") int limit);
    
    /**
     * 筛选条件推荐（基于搜索词的属性）
     */
    @Select("SELECT DISTINCT pt.name as tag_name, COUNT(*) as product_count " +
            "FROM product_tags pt " +
            "JOIN product_tag_relations ptr ON pt.id = ptr.tag_id " +
            "JOIN products p ON ptr.product_id = p.id " +
            "WHERE p.title LIKE CONCAT('%', #{keyword}, '%') " +
            "AND pt.tag_type IN ('brand', 'category', 'attribute') " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "GROUP BY pt.name " +
            "ORDER BY COUNT(*) DESC LIMIT #{limit}")
    List<Map<String, Object>> findRelatedKeywords(@Param("keyword") String keyword, @Param("limit") int limit);
    
    /**
     * 查询意图识别（品牌/品类/属性）
     */
    @Select("SELECT " +
            "CASE " +
            "    WHEN EXISTS (SELECT 1 FROM product_tags WHERE name = #{keyword} AND tag_type = 'brand') THEN 'brand' " +
            "    WHEN EXISTS (SELECT 1 FROM product_tags WHERE name = #{keyword} AND tag_type = 'category') THEN 'category' " +
            "    ELSE 'keyword' " +
            "END as query_intent")
    Map<String, Object> detectQueryIntent(@Param("keyword") String keyword);
    
    // ==================== 购物车页面推荐场景 ====================
    
    /**
     * 凑单推荐（满减优惠）
     */
    @Select("SELECT p.id, p.title as name, p.main_image_url, p.max_price, ps.sales_count, ps.avg_rating " +
            "FROM products p " +
            "JOIN product_sales_stats ps ON p.id = ps.product_id " +
            "WHERE p.max_price <= #{remainingAmount} " +
            "AND ps.time_granularity = 'daily' AND ps.stat_date = CURDATE() " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "ORDER BY ps.sales_count DESC, ps.avg_rating DESC LIMIT #{limit}")
    List<Map<String, Object>> findAddOnProducts(@Param("remainingAmount") Double remainingAmount, @Param("limit") int limit);
    
    /**
     * 购物车内商品的价格变化监控
     */
    @Select("SELECT p.id, p.title as name, pp.current_price, pp.previous_price, " +
            "(pp.previous_price - pp.current_price) as price_drop " +
            "FROM products p " +
            "JOIN product_prices pp ON p.id = pp.product_id " +
            "WHERE p.id IN (${productIds}) " +
            "AND pp.current_price < pp.previous_price " +
            "AND pp.price_updated_at > DATE_SUB(NOW(), INTERVAL 7 DAY) " +
            "ORDER BY price_drop DESC")
    List<Map<String, Object>> findPriceDrops(@Param("productIds") String productIds);
    
    /**
     * 库存稀缺性提示
     */
    @Select("SELECT p.id, p.title as name, pi.current_stock, pi.low_stock_threshold, " +
            "CASE " +
            "    WHEN pi.current_stock <= pi.low_stock_threshold THEN 'low_stock' " +
            "    WHEN pi.current_stock <= 5 THEN 'very_low_stock' " +
            "    ELSE 'normal_stock' " +
            "END as stock_status " +
            "FROM products p " +
            "JOIN product_inventory pi ON p.id = pi.product_id " +
            "WHERE pi.current_stock <= pi.low_stock_threshold " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "ORDER BY pi.current_stock ASC LIMIT #{limit}")
    List<Map<String, Object>> findLowStockProducts(@Param("limit") int limit);
    
    /**
     * 性价比替代品推荐（谨慎使用）
     */
    @Select("SELECT p.id, p.title as name, p.main_image_url, p.max_price, ps.avg_rating, ps.sales_count " +
            "FROM products p " +
            "JOIN product_sales_stats ps ON p.id = ps.product_id " +
            "JOIN product_tag_relations ptr1 ON p.id = ptr1.product_id " +
            "JOIN product_tag_relations ptr2 ON ptr1.tag_id = ptr2.tag_id " +
            "WHERE ptr2.product_id IN (${cartProductIds}) " +
            "AND p.max_price < (SELECT max_price FROM products WHERE id = #{targetProductId}) * 0.9 " +
            "AND ps.avg_rating >= (SELECT avg_rating FROM product_sales_stats WHERE product_id = #{targetProductId}) " +
            "AND p.id NOT IN (${cartProductIds}) " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "ORDER BY (ps.avg_rating * 0.6 + (1 - p.max_price/(SELECT max_price FROM products WHERE id = #{targetProductId})) * 0.4) DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> findAlternativeProducts(@Param("cartProductIds") String cartProductIds, @Param("targetProductId") Long targetProductId, @Param("limit") int limit);

    // ==================== 首页推荐四模块 ====================

    /**
     * 今日发现推荐（商品+内容混合）
     */
    @Select("SELECT DISTINCT pr.recommended_product_id as item_id, 'product' as item_type, p.title as title, p.main_image_url as image_url, ps.view_count, ps.avg_rating, COALESCE(p.goods_slogan, '') as goods_slogan " +
            "FROM product_recommendations pr " +
            "LEFT JOIN products p ON pr.recommended_product_id = p.id " +
            "LEFT JOIN product_sales_stats ps ON pr.recommended_product_id = ps.product_id " +
            "WHERE pr.recommendation_type = 'daily_discovery' AND pr.is_active = true AND (pr.user_id IS NULL OR pr.user_id = #{userId}) " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "ORDER BY pr.recommendation_score DESC, ps.view_count DESC " +
            "LIMIT 4")
    List<Map<String, Object>> findDailyDiscoveryProducts(@Param("userId") Long userId);

    /**
     * 生活场景推荐 - 用户专属推荐（基于时间、日期、季节）
     */
    @Select("SELECT sr.recommendation_title, sr.recommendation_description, " +
            "sr.time_period, sr.day_type, sr.season_type, " +
            "sr.recommended_products, sr.approval_status " +
            "FROM scenario_recommendations sr " +
            "WHERE sr.user_id = #{userId} " +
            "AND sr.time_period = #{timeContext} " +
            "AND sr.day_type = #{dayType} " +
            "AND sr.season_type = #{seasonType} " +
            "AND sr.approval_status = 'approved' " +
            "ORDER BY sr.created_at DESC " +
            "LIMIT 4")
    List<Map<String, Object>> findUserLifeScenarioRecommendations(@Param("userId") Long userId, @Param("timeContext") String timeContext, @Param("dayType") String dayType, @Param("seasonType") String seasonType);

    /**
     * 生活场景推荐 - 通用推荐（基于时间、日期、季节）
     */
    @Select("SELECT sr.recommendation_title, sr.recommendation_description, " +
            "sr.time_period, sr.day_type, sr.season_type, " +
            "sr.recommended_products, sr.approval_status " +
            "FROM scenario_recommendations sr " +
            "WHERE sr.user_id IS NULL " +
            "AND sr.time_period = #{timeContext} " +
            "AND sr.day_type = #{dayType} " +
            "AND sr.season_type = #{seasonType} " +
            "AND sr.approval_status = 'approved' " +
            "ORDER BY sr.created_at DESC " +
            "LIMIT 4")
    List<Map<String, Object>> findGeneralLifeScenarioRecommendations(@Param("timeContext") String timeContext, @Param("dayType") String dayType, @Param("seasonType") String seasonType);

    /**
     * 根据商品ID列表查询商品详细信息
     */
    @Select({"<script>",
            "SELECT id, title, main_image_url as imageUrl, min_price as price, max_price as originalPrice, " +
            "COALESCE(goods_slogan, '') as goodsSlogan, COALESCE(description, '') as description " +
            "FROM products " +
            "WHERE id IN " +
            "<foreach collection='productIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " AND status = 1 AND is_deleted = 0" +
            "</script>"})
    List<Map<String, Object>> findProductsByIds(@Param("productIds") List<Long> productIds);

    /**
     * 社区热榜推荐（客观排名，不关联用户）
     */
    @Select("SELECT p.id as item_id, p.title as title, p.main_image_url as image_url, " +
            "ps.sales_count, ps.view_count, ps.avg_rating, COALESCE(p.goods_slogan, '') as goods_slogan " +
            "FROM products p " +
            "JOIN product_sales_stats ps ON p.id = ps.product_id " +
            "WHERE ps.time_granularity = 'daily' AND ps.stat_date = CURDATE() " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "ORDER BY ps.sales_count DESC, ps.view_count DESC " +
            "LIMIT 6")
    List<Map<String, Object>> findCommunityHotList();

    /**
     * 个性化发现流推荐（支持未登录用户）
     */
    @Select("SELECT pr.recommended_product_id as item_id, p.title as title, " +
            "p.main_image_url as image_url, pr.recommendation_score, COALESCE(p.goods_slogan, '') as goods_slogan, " +
            "CASE " +
            "    WHEN pr.user_id IS NOT NULL THEN 'personalized' " +
            "    ELSE 'general' " +
            "END as recommendation_type " +
            "FROM product_recommendations pr " +
            "JOIN products p ON pr.recommended_product_id = p.id " +
            "LEFT JOIN user_interest_profiles uip ON pr.user_id = uip.user_id " +
            "WHERE pr.recommendation_type = 'personalized' AND pr.is_active = true " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "AND (pr.user_id = #{userId} OR pr.user_id IS NULL) " +
            "ORDER BY " +
            "    CASE " +
            "        WHEN pr.user_id = #{userId} THEN pr.recommendation_score * 1.5 " +
            "        ELSE pr.recommendation_score " +
            "    END DESC " +
            "LIMIT 8")
    List<Map<String, Object>> findPersonalizedDiscoveryStream(@Param("userId") Long userId);
     
    // ==================== 基础推荐方法 ====================
    
    /**
     * 根据商品ID和推荐类型查询推荐列表
     */
    @Select("SELECT pr.* FROM product_recommendations pr " +
            "JOIN products p ON pr.recommended_product_id = p.id " +
            "WHERE pr.product_id = #{productId} AND pr.recommendation_type = #{recommendationType} " +
            "AND pr.is_active = true AND p.status = 1 AND p.is_deleted = 0 " +
            "ORDER BY pr.recommendation_score DESC LIMIT 10")
    List<ProductRecommendation> findByProductIdAndType(@Param("productId") Long productId, @Param("recommendationType") String recommendationType);
    
    /**
     * 根据用户ID查询个性化推荐
     */
    @Select("SELECT pr.* FROM product_recommendations pr " +
            "JOIN products p ON pr.recommended_product_id = p.id " +
            "WHERE pr.user_id = #{userId} AND pr.is_active = true " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "ORDER BY pr.recommendation_score DESC LIMIT 20")
    List<ProductRecommendation> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据推荐类型查询热门推荐
     */
    @Select("SELECT pr.* FROM product_recommendations pr " +
            "JOIN products p ON pr.recommended_product_id = p.id " +
            "WHERE pr.recommendation_type = #{recommendationType} AND pr.is_active = true " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "ORDER BY pr.recommendation_score DESC LIMIT #{limit}")
    List<ProductRecommendation> findByTypeWithLimit(@Param("recommendationType") String recommendationType, @Param("limit") int limit);
    
    /**
     * 根据商品ID查询相关推荐
     */
    @Select("SELECT pr.* FROM product_recommendations pr " +
            "JOIN products p ON pr.recommended_product_id = p.id " +
            "WHERE pr.product_id = #{productId} AND pr.is_active = true " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "ORDER BY pr.recommendation_score DESC LIMIT #{limit}")
    List<ProductRecommendation> findRelatedProductsByProductId(@Param("productId") Long productId, @Param("limit") int limit);
    
    /**
     * 查询通用推荐（无用户ID的推荐）
     */
    @Select("SELECT pr.* FROM product_recommendations pr " +
            "JOIN products p ON pr.recommended_product_id = p.id " +
            "WHERE pr.user_id IS NULL AND pr.is_active = true " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "ORDER BY pr.recommendation_score DESC LIMIT #{limit}")
    List<ProductRecommendation> findGeneralRecommendations(@Param("limit") int limit);
    
    /**
     * 查询引导推荐商品（基于意图标签）
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
     * 查询引导推荐商品（基于意图标签和用户行为）
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
    List<Map<String, Object>> findGuidedProducts(@Param("sessionId") String sessionId, @Param("intentLabel") String intentLabel, @Param("limit") int limit, @Param("userId") Long userId);
}