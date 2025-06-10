package com.example.user.application.service;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.application.dto.UserBehaviorDTO;

import java.util.List;

/**
 * 用户行为应用服务接口
 */
public interface UserBehaviorService extends BaseApplicationService {

    /**
     * 记录用户行为
     *
     * @param userBehaviorDTO 用户行为DTO
     * @return 用户行为DTO
     */
    UserBehaviorDTO recordBehavior(UserBehaviorDTO userBehaviorDTO);

    /**
     * 获取用户行为列表
     *
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @param targetType 目标类型
     * @param limit 限制数量
     * @return 用户行为DTO列表
     */
    List<UserBehaviorDTO> getUserBehaviors(Long userId, Integer behaviorType, Integer targetType, Integer limit);

    /**
     * 分页获取用户行为
     *
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @param targetType 目标类型
     * @param pageRequest 分页请求
     * @return 用户行为分页结果
     */
    PageResult<UserBehaviorDTO> getUserBehaviorPage(Long userId, Integer behaviorType, Integer targetType, PageRequest pageRequest);

    /**
     * 获取目标对象的行为统计
     *
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param behaviorType 行为类型
     * @return 行为数量
     */
    Long countBehaviorByTarget(Long targetId, Integer targetType, Integer behaviorType);

    /**
     * 检查用户是否对目标对象有指定行为
     *
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param behaviorType 行为类型
     * @return 是否有行为
     */
    boolean hasUserBehavior(Long userId, Long targetId, Integer targetType, Integer behaviorType);

    /**
     * 删除用户行为
     *
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param behaviorType 行为类型
     * @return 是否删除成功
     */
    boolean deleteBehavior(Long userId, Long targetId, Integer targetType, Integer behaviorType);
}