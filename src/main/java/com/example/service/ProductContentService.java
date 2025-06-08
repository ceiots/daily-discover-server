package com.example.service;

import com.example.model.ProductContent;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 商品内容服务接口
 */
public interface ProductContentService {

    /**
     * 根据商品ID获取内容信息
     */
    ProductContent getByProductId(Long productId);
    
    /**
     * 创建商品内容
     */
    @Transactional
    ProductContent create(ProductContent content);
    
    /**
     * 更新商品内容
     */
    @Transactional
    boolean update(ProductContent content);
    
    /**
     * 删除商品内容
     */
    @Transactional
    boolean deleteByProductId(Long productId);
    
    /**
     * 批量获取商品内容
     */
    List<ProductContent> getByProductIds(List<Long> productIds);
} 