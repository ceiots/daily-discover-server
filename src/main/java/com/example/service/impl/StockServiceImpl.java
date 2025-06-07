package com.example.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dao.ProductDao;
import com.example.dao.StockLockDao;
import com.example.model.Product;
import com.example.model.StockLock;
import com.example.service.StockService;

@Service
public class StockServiceImpl implements StockService {
    private static final Logger logger = LoggerFactory.getLogger(StockServiceImpl.class);

    @Autowired
    private ProductDao productDao;

    @Autowired
    private StockLockDao stockLockDao;

    @Override
    @Transactional
    public boolean decreaseStock(Long productId, Integer quantity) {
        // 1. 查询商品
        Product product = productDao.findById(productId);
        if (product == null) {
            logger.error("商品不存在: {}", productId);
            return false;
        }

        // 2. 检查库存是否足够
        if (product.getTotalStock() < quantity) {
            logger.error("商品库存不足: {}, 当前库存: {}, 需要: {}", 
                    productId, product.getTotalStock(), quantity);
            return false;
        }

        // 3. 使用乐观锁更新库存，并发安全
        int rows = productDao.decreaseStock(productId, quantity, product.getVersion());
        if (rows <= 0) {
            logger.error("乐观锁更新失败，可能发生并发更新: {}", productId);
            // 可以重试几次，这里简化处理，直接返回失败
            return false;
        }

        logger.info("库存扣减成功: 商品 {}, 数量 {}, 剩余 {}", 
                productId, quantity, product.getTotalStock() - quantity);
        return true;
    }

    @Override
    @Transactional
    public boolean increaseStock(Long productId, Integer quantity) {
        // 1. 查询商品
        Product product = productDao.findById(productId);
        if (product == null) {
            logger.error("商品不存在: {}", productId);
            return false;
        }

        // 2. 使用乐观锁更新库存，并发安全
        int rows = productDao.increaseStock(productId, quantity, product.getVersion());
        if (rows <= 0) {
            logger.error("乐观锁更新失败，可能发生并发更新: {}", productId);
            // 可以重试几次，这里简化处理，直接返回失败
            return false;
        }

        logger.info("库存恢复成功: 商品 {}, 数量 {}, 更新后 {}", 
                productId, quantity, product.getTotalStock() + quantity);
        return true;
    }

    @Override
    public Integer getStock(Long productId) {
        Product product = productDao.findById(productId);
        return product != null ? product.getTotalStock() : 0;
    }

    @Override
    @Transactional
    public boolean lockStock(Long productId, Integer quantity, Long orderId) {
        // 1. 先扣减库存
        boolean decreased = decreaseStock(productId, quantity);
        if (!decreased) {
            return false;
        }

        // 2. 记录库存锁定
        StockLock stockLock = new StockLock();
        stockLock.setProductId(productId);
        stockLock.setQuantity(quantity);
        stockLock.setOrderId(orderId);
        stockLock.setLockStatus(1); // 1: 锁定
        stockLock.setCreateTime(new Date());
        
        stockLockDao.insert(stockLock);
        logger.info("库存已锁定: 商品 {}, 数量 {}, 订单 {}", productId, quantity, orderId);
        return true;
    }

    @Override
    @Transactional
    public boolean unlockStock(Long orderId) {
        // 1. 查询锁定记录
        List<StockLock> lockList = stockLockDao.findByOrderId(orderId);
        if (lockList == null || lockList.isEmpty()) {
            logger.warn("找不到订单的库存锁定记录: {}", orderId);
            return false;
        }

        boolean success = true;
        // 2. 恢复每个商品的库存
        for (StockLock lock : lockList) {
            if (lock.getLockStatus() == 1) { // 只处理已锁定状态
                boolean increased = increaseStock(lock.getProductId(), lock.getQuantity());
                if (increased) {
                    // 更新锁定状态为已解锁
                    stockLockDao.updateStatus(lock.getId(), 2); // 2: 已解锁
                    logger.info("库存已解锁: 商品 {}, 数量 {}, 订单 {}", 
                            lock.getProductId(), lock.getQuantity(), orderId);
                } else {
                    success = false;
                    logger.error("库存解锁失败: 商品 {}, 数量 {}, 订单 {}", 
                            lock.getProductId(), lock.getQuantity(), orderId);
                }
            }
        }
        return success;
    }
} 