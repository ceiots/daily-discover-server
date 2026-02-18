package com.dailydiscover.service;

import com.dailydiscover.model.InventoryTransaction;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 库存交易记录服务接口
 */
public interface InventoryTransactionService extends IService<InventoryTransaction> {
    
    /**
     * 根据商品ID查询交易记录
     */
    java.util.List<InventoryTransaction> getByProductId(Long productId);
    
    /**
     * 根据交易类型查询交易记录
     */
    java.util.List<InventoryTransaction> getByTransactionType(String transactionType);
    
    /**
     * 根据时间范围查询交易记录
     */
    java.util.List<InventoryTransaction> getByTimeRange(java.time.LocalDateTime startTime, java.time.LocalDateTime endTime);
    
    /**
     * 记录库存入库交易
     */
    boolean recordStockIn(Long productId, Integer quantity, String referenceType, Long referenceId, String notes);
    
    /**
     * 记录库存出库交易
     */
    boolean recordStockOut(Long productId, Integer quantity, String referenceType, Long referenceId, String notes);
    
    /**
     * 记录库存调整交易
     */
    boolean recordStockAdjustment(Long productId, Integer oldQuantity, Integer newQuantity, String reason);
    
    /**
     * 获取商品库存变动历史
     */
    java.util.List<InventoryTransaction> getStockChangeHistory(Long productId, Integer limit);
    
    /**
     * 统计商品总入库数量
     */
    Integer getTotalStockInQuantity(Long productId);
    
    /**
     * 统计商品总出库数量
     */
    Integer getTotalStockOutQuantity(Long productId);
    
    /**
     * 获取库存交易统计
     */
    java.util.Map<String, Object> getTransactionStats(Long productId, java.time.LocalDateTime startTime, java.time.LocalDateTime endTime);
    
    /**
     * 根据SKU ID查询交易记录
     */
    java.util.List<InventoryTransaction> findBySkuId(Long skuId);
    
    /**
     * 根据交易类型查询交易记录
     */
    java.util.List<InventoryTransaction> findByTransactionType(String transactionType);
    
    /**
     * 根据订单ID查询交易记录
     */
    java.util.List<InventoryTransaction> findByOrderId(Long orderId);
}