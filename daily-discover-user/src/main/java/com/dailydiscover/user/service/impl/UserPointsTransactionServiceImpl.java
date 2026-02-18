package com.dailydiscover.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dailydiscover.user.dto.UserPointsTransactionResponse;
import com.dailydiscover.user.entity.UserPointsTransaction;
import com.dailydiscover.user.mapper.UserPointsTransactionMapper;
import com.dailydiscover.user.service.UserPointsTransactionService;
import com.dailydiscover.common.util.LogTracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户积分交易记录服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserPointsTransactionServiceImpl implements UserPointsTransactionService {

    private final UserPointsTransactionMapper userPointsTransactionMapper;

    @Override
    @Transactional
    public UserPointsTransactionResponse addTransaction(UserPointsTransaction transaction) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(transaction, null);
        
        try {
            transaction.setTransactionTime(LocalDateTime.now());
            transaction.setCreatedAt(LocalDateTime.now());
            transaction.setUpdatedAt(LocalDateTime.now());
            
            int result = userPointsTransactionMapper.insert(transaction);
            LogTracer.traceDatabaseQuery("INSERT INTO user_points_transactions", new Object[]{transaction}, result);
            
            if (result <= 0) {
                throw new RuntimeException("添加积分交易记录失败");
            }
            
            UserPointsTransactionResponse response = convertToResponse(transaction);
            LogTracer.traceBusinessMethod(transaction, response);
            LogTracer.traceBusinessPerformance(startTime);
            
            return response;
        } catch (Exception e) {
            log.error("添加积分交易记录异常", e);
            throw new RuntimeException("添加积分交易记录异常: " + e.getMessage(), e);
        }
    }

    @Override
    public List<UserPointsTransactionResponse> getTransactionsByUserId(Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(userId, null);
        
        try {
            QueryWrapper<UserPointsTransaction> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                       .orderByDesc("transaction_time");
            
            List<UserPointsTransaction> transactions = userPointsTransactionMapper.selectList(queryWrapper);
            LogTracer.traceDatabaseQuery("SELECT * FROM user_points_transactions WHERE user_id = ? ORDER BY transaction_time DESC", 
                new Object[]{userId}, transactions.size());
            
            List<UserPointsTransactionResponse> responses = transactions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            LogTracer.traceBusinessMethod(userId, responses);
            LogTracer.traceBusinessPerformance(startTime);
            
            return responses;
        } catch (Exception e) {
            log.error("获取用户积分交易记录异常", e);
            throw new RuntimeException("获取用户积分交易记录异常: " + e.getMessage(), e);
        }
    }

    @Override
    public List<UserPointsTransactionResponse> getTransactionsByUserIdAndType(Long userId, String transactionType) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(new Object[]{userId, transactionType}, null);
        
        try {
            QueryWrapper<UserPointsTransaction> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                       .eq("transaction_type", transactionType)
                       .orderByDesc("transaction_time");
            
            List<UserPointsTransaction> transactions = userPointsTransactionMapper.selectList(queryWrapper);
            LogTracer.traceDatabaseQuery("SELECT * FROM user_points_transactions WHERE user_id = ? AND transaction_type = ? ORDER BY transaction_time DESC", 
                new Object[]{userId, transactionType}, transactions.size());
            
            List<UserPointsTransactionResponse> responses = transactions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            LogTracer.traceBusinessMethod(new Object[]{userId, transactionType}, responses);
            LogTracer.traceBusinessPerformance(startTime);
            
            return responses;
        } catch (Exception e) {
            log.error("获取用户积分交易记录异常", e);
            throw new RuntimeException("获取用户积分交易记录异常: " + e.getMessage(), e);
        }
    }

    @Override
    public int countTransactionsByUserId(Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(userId, null);
        
        try {
            QueryWrapper<UserPointsTransaction> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            
            Long count = userPointsTransactionMapper.selectCount(queryWrapper);
            LogTracer.traceDatabaseQuery("SELECT COUNT(*) FROM user_points_transactions WHERE user_id = ?", 
                new Object[]{userId}, count);
            
            LogTracer.traceBusinessMethod(userId, count);
            LogTracer.traceBusinessPerformance(startTime);
            
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            log.error("统计用户积分交易记录异常", e);
            throw new RuntimeException("统计用户积分交易记录异常: " + e.getMessage(), e);
        }
    }

    @Override
    public Integer getPointsBalanceByUserId(Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(userId, null);
        
        try {
            QueryWrapper<UserPointsTransaction> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                       .orderByDesc("transaction_time")
                       .last("LIMIT 1");
            
            UserPointsTransaction latestTransaction = userPointsTransactionMapper.selectOne(queryWrapper);
            LogTracer.traceDatabaseQuery("SELECT * FROM user_points_transactions WHERE user_id = ? ORDER BY transaction_time DESC LIMIT 1", 
                new Object[]{userId}, latestTransaction != null ? 1 : 0);
            
            Integer balance = latestTransaction != null ? latestTransaction.getPointsBalance() : 0;
            
            LogTracer.traceBusinessMethod(userId, balance);
            LogTracer.traceBusinessPerformance(startTime);
            
            return balance;
        } catch (Exception e) {
            log.error("获取用户积分余额异常", e);
            throw new RuntimeException("获取用户积分余额异常: " + e.getMessage(), e);
        }
    }

    @Override
    public List<UserPointsTransactionResponse> getRecentTransactionsByUserId(Long userId, int limit) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(new Object[]{userId, limit}, null);
        
        try {
            QueryWrapper<UserPointsTransaction> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                       .orderByDesc("transaction_time")
                       .last("LIMIT " + limit);
            
            List<UserPointsTransaction> transactions = userPointsTransactionMapper.selectList(queryWrapper);
            LogTracer.traceDatabaseQuery("SELECT * FROM user_points_transactions WHERE user_id = ? ORDER BY transaction_time DESC LIMIT " + limit, 
                new Object[]{userId}, transactions.size());
            
            List<UserPointsTransactionResponse> responses = transactions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            LogTracer.traceBusinessMethod(new Object[]{userId, limit}, responses);
            LogTracer.traceBusinessPerformance(startTime);
            
            return responses;
        } catch (Exception e) {
            log.error("获取用户最近积分交易记录异常", e);
            throw new RuntimeException("获取用户最近积分交易记录异常: " + e.getMessage(), e);
        }
    }

    /**
     * 将UserPointsTransaction实体转换为UserPointsTransactionResponse DTO
     */
    private UserPointsTransactionResponse convertToResponse(UserPointsTransaction transaction) {
        UserPointsTransactionResponse response = new UserPointsTransactionResponse();
        BeanUtils.copyProperties(transaction, response);
        return response;
    }
}