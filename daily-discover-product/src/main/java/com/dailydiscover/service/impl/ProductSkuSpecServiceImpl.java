package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ProductSkuSpecMapper;
import com.dailydiscover.model.ProductSkuSpec;
import com.dailydiscover.service.ProductSkuSpecService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductSkuSpecServiceImpl extends ServiceImpl<ProductSkuSpecMapper, ProductSkuSpec> implements ProductSkuSpecService {
    
    @Autowired
    private ProductSkuSpecMapper productSkuSpecMapper;
    
    @Override
    public List<ProductSkuSpec> getSpecsByProductId(Long productId) {
        return productSkuSpecMapper.getSpecsByProductId(productId);
    }
    
    @Override
    public ProductSkuSpec createSpec(Long productId, String specName, Boolean isRequired) {
        return productSkuSpecMapper.createSpec(productId, specName, isRequired);
    }
    
    @Override
    public boolean updateSpecSortOrder(Long specId, Integer sortOrder) {
        return productSkuSpecMapper.updateSpecSortOrder(specId, sortOrder);
    }
}