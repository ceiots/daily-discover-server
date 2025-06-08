package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mapper.ProductSkuMapper;
import com.example.model.ProductSku;
import com.example.service.ProductSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
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
    @Transactional
    public ProductSku createSku(ProductSku productSku) {
        if (productSku.getCreateTime() == null) {
            productSku.setCreateTime(new Date());
        }
        
        // 确保SKU有编码
        productSku.ensureSkuCode();
        
        // 初始化默认值
        if (productSku.getStatus() == null) {
            productSku.setStatus(1); // 默认状态为正常
        }
        if (productSku.getIsDefault() == null) {
            productSku.setIsDefault(false);
        }
        if (productSku.getStock() == null) {
            productSku.setStock(0);
        }
        if (productSku.getLockedStock() == null) {
            productSku.setLockedStock(0);
        }
        if (productSku.getSalesCount() == null) {
            productSku.setSalesCount(0);
        }
        if (productSku.getDeleted() == null) {
            productSku.setDeleted(false);
        }
        
        productSkuMapper.insertSku(productSku);
        return productSku;
    }

    @Override
    @Transactional
    public List<ProductSku> batchCreateSku(List<ProductSku> productSkus) {
        List<ProductSku> savedSkus = new ArrayList<>();
        for (ProductSku sku : productSkus) {
            savedSkus.add(createSku(sku));
        }
        return savedSkus;
    }

    @Override
    public List<ProductSku> getSkusByProductId(Long productId) {
        return productSkuMapper.findByProductId(productId);
    }

    @Override
    public ProductSku getSkuById(Long id) {
        return productSkuMapper.findById(id);
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
    public ProductSku updateSku(ProductSku productSku) {
        productSku.setUpdateTime(new Date());
        productSkuMapper.updateSku(productSku);
        return productSku;
    }

    @Override
    @Transactional
    public void deleteSku(Long id) {
        productSkuMapper.deleteSku(id);
    }

    @Override
    @Transactional
    public void deleteSkusByProductId(Long productId) {
        productSkuMapper.deleteByProductId(productId);
    }
} 