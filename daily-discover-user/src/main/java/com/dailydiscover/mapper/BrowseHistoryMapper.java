package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.entity.BrowseHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户浏览历史Mapper接口
 */
@Mapper
public interface BrowseHistoryMapper extends BaseMapper<BrowseHistory> {

    /**
     * 删除用户的所有浏览历史
     * 
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(Long userId);

    /**
     * 统计用户浏览历史数量
     * 
     * @param userId 用户ID
     * @return 浏览历史数量
     */
    int countByUserId(Long userId);
}