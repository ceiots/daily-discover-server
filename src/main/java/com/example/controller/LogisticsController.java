package com.example.controller;

import com.example.model.LogisticsInfo;
import com.example.model.LogisticsCompany;
import com.example.service.LogisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物流控制器
 */
@Slf4j
@RestController
@RequestMapping("/logistics")
public class LogisticsController {

    @Autowired
    private LogisticsService logisticsService;

    /**
     * 根据物流单号查询物流信息
     */
    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<Map<String, Object>> trackLogistics(@PathVariable String trackingNumber) {
        log.info("查询物流信息，物流单号: {}", trackingNumber);
        
        LogisticsInfo logisticsInfo = logisticsService.getLogisticsInfoByTrackingNumber(trackingNumber);
        
        Map<String, Object> result = new HashMap<>();
        if (logisticsInfo != null) {
            result.put("success", true);
            result.put("data", logisticsInfo);
        } else {
            result.put("success", false);
            result.put("message", "未找到物流信息");
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * 根据订单ID查询物流信息
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Map<String, Object>> getLogisticsByOrderId(@PathVariable Long orderId) {
        log.info("查询订单物流信息，订单ID: {}", orderId);
        
        LogisticsInfo logisticsInfo = logisticsService.getLogisticsInfoByOrderId(orderId);
        
        Map<String, Object> result = new HashMap<>();
        if (logisticsInfo != null) {
            result.put("success", true);
            result.put("data", logisticsInfo);
        } else {
            result.put("success", false);
            result.put("message", "未找到物流信息");
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 根据订单ID查询实时物流信息
     * 先从数据库获取基本信息，再从第三方API获取最新轨迹
     */
    @GetMapping("/realtime/order/{orderId}")
    public ResponseEntity<Map<String, Object>> getRealTimeLogisticsByOrderId(@PathVariable Long orderId) {
        log.info("查询订单实时物流信息，订单ID: {}", orderId);
        
        Map<String, Object> result = logisticsService.getRealTimeLogisticsInfoByOrderId(orderId);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 查询所有物流公司
     */
    @GetMapping("/companies")
    public ResponseEntity<Map<String, Object>> getAllLogisticsCompanies() {
        log.info("查询所有物流公司");
        
        List<LogisticsCompany> companies = logisticsService.getAllLogisticsCompanies();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", companies);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 查询所有启用的物流公司
     */
    @GetMapping("/companies/enabled")
    public ResponseEntity<Map<String, Object>> getAllEnabledLogisticsCompanies() {
        log.info("查询所有启用的物流公司");
        
        List<LogisticsCompany> companies = logisticsService.getAllEnabledLogisticsCompanies();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", companies);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 创建物流信息
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createLogistics(
            @RequestParam Long orderId,
            @RequestParam String companyCode) {
        log.info("创建物流信息，订单ID: {}, 物流公司编码: {}", orderId, companyCode);
        
        LogisticsInfo logisticsInfo = logisticsService.createLogisticsInfo(orderId, companyCode);
        
        Map<String, Object> result = new HashMap<>();
        if (logisticsInfo != null) {
            result.put("success", true);
            result.put("data", logisticsInfo);
        } else {
            result.put("success", false);
            result.put("message", "创建物流信息失败");
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * 更新物流状态
     */
    @PostMapping("/update-status")
    public ResponseEntity<Map<String, Object>> updateLogisticsStatus(
            @RequestParam Long logisticsId,
            @RequestParam Integer status,
            @RequestParam String location,
            @RequestParam String description) {
        log.info("更新物流状态，物流ID: {}, 状态: {}, 地点: {}, 描述: {}", 
                logisticsId, status, location, description);
        
        boolean success = logisticsService.updateLogisticsStatus(logisticsId, status, location, description);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        if (!success) {
            result.put("message", "更新物流状态失败");
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * 模拟物流轨迹更新
     */
    @PostMapping("/simulate-update/{logisticsId}")
    public ResponseEntity<Map<String, Object>> simulateLogisticsUpdate(@PathVariable Long logisticsId) {
        log.info("模拟物流轨迹更新，物流ID: {}", logisticsId);
        
        boolean success = logisticsService.simulateLogisticsUpdate(logisticsId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        if (!success) {
            result.put("message", "模拟物流轨迹更新失败");
        }
        
        return ResponseEntity.ok(result);
    }
} 