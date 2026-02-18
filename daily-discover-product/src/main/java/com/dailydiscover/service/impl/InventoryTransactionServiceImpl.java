package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.InventoryTransactionMapper;
import com.dailydiscover.model.InventoryTransaction;
import com.dailydiscover.service.InventoryTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class InventoryTransactionServiceImpl extends ServiceImpl<InventoryTransactionMapper, InventoryTransaction> implements InventoryTransactionService {
    
    @Autowired
    private InventoryTransactionMapper inventoryTransactionMapper;
    
    @Override
    public List<InventoryTransaction> getByProductId(Long productId) {
        return inventoryTransactionMapper.getByProductId(productId);
    }
    
    @Override
    public List<InventoryTransaction> getByTransactionType(String transactionType) {
        return inventoryTransactionMapper.getByTransactionType(transactionType);
    }
    
    @Override
    public List<InventoryTransaction> getByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return inventoryTransactionMapper.getByTimeRange(startTime, endTime);
    }
    
    @Override
    public boolean recordStockIn(Long productId, Integer quantity, String referenceType, Long referenceId, String notes) {
        return inventoryTransactionMapper.recordStockIn(productId, quantity, referenceType, referenceId, notes);
    }
    
    @Override
    public boolean recordStockOut(Long productId, Integer quantity, String referenceType, Long referenceId, String notes) {
        return inventoryTransactionMapper.recordStockOut(productId, quantity, referenceType, referenceId, notes);
    }
    
    @Override
    public boolean recordStockAdjustment(Long productId, Integer oldQuantity, Integer newQuantity, String reason) {
        return inventoryTransactionMapper.recordStockAdjustment(productId, oldQuantity, newQuantity, reason);
    }
    
    @Override
    public List<InventoryTransaction> getStockChangeHistory(Long productId, Integer limit) {
        return inventoryTransactionMapper.getStockChangeHistory(productId, limit);
    }
    
    @Override
    public Integer getTotalStockInQuantity(Long productId) {
        return inventoryTransactionMapper.getTotalStockInQuantity(productId);
    }
    
    @Override
    public Integer getTotalStockOutQuantity(Long productId) {
        return inventoryTransactionMapper.getTotalStockOutQuantity(productId);
    }
    
    @Override
    public Map<String, Object> getTransactionStats(Long productId, LocalDateTime startTime, LocalDateTime endTime) {
        return inventoryTransactionMapper.getTransactionStats(productId, startTime, endTime);
    }
}