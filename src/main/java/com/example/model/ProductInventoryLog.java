package com.example.model;

import java.util.Date;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 商品库存日志实体类
 * 记录商品库存变更历史
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductInventoryLog {
    private Long id;
    private Long productId;        // 商品ID
    private Long skuId;            // SKU ID
    private Long orderId;          // 订单ID
    private Integer changeType;    // 变更类型:1-入库,2-出库,3-锁定,4-解锁
    private Integer quantity;      // 变更数量
    private Integer beforeStock;   // 变更前库存
    private Integer afterStock;    // 变更后库存
    private Long operatorId;       // 操作人ID
    private String remark;         // 备注说明
    private Date createTime;       // 创建时间
    
    /**
     * 创建入库记录
     */
    public static ProductInventoryLog createInboundLog(Long productId, Long skuId, Integer quantity, 
                                                    Integer beforeStock, Integer afterStock, Long operatorId, String remark) {
        ProductInventoryLog log = new ProductInventoryLog();
        log.setProductId(productId);
        log.setSkuId(skuId);
        log.setChangeType(1);
        log.setQuantity(quantity);
        log.setBeforeStock(beforeStock);
        log.setAfterStock(afterStock);
        log.setOperatorId(operatorId);
        log.setRemark(remark);
        log.setCreateTime(new Date());
        return log;
    }
    
    /**
     * 创建出库记录
     */
    public static ProductInventoryLog createOutboundLog(Long productId, Long skuId, Long orderId, Integer quantity, 
                                                     Integer beforeStock, Integer afterStock, Long operatorId, String remark) {
        ProductInventoryLog log = new ProductInventoryLog();
        log.setProductId(productId);
        log.setSkuId(skuId);
        log.setOrderId(orderId);
        log.setChangeType(2);
        log.setQuantity(quantity);
        log.setBeforeStock(beforeStock);
        log.setAfterStock(afterStock);
        log.setOperatorId(operatorId);
        log.setRemark(remark);
        log.setCreateTime(new Date());
        return log;
    }
    
    /**
     * 创建库存锁定记录
     */
    public static ProductInventoryLog createLockLog(Long productId, Long skuId, Long orderId, Integer quantity, 
                                                 Integer beforeStock, Integer afterStock, Long operatorId, String remark) {
        ProductInventoryLog log = new ProductInventoryLog();
        log.setProductId(productId);
        log.setSkuId(skuId);
        log.setOrderId(orderId);
        log.setChangeType(3);
        log.setQuantity(quantity);
        log.setBeforeStock(beforeStock);
        log.setAfterStock(afterStock);
        log.setOperatorId(operatorId);
        log.setRemark(remark);
        log.setCreateTime(new Date());
        return log;
    }
    
    /**
     * 创建库存解锁记录
     */
    public static ProductInventoryLog createUnlockLog(Long productId, Long skuId, Long orderId, Integer quantity, 
                                                   Integer beforeStock, Integer afterStock, Long operatorId, String remark) {
        ProductInventoryLog log = new ProductInventoryLog();
        log.setProductId(productId);
        log.setSkuId(skuId);
        log.setOrderId(orderId);
        log.setChangeType(4);
        log.setQuantity(quantity);
        log.setBeforeStock(beforeStock);
        log.setAfterStock(afterStock);
        log.setOperatorId(operatorId);
        log.setRemark(remark);
        log.setCreateTime(new Date());
        return log;
    }
    
    /**
     * 验证日志是否有效
     * @return 如果日志有效返回true，否则返回false
     */
    public boolean isValid() {
        return productId != null && skuId != null && 
               changeType != null && quantity != null && 
               beforeStock != null && afterStock != null;
    }
    
    /**
     * 获取变更类型的文本描述
     * @return 变更类型的文本描述
     */
    public String getChangeTypeText() {
        if (changeType == null) {
            return "未知";
        }
        
        switch (changeType) {
            case 1: return "入库";
            case 2: return "出库";
            case 3: return "锁定";
            case 4: return "解锁";
            default: return "未知(" + changeType + ")";
        }
    }
} 