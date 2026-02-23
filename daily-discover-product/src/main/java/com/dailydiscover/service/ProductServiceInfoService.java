package com.dailydiscover.service;

import com.dailydiscover.model.dto.ProductServiceInfoDTO;

import java.util.List;

/**
 * 产品服务信息服务接口
 */
public interface ProductServiceInfoService {
    
    /**
     * 根据商品ID获取产品服务信息
     */
    List<ProductServiceInfoDTO> getProductServiceInfo(Long productId);
    
    /**
     * 获取所有启用的产品服务信息分类
     */
    List<ProductServiceInfoDTO.ServiceCategoryDTO> getEnabledCategories();
    
    /**
     * 根据分类ID获取信息项
     */
    List<ProductServiceInfoDTO> getInfoItemsByCategoryId(Long categoryId);
}