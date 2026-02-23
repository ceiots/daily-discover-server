package com.dailydiscover.service.impl;

import com.dailydiscover.mapper.ProductServiceInfoMapper;
import com.dailydiscover.model.dto.ProductServiceInfoDTO;
import com.dailydiscover.service.ProductServiceInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 产品服务信息服务实现类
 */
@Service
@RequiredArgsConstructor
public class ProductServiceInfoServiceImpl implements ProductServiceInfoService {
    
    private final ProductServiceInfoMapper productServiceInfoMapper;
    
    /**
     * 根据商品ID获取产品服务信息
     */
    @Override
    public List<ProductServiceInfoDTO> getProductServiceInfo(Long productId) {
        return productServiceInfoMapper.findServiceInfoByProductId(productId);
    }
    
    /**
     * 获取所有启用的产品服务信息分类
     */
    @Override
    public List<ProductServiceInfoDTO.ServiceCategoryDTO> getEnabledCategories() {
        return productServiceInfoMapper.findAllEnabledCategories();
    }
    
    /**
     * 根据分类ID获取信息项
     */
    @Override
    public List<ProductServiceInfoDTO> getInfoItemsByCategoryId(Long categoryId) {
        return productServiceInfoMapper.findInfoItemsByCategoryId(categoryId);
    }
}