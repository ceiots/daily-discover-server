package com.example.user.application.service;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.application.dto.UserFavoriteDTO;

import java.util.List;

/**
 * 用户收藏应用服务接口
 */
public interface UserFavoriteService extends BaseApplicationService {

    /**
     * 添加收藏
     *
     * @param userFavoriteDTO 用户收藏DTO
     * @return 用户收藏DTO
     */
    UserFavoriteDTO addFavorite(UserFavoriteDTO userFavoriteDTO);

    /**
     * 取消收藏
     *
     * @param userId 用户ID
     * @param type 收藏类型
     * @param targetId 收藏对象ID
     * @return 是否取消成功
     */
    boolean cancelFavorite(Long userId, Integer type, Long targetId);

    /**
     * 获取用户收藏列表
     *
     * @param userId 用户ID
     * @param type 收藏类型
     * @param limit 限制数量
     * @return 用户收藏DTO列表
     */
    List<UserFavoriteDTO> getUserFavorites(Long userId, Integer type, Integer limit);

    /**
     * 分页获取用户收藏
     *
     * @param userId 用户ID
     * @param type 收藏类型
     * @param pageRequest 分页请求
     * @return 用户收藏分页结果
     */
    PageResult<UserFavoriteDTO> getUserFavoritePage(Long userId, Integer type, PageRequest pageRequest);

    /**
     * 检查用户是否收藏
     *
     * @param userId 用户ID
     * @param type 收藏类型
     * @param targetId 收藏对象ID
     * @return 是否已收藏
     */
    boolean isFavorite(Long userId, Integer type, Long targetId);

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
    boolean updateFavoriteNote(Long userId, Integer type, Long targetId, String note);
}