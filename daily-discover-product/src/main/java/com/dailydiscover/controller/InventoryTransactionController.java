package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.InventoryTransaction;
import com.dailydiscover.service.InventoryTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory-transactions")
@RequiredArgsConstructor
public class InventoryTransactionController {

    private final InventoryTransactionService inventoryTransactionService;

    @GetMapping
    @ApiLog("获取所有库存交易记录")
    public ResponseEntity<List<InventoryTransaction>> getAllInventoryTransactions() {
        try {
            List<InventoryTransaction> transactions = inventoryTransactionService.list();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取库存交易记录")
    public ResponseEntity<InventoryTransaction> getInventoryTransactionById(@PathVariable Long id) {
        try {
            InventoryTransaction transaction = inventoryTransactionService.getById(id);
            return transaction != null ? ResponseEntity.ok(transaction) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/product/{productId}")
    @ApiLog("根据商品ID获取库存交易记录")
    public ResponseEntity<List<InventoryTransaction>> getInventoryTransactionsByProductId(@PathVariable Long productId) {
        try {
            List<InventoryTransaction> transactions = inventoryTransactionService.getByProductId(productId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/sku/{skuId}")
    @ApiLog("根据SKU ID获取库存交易记录")
    public ResponseEntity<List<InventoryTransaction>> getInventoryTransactionsBySkuId(@PathVariable Long skuId) {
        try {
            List<InventoryTransaction> transactions = inventoryTransactionService.findBySkuId(skuId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/type/{transactionType}")
    @ApiLog("根据交易类型获取库存交易记录")
    public ResponseEntity<List<InventoryTransaction>> getInventoryTransactionsByType(@PathVariable String transactionType) {
        try {
            List<InventoryTransaction> transactions = inventoryTransactionService.findByTransactionType(transactionType);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/order/{orderId}")
    @ApiLog("根据订单ID获取库存交易记录")
    public ResponseEntity<List<InventoryTransaction>> getInventoryTransactionsByOrderId(@PathVariable Long orderId) {
        try {
            List<InventoryTransaction> transactions = inventoryTransactionService.findByOrderId(orderId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建库存交易记录")
    public ResponseEntity<InventoryTransaction> createInventoryTransaction(@RequestBody InventoryTransaction transaction) {
        try {
            boolean success = inventoryTransactionService.save(transaction);
            return success ? ResponseEntity.ok(transaction) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新库存交易记录")
    public ResponseEntity<InventoryTransaction> updateInventoryTransaction(@PathVariable Long id, @RequestBody InventoryTransaction transaction) {
        try {
            transaction.setId(id);
            boolean success = inventoryTransactionService.updateById(transaction);
            return success ? ResponseEntity.ok(transaction) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除库存交易记录")
    public ResponseEntity<Void> deleteInventoryTransaction(@PathVariable Long id) {
        try {
            boolean success = inventoryTransactionService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}