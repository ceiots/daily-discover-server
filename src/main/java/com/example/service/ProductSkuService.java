package com.example.service;

import com.example.model.ProductSku;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 商品SKU服务接口
 */
@Service
public interface ProductSkuService {

    /**
     * 根据商品ID获取所有SKU
     * @param productId 商品ID
     * @return SKU列表
     */
    List<ProductSku> getSkusByProductId(Long productId);
    
    /**
     * 根据SKU ID获取SKU详情
     * @param skuId SKU ID
     * @return SKU详情
     */
    ProductSku getSkuById(Long skuId);
    
    /**
     * 根据商品ID和规格获取SKU
     * @param productId 商品ID
     * @param specifications 规格键值对
     * @return 匹配的SKU
     */
    ProductSku getSkuByProductIdAndSpecs(Long productId, Map<String, String> specifications);
    
    /**
     * 创建或更新SKU
     * @param sku SKU对象
     * @return 保存后的SKU
     */
    @Transactional
    ProductSku saveOrUpdateSku(ProductSku sku);
    
    /**
     * 批量保存SKU
     * @param skus SKU列表
     * @return 保存后的SKU列表
     */
    @Transactional
    List<ProductSku> batchSaveOrUpdateSkus(List<ProductSku> skus);
    
    /**
     * 删除SKU
     * @param skuId SKU ID
     */
    @Transactional
    void deleteSku(Long skuId);
    
    /**
     * 删除商品的所有SKU
     * @param productId 商品ID
     */
    @Transactional
    void deleteSkusByProductId(Long productId);
} 