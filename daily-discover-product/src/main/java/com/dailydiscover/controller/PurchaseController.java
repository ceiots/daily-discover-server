package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/purchase")
@RequiredArgsConstructor
public class PurchaseController {

    @GetMapping("/history")
    @ApiLog("获取购买历史")
    public ResponseEntity<List<Map<String, Object>>> getPurchaseHistory() {
        try {
            List<Map<String, Object>> history = new ArrayList<>();
            
            // 暂时返回空列表
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/history/{productId}")
    @ApiLog("获取商品购买历史")
    public ResponseEntity<List<Map<String, Object>>> getPurchaseHistoryByProduct(@PathVariable String productId) {
        try {
            List<Map<String, Object>> history = new ArrayList<>();
            
            // 暂时返回空列表
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}