package com.example.user.domain.repository;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.UserPointsLog;
import com.example.user.domain.model.id.UserId;

import java.util.List;
import java.util.Optional;

/**
 * 用户积分记录仓储接口
 */
public interface UserPointsLogRepository {

    /**
     * 通过ID获取用户积分记录
     *
     * @param id 记录ID
     * @return 用户积分记录对象
     */
    Optional<UserPointsLog> findById(Long id);

    /**
     * 保存用户积分记录
     *
     * @param pointsLog 用户积分记录对象
     * @return 保存后的用户积分记录对象
     */
    UserPointsLog save(UserPointsLog pointsLog);

    /**
     * 查询用户积分记录列表
     *
     * @param userId 用户ID
     * @param limit  限制条数
     * @return 用户积分记录列表
     */
    List<UserPointsLog> findByUserId(UserId userId, Integer limit);

    /**
     * 查询用户积分记录列表（分页）
     *
     * @param userId      用户ID
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<UserPointsLog> findPage(UserId userId, PageRequest pageRequest);
    
    /**
     * 根据用户ID和类型查询积分记录
     *
     * @param userId 用户ID
     * @param type 类型
     * @param limit 限制条数
     * @return 用户积分记录列表
     */
    List<UserPointsLog> findByUserIdAndType(UserId userId, Integer type, Integer limit);
}