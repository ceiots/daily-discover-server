package com.dailydiscover.service;

import com.dailydiscover.model.ProductInventoryConfig;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 商品库存配置服务接口
 */
public interface ProductInventoryConfigService extends IService<ProductInventoryConfig> {
    
    /**
     * 根据库存ID查询库存配置
     */
    ProductInventoryConfig getByInventoryId(Long inventoryId);
    
    /**
     * 更新库存预警阈值
     */
    boolean updateAlertThreshold(Long inventoryId, Integer minStockLevel, Integer maxStockLevel);
    
    /**
     * 更新安全库存设置
     */
    boolean updateSafetyStock(Long inventoryId, Integer safetyStock);
    
    /**
     * 获取需要补货的库存列表
     */
    java.util.List<ProductInventoryConfig> getInventoriesNeedRestock();
    
    /**
     * 批量更新库存配置
     */
    boolean batchUpdateConfig(java.util.List<Long> inventoryIds, String configKey, String configValue);
    
    /**
     * 根据ID查询库存配置
     */
    ProductInventoryConfig findById(Long id);
    
    /**
     * 查询所有库存配置
     */
    java.util.List<ProductInventoryConfig> findAll();
    
    /**
     * 保存库存配置
     */
    boolean save(ProductInventoryConfig config);
    
    /**
     * 更新库存配置
     */
    boolean update(ProductInventoryConfig config);
    
    /**
     * 删除库存配置
     */
    boolean delete(Long id);
}