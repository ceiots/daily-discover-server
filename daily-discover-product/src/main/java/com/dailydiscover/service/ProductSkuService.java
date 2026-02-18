package com.dailydiscover.service;

import com.dailydiscover.model.ProductSku;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 商品SKU服务接口
 */
public interface ProductSkuService extends IService<ProductSku> {
    
    /**
     * 根据商品ID查询SKU列表
     * @param productId 商品ID
     * @return SKU列表
     */
    java.util.List<ProductSku> getSkusByProductId(Long productId);
    
    /**
     * 根据SKU ID查询SKU信息
     * @param skuId SKU ID
     * @return SKU信息
     */
    ProductSku getSkuById(Long skuId);
    
    /**
     * 更新SKU价格
     * @param skuId SKU ID
     * @param price 新价格
     * @return 是否成功
     */
    boolean updateSkuPrice(Long skuId, java.math.BigDecimal price);
}