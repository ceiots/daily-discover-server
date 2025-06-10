package com.example.user.domain.repository;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.UserPointsLog;
import com.example.user.domain.model.id.UserId;

import java.util.List;

/**
 * 用户积分记录仓储接口
 */
public interface UserPointsLogRepository {
    
    /**
     * 保存积分记录
     *
     * @param pointsLog 积分记录
     * @return 保存后的积分记录
     */
    UserPointsLog save(UserPointsLog pointsLog);
    
    /**
     * 根据用户ID查询积分记录
     *
     * @param userId 用户ID
     * @return 积分记录列表
     */
    List<UserPointsLog> findByUserId(UserId userId);
    
    /**
     * 根据用户ID分页查询积分记录
     *
     * @param userId 用户ID
     * @param pageRequest 分页请求
     * @return 积分记录分页结果
     */
    PageResult<UserPointsLog> findByUserId(UserId userId, PageRequest pageRequest);
    
    /**
     * 根据ID查询积分记录
     *
     * @param id 记录ID
     * @return 积分记录
     */
    UserPointsLog findById(Long id);
    
    /**
     * 更新积分记录
     *
     * @param pointsLog 积分记录
     * @return 更新后的积分记录
     */
    UserPointsLog update(UserPointsLog pointsLog);
    
    /**
     * 删除积分记录
     *
     * @param id 记录ID
     * @return 是否删除成功
     */
    boolean delete(Long id);
}