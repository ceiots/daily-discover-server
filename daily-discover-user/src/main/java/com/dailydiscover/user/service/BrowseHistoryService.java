package com.dailydiscover.user.service;

import com.dailydiscover.user.dto.BrowseHistoryResponse;
import com.dailydiscover.user.entity.BrowseHistory;

import java.util.List;

/**
 * 用户浏览历史服务接口
 */
public interface BrowseHistoryService {

    /**
     * 添加浏览记录
     * 
     * @param browseHistory 浏览记录
     * @return 浏览记录信息
     */
    BrowseHistoryResponse addBrowseHistory(BrowseHistory browseHistory);

    /**
     * 获取用户的浏览历史
     * 
     * @param userId 用户ID
     * @return 浏览历史列表
     */
    List<BrowseHistoryResponse> getBrowseHistoryByUserId(Long userId);

    /**
     * 删除浏览记录
     * 
     * @param id 浏览记录ID
     * @return 是否成功
     */
    boolean deleteBrowseHistory(Long id);

    /**
     * 清空用户的浏览历史
     * 
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean clearBrowseHistory(Long userId);

    /**
     * 统计用户浏览历史数量
     * 
     * @param userId 用户ID
     * @return 浏览历史数量
     */
    int countBrowseHistory(Long userId);
}