package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.Product;
import com.dailydiscover.dto.ProductBasicInfoDTO;
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
     * 根据ID查询商品基础信息（简化版，避免复杂关联查询）
     */
    @Select("SELECT " +
            "p.id, p.title, p.category_id, p.brand, p.model, " +
            "p.min_price, p.max_price, p.main_image_url, " +
            "COALESCE(pss.sales_count, 0) as sales_count, " +
            "COALESCE(rs.average_rating, 0) as average_rating, COALESCE(rs.total_reviews, 0) as total_reviews, " +
            "p.created_at, p.updated_at " +
            "FROM products p " +
            "LEFT JOIN product_sales_stats pss ON p.id = pss.product_id AND pss.time_granularity = 'daily' " +
            "LEFT JOIN review_stats rs ON p.id = rs.product_id " +
            "WHERE p.id = #{id} AND p.status = 1 AND p.is_deleted = 0")
    ProductBasicInfoDTO findBasicInfoById(@Param("id") Long id);
    
    /**
     * 根据ID列表批量查询商品基础信息（性能优化版）
     */
    @Select("<script>" +
            "SELECT DISTINCT " +
            "p.id, p.title, p.category_id, p.brand, p.model, " +
            "p.min_price, p.max_price, p.main_image_url, COALESCE(p.goods_slogan, '') as goods_slogan, " +
            "COALESCE(pss.sales_count, 0) as sales_count, COALESCE(pss.view_count, 0) as view_count, " +
            "COALESCE(rs.average_rating, 0) as average_rating, COALESCE(rs.total_reviews, 0) as total_reviews, " +
            "p.created_at, p.updated_at " +
            "FROM products p " +
            "LEFT JOIN (" +
            "    SELECT product_id, MAX(sales_count) as sales_count, MAX(view_count) as view_count " +
            "    FROM product_sales_stats " +
            "    WHERE time_granularity = 'daily' " +
            "    GROUP BY product_id" +
            ") pss ON p.id = pss.product_id " +
            "LEFT JOIN review_stats rs ON p.id = rs.product_id " +
            "WHERE p.id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "AND p.status = 1 AND p.is_deleted = 0" +
            "</script>")
    List<ProductBasicInfoDTO> findBasicInfoByIds(@Param("ids") List<Long> ids);
}