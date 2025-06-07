package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mapper.ProductSkuMapper;
import com.example.model.ProductSku;
import com.example.service.ProductSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 商品SKU服务实现类
 */
@Service
public class ProductSkuServiceImpl implements ProductSkuService {

    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Override
    public List<ProductSku> getSkusByProductId(Long productId) {
        QueryWrapper<ProductSku> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId);
        return productSkuMapper.selectList(queryWrapper);
    }

    @Override
    public ProductSku getSkuById(Long skuId) {
        return productSkuMapper.selectById(skuId);
    }

    @Override
    public ProductSku getSkuByProductIdAndSpecs(Long productId, Map<String, String> specifications) {
        if (specifications == null || specifications.isEmpty()) {
            return null;
        }
        
        // 获取所有SKU
        List<ProductSku> skus = getSkusByProductId(productId);
        
        // 遍历查找匹配的SKU
        for (ProductSku sku : skus) {
            Map<String, String> skuSpecs = sku.getSpecifications();
            if (skuSpecs != null && skuSpecs.equals(specifications)) {
                return sku;
            }
        }
        
        return null;
    }

    @Override
    @Transactional
    public ProductSku saveOrUpdateSku(ProductSku sku) {
        if (sku.getId() == null) {
            productSkuMapper.insert(sku);
        } else {
            productSkuMapper.updateById(sku);
        }
        return sku;
    }

    @Override
    @Transactional
    public List<ProductSku> batchSaveOrUpdateSkus(List<ProductSku> skus) {
        List<ProductSku> result = new ArrayList<>();
        
        for (ProductSku sku : skus) {
            result.add(saveOrUpdateSku(sku));
        }
        
        return result;
    }

    @Override
    @Transactional
    public void deleteSku(Long skuId) {
        productSkuMapper.deleteById(skuId);
    }

    @Override
    @Transactional
    public void deleteSkusByProductId(Long productId) {
        QueryWrapper<ProductSku> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId);
        productSkuMapper.delete(queryWrapper);
    }
} 