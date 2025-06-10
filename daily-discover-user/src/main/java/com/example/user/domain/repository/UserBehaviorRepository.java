package com.example.user.domain.repository;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.UserBehavior;
import com.example.user.domain.model.id.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户行为仓储接口
 */
public interface UserBehaviorRepository {

    /**
     * 根据ID查询用户行为
     *
     * @param id 行为ID
     * @return 用户行为
     */
    Optional<UserBehavior> findById(Long id);

    /**
     * 根据用户ID查询用户行为列表
     *
     * @param userId 用户ID
     * @param limit 限制条数
     * @return 用户行为列表
     */
    List<UserBehavior> findByUserId(UserId userId, Integer limit);

    /**
     * 根据用户ID和行为类型查询用户行为列表
     *
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @param limit 限制条数
     * @return 用户行为列表
     */
    List<UserBehavior> findByUserIdAndType(UserId userId, Integer behaviorType, Integer limit);

    /**
     * 根据目标ID和目标类型查询用户行为列表
     *
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param limit 限制条数
     * @return 用户行为列表
     */
    List<UserBehavior> findByTarget(Long targetId, Integer targetType, Integer limit);

    /**
     * 分页查询用户行为
     *
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<UserBehavior> findPage(UserId userId, Integer behaviorType, PageRequest pageRequest);

    /**
     * 保存用户行为
     *
     * @param userBehavior 用户行为
     * @return 保存后的用户行为
     */
    UserBehavior save(UserBehavior userBehavior);

    /**
     * 批量保存用户行为
     *
     * @param userBehaviors 用户行为列表
     * @return 是否保存成功
     */
    boolean saveBatch(List<UserBehavior> userBehaviors);

    /**
     * 删除用户行为
     *
     * @param id 行为ID
     * @return 是否删除成功
     */
    boolean delete(Long id);

    /**
     * 删除用户的所有行为
     *
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteByUserId(UserId userId);

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
    int countByUserIdAndType(UserId userId, Integer behaviorType);

    /**
     * 统计目标的行为数量
     *
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param behaviorType 行为类型
     * @return 行为数量
     */
    int countByTargetAndType(Long targetId, Integer targetType, Integer behaviorType);

    /**
     * 检查用户是否有对目标的特定行为
     *
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param behaviorType 行为类型
     * @return 是否存在
     */
    boolean exists(UserId userId, Long targetId, Integer targetType, Integer behaviorType);
}