package com.dailydiscover.service;

import com.dailydiscover.model.ProductSalesStats;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 商品销售统计服务接口
 */
public interface ProductSalesStatsService extends IService<ProductSalesStats> {
    
    /**
     * 根据商品ID和时间粒度获取销售统计
     * @param productId 商品ID
     * @param timeGranularity 时间粒度
     * @return 销售统计
     */
    ProductSalesStats getSalesStatsByProductAndGranularity(Long productId, String timeGranularity);
    
    /**
     * 获取热门商品排名
     * @param limit 限制数量
     * @return 热门商品列表
     */
    java.util.List<ProductSalesStats> getTopProducts(int limit);
    
    /**
     * 更新商品销售统计
     * @param productId 商品ID
     * @param salesCount 销量
     * @param salesAmount 销售额
     * @return 是否成功
     */
    boolean updateSalesStats(Long productId, int salesCount, java.math.BigDecimal salesAmount);
}