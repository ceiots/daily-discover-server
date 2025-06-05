package com.example.service;

/**
 * 库存服务接口
 */
public interface StockService {

    /**
     * 扣减商品库存
     * @param productId 商品ID
     * @param quantity 扣减数量
     * @return 是否成功
     */
    boolean decreaseStock(Long productId, Integer quantity);

    /**
     * 恢复商品库存
     * @param productId 商品ID
     * @param quantity 恢复数量
     * @return 是否成功
     */
    boolean increaseStock(Long productId, Integer quantity);

    /**
     * 查询商品库存
     * @param productId 商品ID
     * @return 剩余库存
     */
    Integer getStock(Long productId);

    /**
     * 锁定库存（下单时调用）
     * @param productId 商品ID
     * @param quantity 锁定数量
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean lockStock(Long productId, Integer quantity, Long orderId);

    /**
     * 解锁库存（取消订单时调用）
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean unlockStock(Long orderId);
} 