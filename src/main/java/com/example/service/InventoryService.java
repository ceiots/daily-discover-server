package com.example.service;

import com.example.mapper.InventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    @Autowired
    private InventoryMapper inventoryMapper;

    public void decreaseStock(Long productId, int quantity) {
        try {
            int updatedRows = inventoryMapper.decreaseStock(productId, quantity);
            if (updatedRows == 0) {
                throw new RuntimeException("Failed to decrease stock, product might be out of stock");
            }
        } catch (Exception e) {
            // 记录异常日志
            System.err.println("Error decreasing stock: " + e.getMessage());
            throw e; // 重新抛出异常以便上层处理
        }
    }
} 