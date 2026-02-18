package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.ProductSalesStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductSalesStatsMapper extends BaseMapper<ProductSalesStats> {
    
    /**
     * 查询热门商品
     */
    @Select("SELECT * FROM product_sales_stats WHERE time_granularity = 'daily' AND is_deleted = 0 ORDER BY sales_volume DESC LIMIT #{limit}")
    List<ProductSalesStats> findTrendingProducts(@Param("limit") int limit);
    
    /**
     * 根据商品ID和时间粒度查询销售统计
     */
    @Select("SELECT * FROM product_sales_stats WHERE product_id = #{productId} AND time_granularity = #{timeGranularity} AND is_deleted = 0 ORDER BY stat_date DESC")
    List<ProductSalesStats> findByProductIdAndGranularity(@Param("productId") Long productId, @Param("timeGranularity") String timeGranularity);
    
    /**
     * 查询热销商品
     */
    @Select("SELECT * FROM product_sales_stats WHERE time_granularity = 'daily' AND is_deleted = 0 ORDER BY sales_volume DESC LIMIT #{limit}")
    List<ProductSalesStats> findTopSellingProducts(@Param("limit") int limit);
    
    /**
     * 查询商品销售趋势
     */
    @Select("SELECT * FROM product_sales_stats WHERE product_id = #{productId} AND time_granularity = #{timeGranularity} AND stat_date >= DATE_SUB(NOW(), INTERVAL 30 DAY) AND is_deleted = 0 ORDER BY stat_date ASC")
    List<ProductSalesStats> findSalesTrend(@Param("productId") Long productId, @Param("timeGranularity") String timeGranularity);
}