package com.example.model;

import java.util.Date;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 商品库存管理实体类
 * 提取自Product表，用于管理商品库存和销量
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductInventory {
    private Long id;
    private Long productId;
    private Long skuId;
    private Integer stock;                // 可售库存
    private Integer lockedStock;          // 锁定库存（已下单未支付）
    private Integer totalStock;           // 总库存
    private Integer salesCount;           // 销售数量
    private Integer stockAlertThreshold;  // 库存预警阈值
    private Boolean isStockVisible;       // 是否显示库存数量
    private Date createTime;
    private Date updateTime;
    
    /**
     * 检查是否有足够的库存可以下单
     * @param quantity 需要的数量
     * @return 如果有足够库存返回true
     */
    public boolean hasEnoughStock(int quantity) {
        return stock != null && stock >= quantity;
    }
    
    /**
     * 检查是否达到预警库存
     * @return 如果库存低于预警值返回true
     */
    public boolean isStockAlertTriggered() {
        return stockAlertThreshold != null && stock != null && stock <= stockAlertThreshold;
    }
    
    /**
     * 锁定库存（下单时调用）
     * 将库存从可售库存转移到锁定库存
     * @param quantity 要锁定的数量
     * @return 是否成功锁定
     */
    public boolean lockStock(int quantity) {
        if (!hasEnoughStock(quantity)) {
            return false;
        }
        
        stock -= quantity;
        lockedStock = (lockedStock == null ? 0 : lockedStock) + quantity;
        return true;
    }
    
    /**
     * 解锁库存（取消订单或支付超时时调用）
     * 将库存从锁定库存返回到可售库存
     * @param quantity 要解锁的数量
     * @return 是否成功解锁
     */
    public boolean unlockStock(int quantity) {
        if (lockedStock == null || lockedStock < quantity) {
            return false;
        }
        
        lockedStock -= quantity;
        stock = (stock == null ? 0 : stock) + quantity;
        return true;
    }
    
    /**
     * 扣减库存（支付成功时调用）
     * 从锁定库存中扣除，减少总库存
     * @param quantity 要扣减的数量
     * @return 是否成功扣减
     */
    public boolean deductStock(int quantity) {
        if (lockedStock == null || lockedStock < quantity) {
            return false;
        }
        
        lockedStock -= quantity;
        totalStock = (totalStock == null ? 0 : totalStock) - quantity;
        salesCount = (salesCount == null ? 0 : salesCount) + quantity;
        return true;
    }
    
    /**
     * 增加库存
     * @param quantity 要增加的数量
     */
    public void addStock(int quantity) {
        if (stock == null) {
            stock = 0;
        }
        
        stock += quantity;
        totalStock = (totalStock == null ? 0 : totalStock) + quantity;
    }
} 