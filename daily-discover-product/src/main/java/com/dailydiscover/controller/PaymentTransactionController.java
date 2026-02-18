package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.PaymentTransaction;
import com.dailydiscover.service.PaymentTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payment-transactions")
@RequiredArgsConstructor
public class PaymentTransactionController {

    private final PaymentTransactionService paymentTransactionService;

    @GetMapping
    @ApiLog("获取所有支付交易")
    public ResponseEntity<List<PaymentTransaction>> getAllPaymentTransactions() {
        try {
            List<PaymentTransaction> transactions = paymentTransactionService.list();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取支付交易")
    public ResponseEntity<PaymentTransaction> getPaymentTransactionById(@PathVariable Long id) {
        try {
            PaymentTransaction transaction = paymentTransactionService.getById(id);
            return transaction != null ? ResponseEntity.ok(transaction) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/order/{orderId}")
    @ApiLog("根据订单ID获取支付交易")
    public ResponseEntity<PaymentTransaction> getPaymentTransactionByOrderId(@PathVariable Long orderId) {
        try {
            PaymentTransaction transaction = paymentTransactionService.getByOrderId(orderId);
            return transaction != null ? ResponseEntity.ok(transaction) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/transaction-no/{transactionNo}")
    @ApiLog("根据交易号获取支付交易")
    public ResponseEntity<PaymentTransaction> getPaymentTransactionByTransactionNo(@PathVariable String transactionNo) {
        try {
            PaymentTransaction transaction = paymentTransactionService.getByTransactionNo(transactionNo);
            return transaction != null ? ResponseEntity.ok(transaction) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    @ApiLog("根据用户ID获取支付交易")
    public ResponseEntity<List<PaymentTransaction>> getPaymentTransactionsByUserId(@PathVariable Long userId) {
        try {
            List<PaymentTransaction> transactions = paymentTransactionService.getByUserId(userId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/transaction-no/{transactionNo}/status")
    @ApiLog("查询支付状态")
    public ResponseEntity<Map<String, Object>> queryPaymentStatus(@PathVariable String transactionNo) {
        try {
            Map<String, Object> status = paymentTransactionService.queryPaymentStatus(transactionNo);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/stats")
    @ApiLog("获取支付统计信息")
    public ResponseEntity<Map<String, Object>> getPaymentStats(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            Map<String, Object> stats = paymentTransactionService.getPaymentStats(startDate, endDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建支付交易")
    public ResponseEntity<PaymentTransaction> createPaymentTransaction(@RequestBody PaymentTransaction transaction) {
        try {
            boolean success = paymentTransactionService.save(transaction);
            return success ? ResponseEntity.ok(transaction) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/create")
    @ApiLog("创建支付交易")
    public ResponseEntity<PaymentTransaction> createTransaction(
            @RequestParam Long orderId,
            @RequestParam Long paymentMethodId,
            @RequestParam BigDecimal amount,
            @RequestParam String currency) {
        try {
            PaymentTransaction transaction = paymentTransactionService.createTransaction(orderId, paymentMethodId, amount, currency);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新支付交易")
    public ResponseEntity<PaymentTransaction> updatePaymentTransaction(@PathVariable Long id, @RequestBody PaymentTransaction transaction) {
        try {
            transaction.setId(id);
            boolean success = paymentTransactionService.updateById(transaction);
            return success ? ResponseEntity.ok(transaction) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/status")
    @ApiLog("更新支付交易状态")
    public ResponseEntity<Void> updateTransactionStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            boolean success = paymentTransactionService.updateTransactionStatus(id, status);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/callback")
    @ApiLog("处理支付回调")
    public ResponseEntity<Void> processPaymentCallback(@RequestParam String transactionNo, @RequestBody Map<String, Object> callbackData) {
        try {
            boolean success = paymentTransactionService.processPaymentCallback(transactionNo, callbackData);
            return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除支付交易")
    public ResponseEntity<Void> deletePaymentTransaction(@PathVariable Long id) {
        try {
            boolean success = paymentTransactionService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}