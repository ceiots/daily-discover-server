package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.*;
import com.dailydiscover.service.ProductDetailService;
import com.dailydiscover.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/{productId}")
@RequiredArgsConstructor
public class ProductDetailController {
    
    private final ProductDetailService productDetailService;
    private final ProductService productService;

    @GetMapping("/images")
    @ApiLog("获取商品图片列表")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ProductImage>> getProductImages(@PathVariable Long productId) {
        try {
            List<ProductImage> images = productDetailService.getProductImages(productId);
            return ResponseEntity.ok(images);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/specifications")
    @ApiLog("获取商品规格参数")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ProductSpec>> getProductSpecifications(@PathVariable Long productId) {
        try {
            List<ProductSpec> specs = productDetailService.getProductSpecifications(productId);
            return ResponseEntity.ok(specs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/skus")
    @ApiLog("获取商品SKU列表")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ProductSku>> getProductSKUs(@PathVariable Long productId) {
        try {
            List<ProductSku> skus = productDetailService.getProductSKUs(productId);
            return ResponseEntity.ok(skus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/details")
    @ApiLog("获取商品详情信息")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ProductDetail> getProductDetailInfo(@PathVariable Long productId) {
        try {
            ProductDetail detail = productDetailService.getProductDetailInfo(productId);
            if (detail != null) {
                return ResponseEntity.ok(detail);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/features")
    @ApiLog("获取商品特性")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<String>> getProductFeatures(@PathVariable Long productId) {
        try {
            List<String> features = productDetailService.getProductFeatures(productId);
            return ResponseEntity.ok(features);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/related")
    @ApiLog("获取相关商品推荐")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<Product>> getRelatedProducts(@PathVariable Long productId) {
        try {
            Product product = productService.findById(productId);
            if (product != null) {
                List<Product> products = productService.findByCategoryId(product.getCategoryId());
                products = products.stream()
                    .filter(p -> !p.getId().equals(productId))
                    .limit(10)
                    .collect(Collectors.toList());
                return ResponseEntity.ok(products);
            }
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
