package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ProductInventoryConfigMapper;
import com.dailydiscover.model.ProductInventoryConfig;
import com.dailydiscover.service.ProductInventoryConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductInventoryConfigServiceImpl extends ServiceImpl<ProductInventoryConfigMapper, ProductInventoryConfig> implements ProductInventoryConfigService {
    
    @Autowired
    private ProductInventoryConfigMapper productInventoryConfigMapper;
    
    @Override
    public ProductInventoryConfig getByProductId(Long productId) {
        return lambdaQuery().eq(ProductInventoryConfig::getProductId, productId).one();
    }
    
    @Override
    public boolean updateAlertThreshold(Long productId, Integer lowStockThreshold, Integer outOfStockThreshold) {
        ProductInventoryConfig config = getByProductId(productId);
        if (config != null) {
            config.setLowStockThreshold(lowStockThreshold);
            config.setOutOfStockThreshold(outOfStockThreshold);
            return updateById(config);
        }
        return false;
    }
    
    @Override
    public boolean updateInventoryStrategy(Long productId, String strategyType, String strategyParams) {
        ProductInventoryConfig config = getByProductId(productId);
        if (config != null) {
            config.setStrategyType(strategyType);
            config.setStrategyParams(strategyParams);
            return updateById(config);
        }
        return false;
    }
    
    @Override
    public boolean toggleAlertEnabled(Long productId, Boolean enabled) {
        ProductInventoryConfig config = getByProductId(productId);
        if (config != null) {
            config.setAlertEnabled(enabled);
            return updateById(config);
        }
        return false;
    }
    
    @Override
    public boolean updateSafetyStock(Long productId, Integer safetyStock) {
        ProductInventoryConfig config = getByProductId(productId);
        if (config != null) {
            config.setSafetyStock(safetyStock);
            return updateById(config);
        }
        return false;
    }
    
    @Override
    public List<ProductInventoryConfig> getProductsNeedAlert() {
        return lambdaQuery()
                .eq(ProductInventoryConfig::getAlertEnabled, true)
                .list();
    }
    
    @Override
    public boolean batchUpdateConfig(List<Long> productIds, String configKey, String configValue) {
        return lambdaUpdate()
                .in(ProductInventoryConfig::getProductId, productIds)
                .setSql(configKey + " = " + configValue)
                .update();
    }
}