package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.ProductSkuSpec;
import com.dailydiscover.service.ProductSkuSpecService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product-sku-specs")
@RequiredArgsConstructor
public class ProductSkuSpecController {

    private final ProductSkuSpecService productSkuSpecService;

    @GetMapping
    @ApiLog("获取所有SKU规格")
    public ResponseEntity<List<ProductSkuSpec>> getAllProductSkuSpecs() {
        try {
            List<ProductSkuSpec> specs = productSkuSpecService.list();
            return ResponseEntity.ok(specs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取SKU规格")
    public ResponseEntity<ProductSkuSpec> getProductSkuSpecById(@PathVariable Long id) {
        try {
            ProductSkuSpec spec = productSkuSpecService.getById(id);
            return spec != null ? ResponseEntity.ok(spec) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/product/{productId}")
    @ApiLog("根据商品ID获取SKU规格")
    public ResponseEntity<List<ProductSkuSpec>> getProductSkuSpecsByProductId(@PathVariable Long productId) {
        try {
            List<ProductSkuSpec> specs = productSkuSpecService.getSpecsByProductId(productId);
            return ResponseEntity.ok(specs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建SKU规格")
    public ResponseEntity<ProductSkuSpec> createProductSkuSpec(@RequestBody ProductSkuSpec spec) {
        try {
            boolean success = productSkuSpecService.save(spec);
            return success ? ResponseEntity.ok(spec) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新SKU规格")
    public ResponseEntity<ProductSkuSpec> updateProductSkuSpec(@PathVariable Long id, @RequestBody ProductSkuSpec spec) {
        try {
            spec.setId(id);
            boolean success = productSkuSpecService.updateById(spec);
            return success ? ResponseEntity.ok(spec) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除SKU规格")
    public ResponseEntity<Void> deleteProductSkuSpec(@PathVariable Long id) {
        try {
            boolean success = productSkuSpecService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}