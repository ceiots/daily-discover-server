package com.example.service;

import com.example.model.ProductPromotion;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 商品促销服务接口
 */
public interface ProductPromotionService {

    /**
     * 根据ID获取促销信息
     */
    ProductPromotion getById(Long id);
    
    /**
     * 根据商品ID获取所有促销信息
     */
    List<ProductPromotion> getByProductId(Long productId);
    
    /**
     * 根据SKU ID获取所有促销信息
     */
    List<ProductPromotion> getBySkuId(Long skuId);
    
    /**
     * 获取商品或SKU当前生效的促销
     */
    List<ProductPromotion> getActivePromotions(Long productId, Long skuId);
    
    /**
     * 创建商品促销
     */
    @Transactional
    ProductPromotion create(ProductPromotion promotion);
    
    /**
     * 更新商品促销
     */
    @Transactional
    boolean update(ProductPromotion promotion);
    
    /**
     * 删除商品促销
     */
    @Transactional
    boolean delete(Long id);
    
    /**
     * 更新促销状态
     */
    @Transactional
    boolean updateStatus(Long id, Integer status);
    
    /**
     * 根据商品ID删除所有促销
     */
    @Transactional
    boolean deleteByProductId(Long productId);
    
    /**
     * 根据SKU ID删除所有促销
     */
    @Transactional
    boolean deleteBySkuId(Long skuId);
} 