package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.ProductInventoryConfig;
import com.dailydiscover.service.ProductInventoryConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product-inventory-configs")
@RequiredArgsConstructor
public class ProductInventoryConfigController {

    private final ProductInventoryConfigService productInventoryConfigService;

    @GetMapping
    @ApiLog("获取所有商品库存配置")
    public ResponseEntity<List<ProductInventoryConfig>> getAllProductInventoryConfigs() {
        try {
            List<ProductInventoryConfig> configs = productInventoryConfigService.list();
            return ResponseEntity.ok(configs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取商品库存配置")
    public ResponseEntity<ProductInventoryConfig> getProductInventoryConfigById(@PathVariable Long id) {
        try {
            ProductInventoryConfig config = productInventoryConfigService.getById(id);
            return config != null ? ResponseEntity.ok(config) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/product/{productId}")
    @ApiLog("根据商品ID获取库存配置")
    public ResponseEntity<ProductInventoryConfig> getProductInventoryConfigByProductId(@PathVariable Long productId) {
        try {
            ProductInventoryConfig config = productInventoryConfigService.getByProductId(productId);
            return config != null ? ResponseEntity.ok(config) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/alert-needed")
    @ApiLog("获取需要预警的商品列表")
    public ResponseEntity<List<ProductInventoryConfig>> getProductsNeedAlert() {
        try {
            List<ProductInventoryConfig> configs = productInventoryConfigService.getProductsNeedAlert();
            return ResponseEntity.ok(configs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建商品库存配置")
    public ResponseEntity<ProductInventoryConfig> createProductInventoryConfig(@RequestBody ProductInventoryConfig config) {
        try {
            ProductInventoryConfig savedConfig = productInventoryConfigService.save(config);
            return ResponseEntity.ok(savedConfig);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新商品库存配置")
    public ResponseEntity<ProductInventoryConfig> updateProductInventoryConfig(@PathVariable Long id, @RequestBody ProductInventoryConfig config) {
        try {
            config.setInventoryId(id);
            boolean success = productInventoryConfigService.updateById(config);
            return success ? ResponseEntity.ok(config) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/alert-threshold")
    @ApiLog("更新库存预警阈值")
    public ResponseEntity<Void> updateAlertThreshold(@PathVariable Long id, 
                                                    @RequestParam Integer lowStockThreshold,
                                                    @RequestParam Integer outOfStockThreshold) {
        try {
            boolean success = productInventoryConfigService.updateAlertThreshold(id, lowStockThreshold, outOfStockThreshold);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/safety-stock")
    @ApiLog("更新安全库存设置")
    public ResponseEntity<Void> updateSafetyStock(@PathVariable Long id, @RequestParam Integer safetyStock) {
        try {
            boolean success = productInventoryConfigService.updateSafetyStock(id, safetyStock);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/alert-enabled")
    @ApiLog("启用/禁用库存预警")
    public ResponseEntity<Void> toggleAlertEnabled(@PathVariable Long id, @RequestParam Boolean enabled) {
        try {
            boolean success = productInventoryConfigService.toggleAlertEnabled(id, enabled);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除商品库存配置")
    public ResponseEntity<Void> deleteProductInventoryConfig(@PathVariable Long id) {
        try {
            boolean success = productInventoryConfigService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}