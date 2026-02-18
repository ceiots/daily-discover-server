package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ProductInventoryCoreMapper;
import com.dailydiscover.model.ProductInventoryCore;
import com.dailydiscover.service.ProductInventoryCoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductInventoryCoreServiceImpl extends ServiceImpl<ProductInventoryCoreMapper, ProductInventoryCore> implements ProductInventoryCoreService {
    
    @Autowired
    private ProductInventoryCoreMapper productInventoryCoreMapper;
    
    @Override
    public ProductInventoryCore getByProductId(Long productId) {
        return productInventoryCoreMapper.getByProductId(productId);
    }
    
    @Override
    public ProductInventoryCore getBySkuId(Long skuId) {
        return productInventoryCoreMapper.getBySkuId(skuId);
    }
    
    @Override
    public boolean updateStockQuantity(Long productId, Integer quantity) {
        return productInventoryCoreMapper.updateStockQuantity(productId, quantity);
    }
    
    @Override
    public boolean increaseStock(Long productId, Integer quantity) {
        return productInventoryCoreMapper.increaseStock(productId, quantity);
    }
    
    @Override
    public boolean decreaseStock(Long productId, Integer quantity) {
        return productInventoryCoreMapper.decreaseStock(productId, quantity);
    }
    
    @Override
    public boolean checkStockSufficient(Long productId, Integer requiredQuantity) {
        return productInventoryCoreMapper.checkStockSufficient(productId, requiredQuantity);
    }
    
    @Override
    public List<ProductInventoryCore> getLowStockProducts(Integer threshold) {
        return productInventoryCoreMapper.getLowStockProducts(threshold);
    }
    
    @Override
    public List<ProductInventoryCore> getOutOfStockProducts() {
        return productInventoryCoreMapper.getOutOfStockProducts();
    }
}