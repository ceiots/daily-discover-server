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
        // 使用 MyBatis-Plus 的 lambda 查询实现
        List<ProductInventoryCore> inventoryList = lambdaQuery()
                .eq(ProductInventoryCore::getProductId, productId)
                .list();
        return inventoryList != null && !inventoryList.isEmpty() ? inventoryList.get(0) : null;
    }
    
    @Override
    public ProductInventoryCore getBySkuId(Long skuId) {
        // 使用 Mapper 方法查询
        return productInventoryCoreMapper.findBySkuId(skuId);
    }
    
    @Override
    public boolean updateStockQuantity(Long productId, Integer quantity) {
        // 使用 MyBatis-Plus 的 lambda 更新实现
        return lambdaUpdate()
                .eq(ProductInventoryCore::getProductId, productId)
                .set(ProductInventoryCore::getQuantity, quantity)
                .update();
    }
    
    @Override
    public boolean increaseStock(Long productId, Integer quantity) {
        // 使用 MyBatis-Plus 的 lambda 更新实现
        return lambdaUpdate()
                .eq(ProductInventoryCore::getProductId, productId)
                .setSql("quantity = quantity + " + quantity)
                .update();
    }
    
    @Override
    public boolean decreaseStock(Long productId, Integer quantity) {
        // 使用 MyBatis-Plus 的 lambda 更新实现
        return lambdaUpdate()
                .eq(ProductInventoryCore::getProductId, productId)
                .setSql("quantity = quantity - " + quantity)
                .update();
    }
    
    @Override
    public boolean checkStockSufficient(Long productId, Integer requiredQuantity) {
        // 使用 MyBatis-Plus 的 lambda 查询实现
        ProductInventoryCore inventory = getByProductId(productId);
        return inventory != null && inventory.getQuantity() >= requiredQuantity;
    }
    
    @Override
    public List<ProductInventoryCore> getLowStockProducts(Integer threshold) {
        // 使用 MyBatis-Plus 的 lambda 查询实现
        return lambdaQuery()
                .le(ProductInventoryCore::getQuantity, threshold)
                .gt(ProductInventoryCore::getQuantity, 0)
                .list();
    }
    
    @Override
    public List<ProductInventoryCore> getOutOfStockProducts() {
        // 使用 MyBatis-Plus 的 lambda 查询实现
        return lambdaQuery()
                .le(ProductInventoryCore::getQuantity, 0)
                .list();
    }
}