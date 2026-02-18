package com.dailydiscover.mapper;

import com.dailydiscover.model.InventoryTransaction;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 库存操作记录表 Mapper
 */
@Mapper
public interface InventoryTransactionMapper extends BaseMapper<InventoryTransaction> {
    
    /**
     * 根据库存ID查询操作记录
     */
    @Select("SELECT * FROM inventory_transactions WHERE inventory_id = #{inventoryId} ORDER BY created_at DESC")
    List<InventoryTransaction> findByInventoryId(@Param("inventoryId") Long inventoryId);
    
    /**
     * 根据操作类型查询记录
     */
    @Select("SELECT * FROM inventory_transactions WHERE transaction_type = #{transactionType} ORDER BY created_at DESC")
    List<InventoryTransaction> findByTransactionType(@Param("transactionType") String transactionType);
    
    /**
     * 根据商品ID查询库存操作记录
     */
    @Select("SELECT * FROM inventory_transactions WHERE product_id = #{productId} ORDER BY created_at DESC")
    List<InventoryTransaction> findByProductId(@Param("productId") Long productId);
    
    /**
     * 根据SKU ID查询库存操作记录
     */
    @Select("SELECT * FROM inventory_transactions WHERE sku_id = #{skuId} ORDER BY created_at DESC")
    List<InventoryTransaction> findBySkuId(@Param("skuId") Long skuId);
    
    /**
     * 根据关联类型和ID查询记录
     */
    @Select("SELECT * FROM inventory_transactions WHERE reference_type = #{referenceType} AND reference_id = #{referenceId} ORDER BY created_at DESC")
    List<InventoryTransaction> findByReference(@Param("referenceType") String referenceType, @Param("referenceId") Long referenceId);
    
    /**
     * 根据商品ID查询库存操作记录
     */
    @Select("SELECT * FROM inventory_transactions WHERE product_id = #{productId} ORDER BY created_at DESC")
    List<InventoryTransaction> getByProductId(@Param("productId") Long productId);
    
    /**
     * 根据操作类型查询记录
     */
    @Select("SELECT * FROM inventory_transactions WHERE transaction_type = #{transactionType} ORDER BY created_at DESC")
    List<InventoryTransaction> getByTransactionType(@Param("transactionType") String transactionType);
    
    /**
     * 根据时间范围查询记录
     */
    @Select("SELECT * FROM inventory_transactions WHERE created_at BETWEEN #{startTime} AND #{endTime} ORDER BY created_at DESC")
    List<InventoryTransaction> getByTimeRange(@Param("startTime") java.time.LocalDateTime startTime, @Param("endTime") java.time.LocalDateTime endTime);
    
    /**
     * 记录入库操作
     */
    @Select("INSERT INTO inventory_transactions (product_id, transaction_type, quantity, reference_type, reference_id, notes, created_at) VALUES (#{productId}, 'IN', #{quantity}, #{referenceType}, #{referenceId}, #{notes}, NOW())")
    boolean recordStockIn(@Param("productId") Long productId, @Param("quantity") Integer quantity, @Param("referenceType") String referenceType, @Param("referenceId") Long referenceId, @Param("notes") String notes);
    
    /**
     * 记录出库操作
     */
    @Select("INSERT INTO inventory_transactions (product_id, transaction_type, quantity, reference_type, reference_id, notes, created_at) VALUES (#{productId}, 'OUT', #{quantity}, #{referenceType}, #{referenceId}, #{notes}, NOW())")
    boolean recordStockOut(@Param("productId") Long productId, @Param("quantity") Integer quantity, @Param("referenceType") String referenceType, @Param("referenceId") Long referenceId, @Param("notes") String notes);
    
    /**
     * 记录库存调整
     */
    @Select("INSERT INTO inventory_transactions (product_id, transaction_type, quantity, reference_type, reference_id, notes, created_at) VALUES (#{productId}, 'ADJUST', #{newQuantity}, 'ADJUSTMENT', #{productId}, #{reason}, NOW())")
    boolean recordStockAdjustment(@Param("productId") Long productId, @Param("oldQuantity") Integer oldQuantity, @Param("newQuantity") Integer newQuantity, @Param("reason") String reason);
    
    /**
     * 获取库存变更历史
     */
    @Select("SELECT * FROM inventory_transactions WHERE product_id = #{productId} ORDER BY created_at DESC LIMIT #{limit}")
    List<InventoryTransaction> getStockChangeHistory(@Param("productId") Long productId, @Param("limit") Integer limit);
    
    /**
     * 获取总入库数量
     */
    @Select("SELECT COALESCE(SUM(quantity), 0) FROM inventory_transactions WHERE product_id = #{productId} AND transaction_type = 'IN'")
    Integer getTotalStockInQuantity(@Param("productId") Long productId);
    
    /**
     * 获取总出库数量
     */
    @Select("SELECT COALESCE(SUM(quantity), 0) FROM inventory_transactions WHERE product_id = #{productId} AND transaction_type = 'OUT'")
    Integer getTotalStockOutQuantity(@Param("productId") Long productId);
    
    /**
     * 获取交易统计
     */
    @Select("SELECT COUNT(*) as total_transactions, COALESCE(SUM(CASE WHEN transaction_type = 'IN' THEN quantity ELSE 0 END), 0) as total_in, COALESCE(SUM(CASE WHEN transaction_type = 'OUT' THEN quantity ELSE 0 END), 0) as total_out FROM inventory_transactions WHERE product_id = #{productId} AND created_at BETWEEN #{startTime} AND #{endTime}")
    java.util.Map<String, Object> getTransactionStats(@Param("productId") Long productId, @Param("startTime") java.time.LocalDateTime startTime, @Param("endTime") java.time.LocalDateTime endTime);
}