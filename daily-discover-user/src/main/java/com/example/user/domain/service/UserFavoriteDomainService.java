package com.example.user.domain.service;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.UserFavorite;
import com.example.user.domain.model.id.UserId;

import java.util.List;

/**
 * 用户收藏领域服务接口
 */
public interface UserFavoriteDomainService extends BaseDomainService {

    /**
     * 添加收藏
     *
     * @param userFavorite 用户收藏
     * @return 保存后的用户收藏
     */
    UserFavorite addFavorite(UserFavorite userFavorite);

    /**
     * 取消收藏
     *
     * @param userId 用户ID
     * @param type 收藏类型
     * @param targetId 收藏对象ID
     * @return 是否取消成功
     */
    boolean cancelFavorite(UserId userId, Integer type, Long targetId);

    /**
     * 获取用户收藏列表
     *
     * @param userId 用户ID
     * @param type 收藏类型
     * @param limit 限制数量
     * @return 用户收藏列表
     */
    List<UserFavorite> getUserFavorites(UserId userId, Integer type, Integer limit);

    /**
     * 分页获取用户收藏
     *
     * @param userId 用户ID
     * @param type 收藏类型
     * @param pageRequest 分页请求
     * @return 用户收藏分页结果
     */
    PageResult<UserFavorite> getUserFavoritePage(UserId userId, Integer type, PageRequest pageRequest);

    /**
     * 检查用户是否收藏
     *
     * @param userId 用户ID
     * @param type 收藏类型
     * @param targetId 收藏对象ID
     * @return 是否已收藏
     */
    boolean isFavorite(UserId userId, Integer type, Long targetId);

    /**
     * 统计收藏数量
     *
     * @param type 收藏类型
     * @param targetId 收藏对象ID
     * @return 收藏数量
     */
    Long countFavorites(Integer type, Long targetId);

    /**
     * 更新收藏备注
     *
     * @param userId 用户ID
     * @param type 收藏类型
     * @param targetId 收藏对象ID
     * @param note 备注
     * @return 是否更新成功
     */
    boolean updateFavoriteNote(UserId userId, Integer type, Long targetId, String note);
}