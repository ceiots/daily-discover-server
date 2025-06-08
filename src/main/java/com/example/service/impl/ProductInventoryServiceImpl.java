package com.example.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mapper.ProductInventoryLogMapper;
import com.example.mapper.ProductSkuMapper;
import com.example.model.ProductInventoryLog;
import com.example.model.ProductSku;
import com.example.service.ProductInventoryService;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * 商品库存服务实现类
 */
@Slf4j
@Service
public class ProductInventoryServiceImpl implements ProductInventoryService {

    @Autowired
    private ProductSkuMapper productSkuMapper;
    
    @Autowired
    private ProductInventoryLogMapper inventoryLogMapper;
    
    @Override
    @Transactional
    public boolean increaseStock(Long productId, Long skuId, Integer quantity, Long operatorId, String remark) {
        if (productId == null || skuId == null || quantity == null || quantity <= 0) {
            return false;
        }
        
        try {
            // 1. 获取当前库存
            ProductSku sku = productSkuMapper.findById(skuId);
            if (sku == null) {
                log.error("SKU不存在, skuId: {}", skuId);
                return false;
            }
            
            // 2. 计算新库存
            Integer beforeStock = sku.getStock();
            Integer afterStock = beforeStock + quantity;
            
            // 3. 更新库存
            sku.setStock(afterStock);
            productSkuMapper.updateSku(sku);
            
            // 4. 记录库存变更
            ProductInventoryLog log = ProductInventoryLog.createInboundLog(
                    productId, skuId, quantity, beforeStock, afterStock, operatorId, remark);
            inventoryLogMapper.insert(log);
            
            // 5. 更新商品总库存
            updateProductTotalStock(productId);
            
            return true;
        } catch (Exception e) {
            log.error("增加库存失败, productId: {}, skuId: {}, quantity: {}", productId, skuId, quantity, e);
            throw new RuntimeException("增加库存失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public boolean decreaseStock(Long productId, Long skuId, Integer quantity, Long operatorId, String remark) {
        if (productId == null || skuId == null || quantity == null || quantity <= 0) {
            return false;
        }
        
        try {
            // 1. 获取当前库存
            ProductSku sku = productSkuMapper.findById(skuId);
            if (sku == null) {
                log.error("SKU不存在, skuId: {}", skuId);
                return false;
            }
            
            // 2. 检查库存是否足够
            Integer beforeStock = sku.getStock();
            if (beforeStock < quantity) {
                log.error("库存不足, skuId: {}, current: {}, required: {}", skuId, beforeStock, quantity);
                return false;
            }
            
            // 3. 计算新库存
            Integer afterStock = beforeStock - quantity;
            
            // 4. 更新库存
            sku.setStock(afterStock);
            productSkuMapper.updateSku(sku);
            
            // 5. 记录库存变更
            ProductInventoryLog log = ProductInventoryLog.createOutboundLog(
                    productId, skuId, null, quantity, beforeStock, afterStock, operatorId, remark);
            inventoryLogMapper.insert(log);
            
            // 6. 更新商品总库存
            updateProductTotalStock(productId);
            
            return true;
        } catch (Exception e) {
            log.error("减少库存失败, productId: {}, skuId: {}, quantity: {}", productId, skuId, quantity, e);
            throw new RuntimeException("减少库存失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public boolean lockStock(Long productId, Long skuId, Integer quantity, Long orderId, Long operatorId, String remark) {
        if (productId == null || skuId == null || quantity == null || quantity <= 0 || orderId == null) {
            return false;
        }
        
        try {
            // 1. 获取当前库存
            ProductSku sku = productSkuMapper.findById(skuId);
            if (sku == null) {
                log.error("SKU不存在, skuId: {}", skuId);
                return false;
            }
            
            // 2. 检查库存是否足够
            Integer beforeStock = sku.getStock();
            Integer beforeLockedStock = sku.getLockedStock() != null ? sku.getLockedStock() : 0;
            
            if (beforeStock < quantity) {
                log.error("库存不足, skuId: {}, current: {}, required: {}", skuId, beforeStock, quantity);
                return false;
            }
            
            // 3. 计算新库存
            Integer afterStock = beforeStock - quantity;
            Integer afterLockedStock = beforeLockedStock + quantity;
            
            // 4. 更新库存
            sku.setStock(afterStock);
            sku.setLockedStock(afterLockedStock);
            productSkuMapper.updateSku(sku);
            
            // 5. 记录库存变更
            ProductInventoryLog log = ProductInventoryLog.createLockLog(
                    productId, skuId, orderId, quantity, beforeStock, afterStock, operatorId, remark);
            inventoryLogMapper.insert(log);
            
            // 6. 更新商品总库存
            updateProductTotalStock(productId);
            
            return true;
        } catch (Exception e) {
            log.error("锁定库存失败, productId: {}, skuId: {}, quantity: {}, orderId: {}", 
                    productId, skuId, quantity, orderId, e);
            throw new RuntimeException("锁定库存失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public boolean unlockStock(Long productId, Long skuId, Integer quantity, Long orderId, Long operatorId, String remark) {
        if (productId == null || skuId == null || quantity == null || quantity <= 0 || orderId == null) {
            return false;
        }
        
        try {
            // 1. 获取当前库存
            ProductSku sku = productSkuMapper.findById(skuId);
            if (sku == null) {
                log.error("SKU不存在, skuId: {}", skuId);
                return false;
            }
            
            // 2. 检查锁定库存是否足够
            Integer beforeStock = sku.getStock();
            Integer beforeLockedStock = sku.getLockedStock() != null ? sku.getLockedStock() : 0;
            
            if (beforeLockedStock < quantity) {
                log.error("锁定库存不足, skuId: {}, locked: {}, required: {}", skuId, beforeLockedStock, quantity);
                return false;
            }
            
            // 3. 计算新库存
            Integer afterStock = beforeStock + quantity;
            Integer afterLockedStock = beforeLockedStock - quantity;
            
            // 4. 更新库存
            sku.setStock(afterStock);
            sku.setLockedStock(afterLockedStock);
            productSkuMapper.updateSku(sku);
            
            // 5. 记录库存变更
            ProductInventoryLog log = ProductInventoryLog.createUnlockLog(
                    productId, skuId, orderId, quantity, beforeStock, afterStock, operatorId, remark);
            inventoryLogMapper.insert(log);
            
            // 6. 更新商品总库存
            updateProductTotalStock(productId);
            
            return true;
        } catch (Exception e) {
            log.error("解锁库存失败, productId: {}, skuId: {}, quantity: {}, orderId: {}", 
                    productId, skuId, quantity, orderId, e);
            throw new RuntimeException("解锁库存失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<ProductInventoryLog> getInventoryLogs(Long productId, Long skuId, Integer limit) {
        if (productId == null || skuId == null) {
            return new ArrayList<>();
        }
        
        try {
            limit = limit != null && limit > 0 ? limit : 20;
            return inventoryLogMapper.findByProductAndSku(productId, skuId, limit);
        } catch (Exception e) {
            log.error("获取库存日志失败, productId: {}, skuId: {}", productId, skuId, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<ProductInventoryLog> getInventoryLogsByOrderId(Long orderId) {
        if (orderId == null) {
            return new ArrayList<>();
        }
        
        try {
            return inventoryLogMapper.findByOrderId(orderId);
        } catch (Exception e) {
            log.error("获取订单库存日志失败, orderId: {}", orderId, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean isStockSufficient(Long skuId, Integer quantity) {
        if (skuId == null || quantity == null || quantity <= 0) {
            return false;
        }
        
        try {
            ProductSku sku = productSkuMapper.findById(skuId);
            if (sku == null) {
                return false;
            }
            
            return sku.getStock() >= quantity;
        } catch (Exception e) {
            log.error("检查库存是否充足失败, skuId: {}, quantity: {}", skuId, quantity, e);
            return false;
        }
    }
    
    /**
     * 更新商品总库存（根据SKU库存计算）
     */
    private void updateProductTotalStock(Long productId) {
        try {
            productSkuMapper.updateProductTotalStock(productId);
        } catch (Exception e) {
            log.error("更新商品总库存失败, productId: {}", productId, e);
        }
    }
} 