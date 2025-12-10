package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.entity.UserCollection;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户收藏Mapper接口
 * 
 */
@Mapper
public interface UserCollectionMapper extends BaseMapper<UserCollection> {

    /**
     * 删除用户的所有收藏
     * 
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(Long userId);

    /**
     * 统计用户收藏数量
     * 
     * @param userId 用户ID
     * @return 收藏数量
     */
    int countByUserId(Long userId);

    /**
     * 检查用户是否已收藏某个内容
     * 
     * @param userId 用户ID
     * @param itemType 内容类型
     * @param itemId 内容ID
     * @return 收藏记录
     */
    UserCollection selectByUserAndItem(Long userId, String itemType, String itemId);
}