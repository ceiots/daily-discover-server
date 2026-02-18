package com.dailydiscover.service;

import com.dailydiscover.model.ProductSku;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 商品SKU服务接口
 */
public interface ProductSkuService extends IService<ProductSku> {
    
    /**
     * 根据商品ID查询SKU列表
     */
    List<ProductSku> findByProductId(Long productId);
    
    /**
     * 根据SKU ID查询SKU信息
     */
    ProductSku findById(Long skuId);
    
    /**
     * 根据SKU编码查询SKU信息
     */
    ProductSku findBySkuCode(String skuCode);
    
    /**
     * 更新SKU库存
     */
    int updateStock(Long skuId, Integer quantity);
}