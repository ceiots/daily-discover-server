package com.dailydiscover.user.service;

import com.dailydiscover.user.dto.UserPointsTransactionResponse;
import com.dailydiscover.user.entity.UserPointsTransaction;

import java.util.List;

/**
 * 用户积分交易记录服务接口
 */
public interface UserPointsTransactionService {

    /**
     * 添加积分交易记录
     * 
     * @param transaction 积分交易记录
     * @return 积分交易记录信息
     */
    UserPointsTransactionResponse addTransaction(UserPointsTransaction transaction);

    /**
     * 获取用户的积分交易记录
     * 
     * @param userId 用户ID
     * @return 积分交易记录列表
     */
    List<UserPointsTransactionResponse> getTransactionsByUserId(Long userId);

    /**
     * 根据交易类型获取用户的积分交易记录
     * 
     * @param userId 用户ID
     * @param transactionType 交易类型
     * @return 积分交易记录列表
     */
    List<UserPointsTransactionResponse> getTransactionsByUserIdAndType(Long userId, String transactionType);

    /**
     * 获取用户的积分交易记录数量
     * 
     * @param userId 用户ID
     * @return 交易记录数量
     */
    int countTransactionsByUserId(Long userId);

    /**
     * 获取用户的积分余额
     * 
     * @param userId 用户ID
     * @return 积分余额
     */
    Integer getPointsBalanceByUserId(Long userId);

    /**
     * 获取用户最近的积分交易记录
     * 
     * @param userId 用户ID
     * @param limit 记录数量限制
     * @return 最近的积分交易记录列表
     */
    List<UserPointsTransactionResponse> getRecentTransactionsByUserId(Long userId, int limit);
}