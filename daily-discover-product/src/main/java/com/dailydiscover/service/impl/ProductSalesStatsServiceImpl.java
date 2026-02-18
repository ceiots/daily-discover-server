package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ProductSalesStatsMapper;
import com.dailydiscover.model.ProductSalesStats;
import com.dailydiscover.service.ProductSalesStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class ProductSalesStatsServiceImpl extends ServiceImpl<ProductSalesStatsMapper, ProductSalesStats> implements ProductSalesStatsService {
    
    @Autowired
    private ProductSalesStatsMapper productSalesStatsMapper;
    
    @Override
    public ProductSalesStats getByProductId(Long productId) {
        return lambdaQuery().eq(ProductSalesStats::getProductId, productId).one();
    }
    
    @Override
    public boolean updateSalesStats(Long productId, Integer quantitySold, BigDecimal totalRevenue) {
        ProductSalesStats stats = getByProductId(productId);
        if (stats != null) {
            stats.setQuantitySold(stats.getQuantitySold() + quantitySold);
            stats.setTotalRevenue(stats.getTotalRevenue().add(totalRevenue));
            return updateById(stats);
        }
        return false;
    }
    
    @Override
    public boolean incrementSalesCount(Long productId) {
        ProductSalesStats stats = getByProductId(productId);
        if (stats != null) {
            stats.setSalesCount(stats.getSalesCount() + 1);
            return updateById(stats);
        }
        return false;
    }
    
    @Override
    public boolean updateAverageRating(Long productId, Double averageRating) {
        ProductSalesStats stats = getByProductId(productId);
        if (stats != null) {
            stats.setAverageRating(averageRating);
            return updateById(stats);
        }
        return false;
    }
    
    @Override
    public List<ProductSalesStats> getTopSellingProducts(Integer limit) {
        return lambdaQuery().orderByDesc(ProductSalesStats::getQuantitySold).last("LIMIT " + limit).list();
    }
    
    @Override
    public List<ProductSalesStats> getRevenueRanking(Integer limit) {
        return lambdaQuery().orderByDesc(ProductSalesStats::getTotalRevenue).last("LIMIT " + limit).list();
    }
    
    @Override
    public BigDecimal getTotalRevenueByProductId(Long productId) {
        ProductSalesStats stats = getByProductId(productId);
        return stats != null ? stats.getTotalRevenue() : BigDecimal.ZERO;
    }
    
    @Override
    public Integer getTotalQuantitySoldByProductId(Long productId) {
        ProductSalesStats stats = getByProductId(productId);
        return stats != null ? stats.getQuantitySold() : 0;
    }
}