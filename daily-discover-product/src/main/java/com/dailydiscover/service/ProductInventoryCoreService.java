package com.dailydiscover.service;

import com.dailydiscover.model.ProductInventoryCore;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 商品核心库存服务接口
 */
public interface ProductInventoryCoreService extends IService<ProductInventoryCore> {
    
    /**
     * 根据商品ID查询库存信息
     */
    ProductInventoryCore getByProductId(Long productId);
    
    /**
     * 根据SKU ID查询库存信息
     */
    ProductInventoryCore getBySkuId(Long skuId);
    
    /**
     * 更新商品库存数量
     */
    boolean updateStockQuantity(Long productId, Integer quantity);
    
    /**
     * 增加商品库存
     */
    boolean increaseStock(Long productId, Integer quantity);
    
    /**
     * 减少商品库存
     */
    boolean decreaseStock(Long productId, Integer quantity);
    
    /**
     * 检查商品库存是否充足
     */
    boolean checkStockSufficient(Long productId, Integer requiredQuantity);
    
    /**
     * 获取低库存商品列表
     */
    java.util.List<ProductInventoryCore> getLowStockProducts(Integer threshold);
    
    /**
     * 获取缺货商品列表
     */
    java.util.List<ProductInventoryCore> getOutOfStockProducts();
}