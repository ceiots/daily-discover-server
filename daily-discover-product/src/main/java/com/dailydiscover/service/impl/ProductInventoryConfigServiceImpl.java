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
    public ProductInventoryConfig getByInventoryId(Long inventoryId) {
        return productInventoryConfigMapper.selectById(inventoryId);
    }
    
    @Override
    public boolean updateAlertThreshold(Long inventoryId, Integer minStockLevel, Integer maxStockLevel) {
        ProductInventoryConfig config = getByInventoryId(inventoryId);
        if (config != null) {
            config.setMinStockLevel(minStockLevel);
            config.setMaxStockLevel(maxStockLevel);
            return productInventoryConfigMapper.updateById(config) > 0;
        }
        return false;
    }
    
    @Override
    public boolean updateSafetyStock(Long inventoryId, Integer safetyStock) {
        ProductInventoryConfig config = getByInventoryId(inventoryId);
        if (config != null) {
            config.setSafetyStock(safetyStock);
            return productInventoryConfigMapper.updateById(config) > 0;
        }
        return false;
    }
    
    @Override
    public List<ProductInventoryConfig> getInventoriesNeedRestock() {
        return productInventoryConfigMapper.findInventoriesNeedRestock();
    }
    
    @Override
    public boolean batchUpdateConfig(List<Long> inventoryIds, String configKey, String configValue) {
        return productInventoryConfigMapper.batchUpdateConfig(inventoryIds, configKey, configValue) > 0;
    }
    
    /**
     * 根据库存名称查询库存配置
     */
    public ProductInventoryConfig getByInventoryName(String inventoryName) {
        return productInventoryConfigMapper.findByInventoryName(inventoryName);
    }
    
    /**
     * 根据库存编码查询库存配置
     */
    public ProductInventoryConfig getByInventoryCode(String inventoryCode) {
        return productInventoryConfigMapper.findByInventoryCode(inventoryCode);
    }
    
    @Override
    public ProductInventoryConfig findById(Long id) {
        return getById(id);
    }
    
    @Override
    public List<ProductInventoryConfig> findAll() {
        return list();
    }
    
    @Override
    public boolean save(ProductInventoryConfig config) {
        return super.save(config);
    }
    
    @Override
    public boolean update(ProductInventoryConfig config) {
        return updateById(config);
    }
    
    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }
}