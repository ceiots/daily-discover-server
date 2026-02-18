package com.dailydiscover.user.controller;

import com.dailydiscover.user.dto.UserPointsTransactionResponse;
import com.dailydiscover.user.entity.UserPointsTransaction;
import com.dailydiscover.user.service.UserPointsTransactionService;
import com.dailydiscover.common.util.LogTracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户积分交易记录控制器
 */
@Slf4j
@RestController
@RequestMapping("/points-transactions")
@RequiredArgsConstructor
public class UserPointsTransactionController {

    private final UserPointsTransactionService userPointsTransactionService;

    /**
     * 添加积分交易记录
     */
    @PostMapping
    public ResponseEntity<UserPointsTransactionResponse> addTransaction(@RequestBody UserPointsTransaction transaction) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(transaction, null);
        
        try {
            UserPointsTransactionResponse response = userPointsTransactionService.addTransaction(transaction);
            
            LogTracer.traceBusinessMethod(transaction, response);
            LogTracer.traceBusinessPerformance(startTime);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取用户的积分交易记录
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserPointsTransactionResponse>> getTransactionsByUserId(@PathVariable Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(userId, null);
        
        try {
            List<UserPointsTransactionResponse> transactions = userPointsTransactionService.getTransactionsByUserId(userId);
            
            LogTracer.traceBusinessMethod(userId, transactions);
            LogTracer.traceBusinessPerformance(startTime);
            
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 根据交易类型获取用户的积分交易记录
     */
    @GetMapping("/user/{userId}/type/{transactionType}")
    public ResponseEntity<List<UserPointsTransactionResponse>> getTransactionsByUserIdAndType(
            @PathVariable Long userId, 
            @PathVariable String transactionType) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(new Object[]{userId, transactionType}, null);
        
        try {
            List<UserPointsTransactionResponse> transactions = userPointsTransactionService.getTransactionsByUserIdAndType(userId, transactionType);
            
            LogTracer.traceBusinessMethod(new Object[]{userId, transactionType}, transactions);
            LogTracer.traceBusinessPerformance(startTime);
            
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取用户的积分交易记录数量
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Integer> countTransactionsByUserId(@PathVariable Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(userId, null);
        
        try {
            int count = userPointsTransactionService.countTransactionsByUserId(userId);
            
            LogTracer.traceBusinessMethod(userId, count);
            LogTracer.traceBusinessPerformance(startTime);
            
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取用户的积分余额
     */
    @GetMapping("/user/{userId}/balance")
    public ResponseEntity<Integer> getPointsBalanceByUserId(@PathVariable Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(userId, null);
        
        try {
            Integer balance = userPointsTransactionService.getPointsBalanceByUserId(userId);
            
            LogTracer.traceBusinessMethod(userId, balance);
            LogTracer.traceBusinessPerformance(startTime);
            
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取用户最近的积分交易记录
     */
    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<UserPointsTransactionResponse>> getRecentTransactionsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(new Object[]{userId, limit}, null);
        
        try {
            List<UserPointsTransactionResponse> transactions = userPointsTransactionService.getRecentTransactionsByUserId(userId, limit);
            
            LogTracer.traceBusinessMethod(new Object[]{userId, limit}, transactions);
            LogTracer.traceBusinessPerformance(startTime);
            
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            return ResponseEntity.badRequest().build();
        }
    }
}