package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.ProductSkuSpecOption;
import com.dailydiscover.service.ProductSkuSpecOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product-sku-spec-options")
@RequiredArgsConstructor
public class ProductSkuSpecOptionController {

    private final ProductSkuSpecOptionService productSkuSpecOptionService;

    @GetMapping
    @ApiLog("获取所有SKU规格选项")
    public ResponseEntity<List<ProductSkuSpecOption>> getAllProductSkuSpecOptions() {
        try {
            List<ProductSkuSpecOption> options = productSkuSpecOptionService.list();
            return ResponseEntity.ok(options);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取SKU规格选项")
    public ResponseEntity<ProductSkuSpecOption> getProductSkuSpecOptionById(@PathVariable Long id) {
        try {
            ProductSkuSpecOption option = productSkuSpecOptionService.getById(id);
            return option != null ? ResponseEntity.ok(option) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/spec/{specId}")
    @ApiLog("根据规格ID获取选项")
    public ResponseEntity<List<ProductSkuSpecOption>> getProductSkuSpecOptionsBySpecId(@PathVariable Long specId) {
        try {
            List<ProductSkuSpecOption> options = productSkuSpecOptionService.getOptionsBySpecId(specId);
            return ResponseEntity.ok(options);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/product/{productId}")
    @ApiLog("根据商品ID获取规格选项")
    public ResponseEntity<List<ProductSkuSpecOption>> getProductSkuSpecOptionsByProductId(@PathVariable Long productId) {
        try {
            List<ProductSkuSpecOption> options = productSkuSpecOptionService.getOptionsByProductId(productId);
            return ResponseEntity.ok(options);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建SKU规格选项")
    public ResponseEntity<ProductSkuSpecOption> createProductSkuSpecOption(@RequestBody ProductSkuSpecOption option) {
        try {
            boolean success = productSkuSpecOptionService.save(option);
            return success ? ResponseEntity.ok(option) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新SKU规格选项")
    public ResponseEntity<ProductSkuSpecOption> updateProductSkuSpecOption(@PathVariable Long id, @RequestBody ProductSkuSpecOption option) {
        try {
            option.setId(id);
            boolean success = productSkuSpecOptionService.updateById(option);
            return success ? ResponseEntity.ok(option) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除SKU规格选项")
    public ResponseEntity<Void> deleteProductSkuSpecOption(@PathVariable Long id) {
        try {
            boolean success = productSkuSpecOptionService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}