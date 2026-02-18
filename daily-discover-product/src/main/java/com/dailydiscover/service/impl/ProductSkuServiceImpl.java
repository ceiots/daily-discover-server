package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ProductSkuMapper;
import com.dailydiscover.model.ProductSku;
import com.dailydiscover.service.ProductSkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductSkuServiceImpl extends ServiceImpl<ProductSkuMapper, ProductSku> implements ProductSkuService {
    
    @Autowired
    private ProductSkuMapper productSkuMapper;
    
    @Override
    public List<ProductSku> findByProductId(Long productId) {
        return productSkuMapper.findByProductId(productId);
    }
    
    @Override
    public ProductSku findById(Long skuId) {
        return productSkuMapper.findById(skuId);
    }
    
    @Override
    public ProductSku findBySkuCode(String skuCode) {
        return productSkuMapper.findBySkuCode(skuCode);
    }
    
    @Override
    public int updateStock(Long skuId, Integer quantity) {
        return productSkuMapper.updateStock(skuId, quantity);
    }
}