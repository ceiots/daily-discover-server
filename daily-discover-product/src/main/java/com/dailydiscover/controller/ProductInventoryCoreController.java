package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.ProductInventoryCore;
import com.dailydiscover.service.ProductInventoryCoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product-inventory")
@RequiredArgsConstructor
public class ProductInventoryCoreController {

    private final ProductInventoryCoreService productInventoryCoreService;

    @GetMapping
    @ApiLog("获取所有商品库存信息")
    public ResponseEntity<List<ProductInventoryCore>> getAllProductInventories() {
        try {
            List<ProductInventoryCore> inventories = productInventoryCoreService.list();
            return ResponseEntity.ok(inventories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取商品库存信息")
    public ResponseEntity<ProductInventoryCore> getProductInventoryById(@PathVariable Long id) {
        try {
            ProductInventoryCore inventory = productInventoryCoreService.getById(id);
            return inventory != null ? ResponseEntity.ok(inventory) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/product/{productId}")
    @ApiLog("根据商品ID获取库存信息")
    public ResponseEntity<ProductInventoryCore> getProductInventoryByProductId(@PathVariable Long productId) {
        try {
            ProductInventoryCore inventory = productInventoryCoreService.getByProductId(productId);
            return inventory != null ? ResponseEntity.ok(inventory) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/sku/{skuId}")
    @ApiLog("根据SKU ID获取库存信息")
    public ResponseEntity<ProductInventoryCore> getProductInventoryBySkuId(@PathVariable Long skuId) {
        try {
            ProductInventoryCore inventory = productInventoryCoreService.getBySkuId(skuId);
            return inventory != null ? ResponseEntity.ok(inventory) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/low-stock")
    @ApiLog("获取低库存商品列表")
    public ResponseEntity<List<ProductInventoryCore>> getLowStockProducts(@RequestParam(defaultValue = "10") Integer threshold) {
        try {
            List<ProductInventoryCore> inventories = productInventoryCoreService.getLowStockProducts(threshold);
            return ResponseEntity.ok(inventories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/out-of-stock")
    @ApiLog("获取缺货商品列表")
    public ResponseEntity<List<ProductInventoryCore>> getOutOfStockProducts() {
        try {
            List<ProductInventoryCore> inventories = productInventoryCoreService.getOutOfStockProducts();
            return ResponseEntity.ok(inventories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/product/{productId}/check-stock")
    @ApiLog("检查商品库存是否充足")
    public ResponseEntity<Boolean> checkStockSufficient(@PathVariable Long productId, @RequestParam Integer requiredQuantity) {
        try {
            boolean sufficient = productInventoryCoreService.checkStockSufficient(productId, requiredQuantity);
            return ResponseEntity.ok(sufficient);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建商品库存信息")
    public ResponseEntity<ProductInventoryCore> createProductInventory(@RequestBody ProductInventoryCore inventory) {
        try {
            boolean success = productInventoryCoreService.save(inventory);
            return success ? ResponseEntity.ok(inventory) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新商品库存信息")
    public ResponseEntity<ProductInventoryCore> updateProductInventory(@PathVariable Long id, @RequestBody ProductInventoryCore inventory) {
        try {
            inventory.setId(id);
            boolean success = productInventoryCoreService.updateById(inventory);
            return success ? ResponseEntity.ok(inventory) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/product/{productId}/update-stock")
    @ApiLog("更新商品库存数量")
    public ResponseEntity<Void> updateStockQuantity(@PathVariable Long productId, @RequestParam Integer quantity) {
        try {
            boolean success = productInventoryCoreService.updateStockQuantity(productId, quantity);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/product/{productId}/increase-stock")
    @ApiLog("增加商品库存")
    public ResponseEntity<Void> increaseStock(@PathVariable Long productId, @RequestParam Integer quantity) {
        try {
            boolean success = productInventoryCoreService.increaseStock(productId, quantity);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/product/{productId}/decrease-stock")
    @ApiLog("减少商品库存")
    public ResponseEntity<Void> decreaseStock(@PathVariable Long productId, @RequestParam Integer quantity) {
        try {
            boolean success = productInventoryCoreService.decreaseStock(productId, quantity);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除商品库存信息")
    public ResponseEntity<Void> deleteProductInventory(@PathVariable Long id) {
        try {
            boolean success = productInventoryCoreService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}