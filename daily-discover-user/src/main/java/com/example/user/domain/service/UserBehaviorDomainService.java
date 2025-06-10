package com.example.user.domain.service;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.UserBehavior;
import com.example.user.domain.model.id.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户行为领域服务接口
 */
public interface UserBehaviorDomainService {

    /**
     * 记录用户行为
     *
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param deviceType 设备类型
     * @param deviceId 设备ID
     * @param ip IP地址
     * @return 用户行为
     */
    UserBehavior recordBehavior(UserId userId, Integer behaviorType, Long targetId, Integer targetType,
                               Integer deviceType, String deviceId, String ip);

    /**
     * 记录用户行为（带数据）
     *
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param deviceType 设备类型
     * @param deviceId 设备ID
     * @param ip IP地址
     * @param behaviorData 行为数据
     * @return 用户行为
     */
    UserBehavior recordBehaviorWithData(UserId userId, Integer behaviorType, Long targetId, Integer targetType,
                                       Integer deviceType, String deviceId, String ip, String behaviorData);

    /**
     * 批量记录用户行为
     *
     * @param behaviors 用户行为列表
     * @return 是否成功
     */
    boolean recordBehaviors(List<UserBehavior> behaviors);

    /**
     * 获取用户行为
     *
     * @param id 行为ID
     * @return 用户行为
     */
    Optional<UserBehavior> getBehavior(Long id);

    /**
     * 获取用户行为列表
     *
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @param limit 限制条数
     * @return 用户行为列表
     */
    List<UserBehavior> getUserBehaviors(UserId userId, Integer behaviorType, Integer limit);

    /**
     * 获取目标行为列表
     *
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param behaviorType 行为类型
     * @param limit 限制条数
     * @return 用户行为列表
     */
    List<UserBehavior> getTargetBehaviors(Long targetId, Integer targetType, Integer behaviorType, Integer limit);

    /**
     * 分页获取用户行为
     *
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<UserBehavior> getUserBehaviorsPage(UserId userId, Integer behaviorType, PageRequest pageRequest);

    /**
     * 删除用户行为
     *
     * @param id 行为ID
     * @return 是否成功
     */
    boolean deleteBehavior(Long id);

    /**
     * 删除用户的所有行为
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteUserBehaviors(UserId userId);

    /**
     * 删除指定时间之前的用户行为
     *
     * @param beforeTime 时间
     * @return 删除的记录数
     */
    int deleteBeforeTime(LocalDateTime beforeTime);

    /**
     * 统计用户行为数量
     *
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @return 行为数量
     */
    int countUserBehaviors(UserId userId, Integer behaviorType);

    /**
     * 统计目标行为数量
     *
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param behaviorType 行为类型
     * @return 行为数量
     */
    int countTargetBehaviors(Long targetId, Integer targetType, Integer behaviorType);

    /**
     * 检查用户是否有对目标的特定行为
     *
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param behaviorType 行为类型
     * @return 是否存在
     */
    boolean hasBehavior(UserId userId, Long targetId, Integer targetType, Integer behaviorType);
}