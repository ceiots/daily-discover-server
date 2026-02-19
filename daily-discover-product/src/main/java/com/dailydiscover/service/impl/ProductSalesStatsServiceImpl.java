package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ProductSalesStatsMapper;
import com.dailydiscover.model.ProductSalesStats;
import com.dailydiscover.service.ProductSalesStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductSalesStatsServiceImpl extends ServiceImpl<ProductSalesStatsMapper, ProductSalesStats> implements ProductSalesStatsService {
    
    @Autowired
    private ProductSalesStatsMapper productSalesStatsMapper;
    
    @Override
    public ProductSalesStats getSalesStatsByProductAndGranularity(Long productId, String timeGranularity) {
        return lambdaQuery()
                .eq(ProductSalesStats::getProductId, productId)
                .eq(ProductSalesStats::getTimeGranularity, timeGranularity)
                .one();
    }
    
    @Override
    public java.util.List<ProductSalesStats> getTopProducts(int limit) {
        return lambdaQuery().orderByDesc(ProductSalesStats::getSalesCount).last("LIMIT " + limit).list();
    }
    
    @Override
    public boolean updateSalesStats(Long productId, int salesCount, java.math.BigDecimal salesAmount) {
        ProductSalesStats stats = lambdaQuery().eq(ProductSalesStats::getProductId, productId).one();
        if (stats != null) {
            stats.setSalesCount(stats.getSalesCount() + salesCount);
            stats.setSalesAmount(stats.getSalesAmount().add(salesAmount));
            return updateById(stats);
        }
        return false;
    }
}