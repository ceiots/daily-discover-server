package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.Product;
import com.dailydiscover.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/{productId}")
@RequiredArgsConstructor
public class ProductActionController {
    
    private final ProductService productService;

    @PostMapping("/favorite")
    @ApiLog("收藏/取消收藏商品")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> toggleFavorite(@PathVariable Long productId) {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("productId", productId);
            result.put("favorited", true);
            result.put("message", "收藏成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/favorite")
    @ApiLog("获取商品收藏状态")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getFavoriteStatus(@PathVariable Long productId) {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("productId", productId);
            result.put("favorited", false);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/share")
    @ApiLog("分享商品")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, Object>> shareProduct(@PathVariable Long productId) {
        try {
            Product product = productService.findById(productId);
            Map<String, Object> result = new HashMap<>();
            if (product != null) {
                result.put("productId", productId);
                result.put("title", product.getTitle());
                result.put("imageUrl", product.getMainImageUrl());
                result.put("shareUrl", "/product/" + productId);
                result.put("success", true);
                return ResponseEntity.ok(result);
            }
            result.put("success", false);
            result.put("message", "商品不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
