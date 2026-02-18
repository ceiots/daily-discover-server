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
}