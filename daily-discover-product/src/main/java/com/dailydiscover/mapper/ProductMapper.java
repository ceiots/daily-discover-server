package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.Product;
import com.dailydiscover.model.dto.ProductBasicInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    
    /**
     * 查询所有启用状态的商品
     */
    @Select("SELECT * FROM products WHERE status = 'active' ORDER BY created_at DESC")
    List<Product> findAllActive();
    
    /**
     * 根据商家ID查询商品
     */
    @Select("SELECT * FROM products WHERE seller_id = #{sellerId} AND status = 'active'")
    List<Product> findBySellerId(@Param("sellerId") Long sellerId);
    
    /**
     * 根据分类ID查询商品
     */
    @Select("SELECT * FROM products WHERE category_id = #{categoryId} AND status = 'active'")
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);
    
    /**
     * 查询热门商品
     */
    @Select("SELECT * FROM products WHERE is_hot = true AND status = 'active' ORDER BY total_sales DESC")
    List<Product> findHotProducts();
    
    /**
     * 查询新品（最近7天内创建的商品）
     */
    @Select("SELECT p.*, COALESCE(rs.average_rating, 0) as rating, COALESCE(rs.total_reviews, 0) as reviews, COALESCE(pss.sales_count, 0) as sales " +
            "FROM products p " +
            "LEFT JOIN review_stats rs ON p.id = rs.product_id " +
            "LEFT JOIN product_sales_stats pss ON p.id = pss.product_id AND pss.time_granularity = 'daily' " +
            "WHERE p.status = 1 AND p.is_deleted = 0 AND p.created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
            "ORDER BY p.created_at DESC")
    List<Product> findNewProducts();
    
    /**
     * 查询推荐商品
     */
    @Select("SELECT * FROM products WHERE is_recommended = true AND status = 'active' ORDER BY rating DESC")
    List<Product> findRecommendedProducts();
    
    /**
     * 根据ID查询商品基础信息（包含所有首屏显示所需信息）
     */
    @Select("SELECT " +
            "p.id, p.seller_id, p.title, p.category_id, p.brand, p.model, " +
            "p.min_price, p.max_price, p.main_image_url, " +
            "pd.description as recommendation, " +
            "p.min_price as current_price, p.max_price as original_price, " +
            "CASE WHEN p.max_price > p.min_price THEN ROUND((p.max_price - p.min_price) / p.max_price * 100, 0) ELSE 0 END as discount, " +
            "COALESCE(pss.sales_count, 0) as sales, " +
            "CASE WHEN COALESCE(pss.sales_count, 0) > 100 THEN '热销中' WHEN COALESCE(pss.sales_count, 0) > 50 THEN '销量不错' ELSE '新品上架' END as urgency_hint, " +
            "COALESCE(rs.average_rating, 0) as rating, COALESCE(rs.total_reviews, 0) as reviews, " +
            "s.name as seller_name, s.rating as seller_rating, " +
            "p.created_at, p.updated_at " +
            "FROM products p " +
            "LEFT JOIN product_details pd ON p.id = pd.product_id AND pd.media_type = 1 " +
            "LEFT JOIN product_sales_stats pss ON p.id = pss.product_id AND pss.time_granularity = 'daily' " +
            "LEFT JOIN review_stats rs ON p.id = rs.product_id " +
            "LEFT JOIN sellers s ON p.seller_id = s.id " +
            "WHERE p.id = #{id} AND p.status = 1 AND p.is_deleted = 0")
    ProductBasicInfoDTO findBasicInfoById(@Param("id") Long id);
}