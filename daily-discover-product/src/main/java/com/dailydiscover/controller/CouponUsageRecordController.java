package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.CouponUsageRecord;
import com.dailydiscover.service.CouponUsageRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coupon-usage-records")
@RequiredArgsConstructor
public class CouponUsageRecordController {

    private final CouponUsageRecordService couponUsageRecordService;

    @GetMapping
    @ApiLog("获取所有优惠券使用记录")
    public ResponseEntity<List<CouponUsageRecord>> getAllCouponUsageRecords() {
        try {
            List<CouponUsageRecord> records = couponUsageRecordService.list();
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取优惠券使用记录")
    public ResponseEntity<CouponUsageRecord> getCouponUsageRecordById(@PathVariable Long id) {
        try {
            CouponUsageRecord record = couponUsageRecordService.getById(id);
            return record != null ? ResponseEntity.ok(record) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    @ApiLog("根据用户ID查询优惠券使用记录")
    public ResponseEntity<List<CouponUsageRecord>> getCouponUsageRecordsByUserId(@PathVariable Long userId) {
        try {
            List<CouponUsageRecord> records = couponUsageRecordService.getByUserId(userId);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/coupon/{couponId}")
    @ApiLog("根据优惠券ID查询使用记录")
    public ResponseEntity<List<CouponUsageRecord>> getCouponUsageRecordsByCouponId(@PathVariable Long couponId) {
        try {
            List<CouponUsageRecord> records = couponUsageRecordService.getByCouponId(couponId);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/order/{orderId}")
    @ApiLog("根据订单ID查询优惠券使用记录")
    public ResponseEntity<CouponUsageRecord> getCouponUsageRecordByOrderId(@PathVariable Long orderId) {
        try {
            CouponUsageRecord record = couponUsageRecordService.getByOrderId(orderId);
            return record != null ? ResponseEntity.ok(record) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}/stats")
    @ApiLog("获取用户优惠券使用统计")
    public ResponseEntity<Map<String, Object>> getUserCouponUsageStats(@PathVariable Long userId) {
        try {
            Map<String, Object> stats = couponUsageRecordService.getUserCouponUsageStats(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/stats")
    @ApiLog("获取优惠券使用统计")
    public ResponseEntity<Map<String, Object>> getCouponUsageStats() {
        try {
            Map<String, Object> stats = couponUsageRecordService.getCouponUsageStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建优惠券使用记录")
    public ResponseEntity<CouponUsageRecord> createCouponUsageRecord(@RequestBody CouponUsageRecord record) {
        try {
            boolean success = couponUsageRecordService.save(record);
            return success ? ResponseEntity.ok(record) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新优惠券使用记录")
    public ResponseEntity<CouponUsageRecord> updateCouponUsageRecord(@PathVariable Long id, @RequestBody CouponUsageRecord record) {
        try {
            record.setId(id);
            boolean success = couponUsageRecordService.updateById(record);
            return success ? ResponseEntity.ok(record) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除优惠券使用记录")
    public ResponseEntity<Void> deleteCouponUsageRecord(@PathVariable Long id) {
        try {
            boolean success = couponUsageRecordService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}