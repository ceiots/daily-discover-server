package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ProductSalesStatsMapper;
import com.dailydiscover.model.ProductSalesStats;
import com.dailydiscover.service.ProductSalesStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class ProductSalesStatsServiceImpl extends ServiceImpl<ProductSalesStatsMapper, ProductSalesStats> implements ProductSalesStatsService {
    
    @Autowired
    private ProductSalesStatsMapper productSalesStatsMapper;
    
    @Override
    public ProductSalesStats getSalesStatsByProductAndGranularity(Long productId, String timeGranularity) {
        // 使用 Mapper 方法查询
        java.util.List<ProductSalesStats> statsList = productSalesStatsMapper.findByProductIdAndGranularity(productId, timeGranularity);
        return statsList != null && !statsList.isEmpty() ? statsList.get(0) : null;
    }
    
    @Override
    public java.util.List<ProductSalesStats> getTopProducts(int limit) {
        // 使用 Mapper 方法查询热门商品
        return productSalesStatsMapper.findTopSellingProducts(limit);
    }
    
    @Override
    public boolean updateSalesStats(Long productId, int salesCount, java.math.BigDecimal salesAmount) {
        // 使用 Mapper 方法查询并更新
        ProductSalesStats stats = lambdaQuery().eq(ProductSalesStats::getProductId, productId).one();
        if (stats != null) {
            stats.setSalesCount(stats.getSalesCount() + salesCount);
            stats.setSalesAmount(stats.getSalesAmount().add(salesAmount));
            return updateById(stats);
        }
        return false;
    }
}