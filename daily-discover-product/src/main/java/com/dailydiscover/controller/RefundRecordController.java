package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.RefundRecord;
import com.dailydiscover.service.RefundRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/refund-records")
@RequiredArgsConstructor
public class RefundRecordController {

    private final RefundRecordService refundRecordService;

    @GetMapping
    @ApiLog("获取所有退款记录")
    public ResponseEntity<List<RefundRecord>> getAllRefundRecords() {
        try {
            List<RefundRecord> records = refundRecordService.list();
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取退款记录")
    public ResponseEntity<RefundRecord> getRefundRecordById(@PathVariable Long id) {
        try {
            RefundRecord record = refundRecordService.getById(id);
            return record != null ? ResponseEntity.ok(record) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/order/{orderId}")
    @ApiLog("根据订单ID获取退款记录")
    public ResponseEntity<List<RefundRecord>> getRefundRecordsByOrderId(@PathVariable Long orderId) {
        try {
            List<RefundRecord> records = refundRecordService.getByOrderId(orderId);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/transaction/{transactionId}")
    @ApiLog("根据支付交易ID获取退款记录")
    public ResponseEntity<List<RefundRecord>> getRefundRecordsByTransactionId(@PathVariable Long transactionId) {
        try {
            List<RefundRecord> records = refundRecordService.getByPaymentTransactionId(transactionId);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/refund-no/{refundNo}")
    @ApiLog("根据退款单号获取退款记录")
    public ResponseEntity<RefundRecord> getRefundRecordByRefundNo(@PathVariable String refundNo) {
        try {
            RefundRecord record = refundRecordService.getByRefundNo(refundNo);
            return record != null ? ResponseEntity.ok(record) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/stats")
    @ApiLog("获取退款统计信息")
    public ResponseEntity<Map<String, Object>> getRefundStats(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            Map<String, Object> stats = refundRecordService.getRefundStats(startDate, endDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建退款记录")
    public ResponseEntity<RefundRecord> createRefundRecord(@RequestBody RefundRecord record) {
        try {
            boolean success = refundRecordService.save(record);
            return success ? ResponseEntity.ok(record) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新退款记录")
    public ResponseEntity<RefundRecord> updateRefundRecord(@PathVariable Long id, @RequestBody RefundRecord record) {
        try {
            record.setId(id);
            boolean success = refundRecordService.updateById(record);
            return success ? ResponseEntity.ok(record) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除退款记录")
    public ResponseEntity<Void> deleteRefundRecord(@PathVariable Long id) {
        try {
            boolean success = refundRecordService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}