package com.example.user.domain.repository;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.UserFavorite;
import com.example.user.domain.model.id.UserId;

import java.util.List;
import java.util.Optional;

/**
 * 用户收藏仓储接口
 */
public interface UserFavoriteRepository {

    /**
     * 根据ID查询用户收藏
     *
     * @param id 收藏ID
     * @return 用户收藏
     */
    Optional<UserFavorite> findById(Long id);

    /**
     * 根据用户ID、收藏类型和目标ID查询用户收藏
     *
     * @param userId 用户ID
     * @param type 收藏类型
     * @param targetId 目标ID
     * @return 用户收藏
     */
    Optional<UserFavorite> findByUserIdAndTypeAndTargetId(UserId userId, Integer type, Long targetId);

    /**
     * 根据用户ID和收藏类型查询用户收藏列表
     *
     * @param userId 用户ID
     * @param type 收藏类型
     * @return 用户收藏列表
     */
    List<UserFavorite> findByUserIdAndType(UserId userId, Integer type);

    /**
     * 根据目标ID和收藏类型查询用户收藏列表
     *
     * @param targetId 目标ID
     * @param type 收藏类型
     * @return 用户收藏列表
     */
    List<UserFavorite> findByTargetIdAndType(Long targetId, Integer type);

    /**
     * 分页查询用户收藏
     *
     * @param userId 用户ID
     * @param type 收藏类型
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<UserFavorite> findPage(UserId userId, Integer type, PageRequest pageRequest);

    /**
     * 保存用户收藏
     *
     * @param userFavorite 用户收藏
     * @return 保存后的用户收藏
     */
    UserFavorite save(UserFavorite userFavorite);

    /**
     * 删除用户收藏
     *
     * @param id 收藏ID
     * @return 是否删除成功
     */
    boolean delete(Long id);

    /**
     * 根据用户ID、收藏类型和目标ID删除用户收藏
     *
     * @param userId 用户ID
     * @param type 收藏类型
     * @param targetId 目标ID
     * @return 是否删除成功
     */
    boolean deleteByUserIdAndTypeAndTargetId(UserId userId, Integer type, Long targetId);

    /**
     * 根据用户ID和收藏类型删除用户收藏
     *
     * @param userId 用户ID
     * @param type 收藏类型
     * @return 是否删除成功
     */
    boolean deleteByUserIdAndType(UserId userId, Integer type);

    /**
     * 统计用户收藏数量
     *
     * @param userId 用户ID
     * @param type 收藏类型
     * @return 收藏数量
     */
    int countByUserIdAndType(UserId userId, Integer type);

    /**
     * 统计目标被收藏数量
     *
     * @param targetId 目标ID
     * @param type 收藏类型
     * @return 收藏数量
     */
    int countByTargetIdAndType(Long targetId, Integer type);

    /**
     * 检查用户是否收藏了目标
     *
     * @param userId 用户ID
     * @param type 收藏类型
     * @param targetId 目标ID
     * @return 是否收藏
     */
    boolean existsByUserIdAndTypeAndTargetId(UserId userId, Integer type, Long targetId);

    /**
     * 更新用户收藏
     *
     * @param userFavorite 用户收藏对象
     * @return 更新后的用户收藏对象
     */
    UserFavorite update(UserFavorite userFavorite);

    /**
     * 统计目标收藏数
     *
     * @param type 类型
     * @param targetId 目标ID
     * @return 收藏数
     */
    Long countByTypeAndTargetId(Integer type, Long targetId);
}