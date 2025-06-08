package com.example.service;

import com.example.model.ProductSpecification;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 商品规格服务接口
 */
public interface ProductSpecificationService {

    /**
     * 根据商品ID获取规格信息
     */
    ProductSpecification getByProductId(Long productId);
    
    /**
     * 创建商品规格
     */
    @Transactional
    ProductSpecification create(ProductSpecification specification);
    
    /**
     * 更新商品规格
     */
    @Transactional
    boolean update(ProductSpecification specification);
    
    /**
     * 删除商品规格
     */
    @Transactional
    boolean deleteByProductId(Long productId);
    
    /**
     * 批量获取商品规格
     */
    List<ProductSpecification> getByProductIds(List<Long> productIds);
} 