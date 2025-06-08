package com.example.service;

import com.example.model.ProductInventoryLog;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 商品库存服务接口
 */
public interface ProductInventoryService {

    /**
     * 增加商品SKU库存
     * @param productId 商品ID
     * @param skuId SKU ID
     * @param quantity 增加数量
     * @param operatorId 操作人ID
     * @param remark 备注
     * @return 操作是否成功
     */
    @Transactional
    boolean increaseStock(Long productId, Long skuId, Integer quantity, Long operatorId, String remark);
    
    /**
     * 减少商品SKU库存
     * @param productId 商品ID
     * @param skuId SKU ID
     * @param quantity 减少数量
     * @param operatorId 操作人ID
     * @param remark 备注
     * @return 操作是否成功
     */
    @Transactional
    boolean decreaseStock(Long productId, Long skuId, Integer quantity, Long operatorId, String remark);
    
    /**
     * 锁定商品SKU库存
     * @param productId 商品ID
     * @param skuId SKU ID
     * @param quantity 锁定数量
     * @param orderId 订单ID
     * @param operatorId 操作人ID
     * @param remark 备注
     * @return 操作是否成功
     */
    @Transactional
    boolean lockStock(Long productId, Long skuId, Integer quantity, Long orderId, Long operatorId, String remark);
    
    /**
     * 解锁商品SKU库存
     * @param productId 商品ID
     * @param skuId SKU ID
     * @param quantity 解锁数量
     * @param orderId 订单ID
     * @param operatorId 操作人ID
     * @param remark 备注
     * @return 操作是否成功
     */
    @Transactional
    boolean unlockStock(Long productId, Long skuId, Integer quantity, Long orderId, Long operatorId, String remark);
    
    /**
     * 获取商品库存变更日志
     * @param productId 商品ID
     * @param skuId SKU ID
     * @param limit 限制数量
     * @return 库存变更日志列表
     */
    List<ProductInventoryLog> getInventoryLogs(Long productId, Long skuId, Integer limit);
    
    /**
     * 获取订单相关的库存日志
     * @param orderId 订单ID
     * @return 库存变更日志列表
     */
    List<ProductInventoryLog> getInventoryLogsByOrderId(Long orderId);
    
    /**
     * 检查库存是否充足
     * @param skuId SKU ID
     * @param quantity 需要的数量
     * @return 是否充足
     */
    boolean isStockSufficient(Long skuId, Integer quantity);
} 