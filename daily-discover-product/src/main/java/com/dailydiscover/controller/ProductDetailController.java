package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.Product;
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
    public ResponseEntity<List<String>> getProductImages(@PathVariable Long productId) {
        try {
            List<String> images = productDetailService.getProductImages(productId);
            return ResponseEntity.ok(images);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/specifications")
    @ApiLog("获取商品规格参数")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<String>> getProductSpecifications(@PathVariable Long productId) {
        try {
            List<String> specs = productDetailService.getProductSpecifications(productId);
            return ResponseEntity.ok(specs);
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
