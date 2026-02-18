package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.ProductSku;
import com.dailydiscover.service.ProductSkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product/sku")
@RequiredArgsConstructor
public class ProductSkuController {

    private final ProductSkuService productSkuService;

    @GetMapping
    @ApiLog("获取所有SKU信息")
    public ResponseEntity<List<ProductSku>> getAllSkus() {
        try {
            List<ProductSku> skus = productSkuService.list();
            return ResponseEntity.ok(skus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取SKU信息")
    public ResponseEntity<ProductSku> getSkuById(@PathVariable Long id) {
        try {
            ProductSku sku = productSkuService.getById(id);
            return sku != null ? ResponseEntity.ok(sku) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/product/{productId}")
    @ApiLog("根据商品ID获取SKU列表")
    public ResponseEntity<List<ProductSku>> getSkusByProductId(@PathVariable Long productId) {
        try {
            List<ProductSku> skus = productSkuService.findByProductId(productId);
            return ResponseEntity.ok(skus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/code/{skuCode}")
    @ApiLog("根据SKU编码获取SKU信息")
    public ResponseEntity<ProductSku> getSkuByCode(@PathVariable String skuCode) {
        try {
            ProductSku sku = productSkuService.findBySkuCode(skuCode);
            return sku != null ? ResponseEntity.ok(sku) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建SKU信息")
    public ResponseEntity<ProductSku> createSku(@RequestBody ProductSku sku) {
        try {
            boolean success = productSkuService.save(sku);
            return success ? ResponseEntity.ok(sku) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新SKU信息")
    public ResponseEntity<ProductSku> updateSku(@PathVariable Long id, @RequestBody ProductSku sku) {
        try {
            sku.setId(id);
            boolean success = productSkuService.updateById(sku);
            return success ? ResponseEntity.ok(sku) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除SKU信息")
    public ResponseEntity<Void> deleteSku(@PathVariable Long id) {
        try {
            boolean success = productSkuService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}