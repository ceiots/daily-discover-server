package com.dailydiscover.service;

import com.dailydiscover.model.ProductInventoryConfig;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 商品库存配置服务接口
 */
public interface ProductInventoryConfigService extends IService<ProductInventoryConfig> {
    
    /**
     * 根据商品ID查询库存配置
     */
    ProductInventoryConfig getByProductId(Long productId);
    
    /**
     * 更新库存预警阈值
     */
    boolean updateAlertThreshold(Long productId, Integer lowStockThreshold, Integer outOfStockThreshold);
    
    /**
     * 更新库存策略
     */
    boolean updateInventoryStrategy(Long productId, String strategyType, String strategyParams);
    
    /**
     * 启用/禁用库存预警
     */
    boolean toggleAlertEnabled(Long productId, Boolean enabled);
    
    /**
     * 更新安全库存设置
     */
    boolean updateSafetyStock(Long productId, Integer safetyStock);
    
    /**
     * 获取需要预警的商品列表
     */
    java.util.List<ProductInventoryConfig> getProductsNeedAlert();
    
    /**
     * 批量更新库存配置
     */
    boolean batchUpdateConfig(java.util.List<Long> productIds, String configKey, String configValue);
}