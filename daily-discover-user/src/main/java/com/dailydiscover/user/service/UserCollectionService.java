package com.dailydiscover.user.service;

import com.dailydiscover.user.dto.UserCollectionResponse;
import com.dailydiscover.user.entity.UserCollection;

import java.util.List;

/**
 * 用户收藏服务接口
 */
public interface UserCollectionService {

    /**
     * 添加收藏
     * 
     * @param userCollection 收藏信息
     * @return 收藏记录信息
     */
    UserCollectionResponse addCollection(UserCollection userCollection);

    /**
     * 获取用户的收藏列表
     * 
     * @param userId 用户ID
     * @return 收藏列表
     */
    List<UserCollectionResponse> getCollectionsByUserId(Long userId);

    /**
     * 删除收藏
     * 
     * @param id 收藏记录ID
     * @return 是否成功
     */
    boolean deleteCollection(Long id);

    /**
     * 检查用户是否已收藏某个内容
     * 
     * @param userId 用户ID
     * @param itemType 内容类型
     * @param itemId 内容ID
     * @return 是否已收藏
     */
    boolean isItemCollected(Long userId, String itemType, String itemId);

    /**
     * 统计用户收藏数量
     * 
     * @param userId 用户ID
     * @return 收藏数量
     */
    int countCollections(Long userId);

    /**
     * 清空用户的收藏
     * 
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean clearCollections(Long userId);
}