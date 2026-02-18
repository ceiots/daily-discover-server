package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.ProductSalesStats;
import com.dailydiscover.service.ProductSalesStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product-sales-stats")
@RequiredArgsConstructor
public class ProductSalesStatsController {

    private final ProductSalesStatsService productSalesStatsService;

    @GetMapping
    @ApiLog("获取所有商品销售统计")
    public ResponseEntity<List<ProductSalesStats>> getAllProductSalesStats() {
        try {
            List<ProductSalesStats> stats = productSalesStatsService.list();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取商品销售统计")
    public ResponseEntity<ProductSalesStats> getProductSalesStatsById(@PathVariable Long id) {
        try {
            ProductSalesStats stats = productSalesStatsService.getById(id);
            return stats != null ? ResponseEntity.ok(stats) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/product/{productId}")
    @ApiLog("根据商品ID获取销售统计")
    public ResponseEntity<ProductSalesStats> getProductSalesStatsByProductId(@PathVariable Long productId) {
        try {
            ProductSalesStats stats = productSalesStatsService.getByProductId(productId);
            return stats != null ? ResponseEntity.ok(stats) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/sku/{skuId}")
    @ApiLog("根据SKU ID获取销售统计")
    public ResponseEntity<ProductSalesStats> getProductSalesStatsBySkuId(@PathVariable Long skuId) {
        try {
            ProductSalesStats stats = productSalesStatsService.getBySkuId(skuId);
            return stats != null ? ResponseEntity.ok(stats) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/top-selling")
    @ApiLog("获取热销商品统计")
    public ResponseEntity<List<ProductSalesStats>> getTopSellingProducts(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<ProductSalesStats> stats = productSalesStatsService.getTopSellingProducts(limit);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建商品销售统计")
    public ResponseEntity<ProductSalesStats> createProductSalesStats(@RequestBody ProductSalesStats stats) {
        try {
            boolean success = productSalesStatsService.save(stats);
            return success ? ResponseEntity.ok(stats) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新商品销售统计")
    public ResponseEntity<ProductSalesStats> updateProductSalesStats(@PathVariable Long id, @RequestBody ProductSalesStats stats) {
        try {
            stats.setId(id);
            boolean success = productSalesStatsService.updateById(stats);
            return success ? ResponseEntity.ok(stats) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除商品销售统计")
    public ResponseEntity<Void> deleteProductSalesStats(@PathVariable Long id) {
        try {
            boolean success = productSalesStatsService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}