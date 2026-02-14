package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/purchase")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @GetMapping("/history")
    @ApiLog("获取购买历史")
    public ResponseEntity<List<Map<String, Object>>> getPurchaseHistory() {
        try {
            List<Map<String, Object>> history = purchaseService.getPurchaseHistory();
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/history/{productId}")
    @ApiLog("获取商品购买历史")
    public ResponseEntity<List<Map<String, Object>>> getPurchaseHistoryByProduct(@PathVariable String productId) {
        try {
            List<Map<String, Object>> history = purchaseService.getPurchaseHistoryByProduct(productId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}