package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.service.ProductActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/{productId}")
@RequiredArgsConstructor
public class ProductActionController {
    
    private final ProductActionService productActionService;

    @PostMapping("/favorite")
    @ApiLog("收藏/取消收藏商品")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> toggleFavorite(@PathVariable Long productId) {
        try {
            Map<String, Object> result = productActionService.toggleFavorite(productId);
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
            Map<String, Object> result = productActionService.getFavoriteStatus(productId);
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
            Map<String, Object> result = productActionService.shareProduct(productId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
