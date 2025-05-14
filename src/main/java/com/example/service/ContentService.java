package com.example.service;

import com.example.dto.ContentDto;
import com.example.model.Content;
import java.util.List;

/**
 * 内容服务接口
 */
public interface ContentService {
    
    /**
     * 保存内容（创建新内容或更新已有内容）
     * @param contentDto 内容DTO
     * @param userId 用户ID
     * @return 保存后的内容对象
     */
    Content saveContent(ContentDto contentDto, Long userId);
    
    /**
     * 根据ID获取内容
     * @param id 内容ID
     * @return 内容对象
     */
    Content getContentById(Long id);
    
    /**
     * 根据用户ID获取内容列表
     * @param userId 用户ID
     * @return 内容列表
     */
    List<Content> getContentsByUserId(Long userId);
    
    /**
     * 根据用户ID和状态获取内容列表
     * @param userId 用户ID
     * @param status 状态
     * @return 内容列表
     */
    List<Content> getContentsByUserIdAndStatus(Long userId, Integer status);
    
    /**
     * 获取所有已发布的内容
     * @return 内容列表
     */
    List<Content> getAllPublishedContents();
    
    /**
     * 删除内容
     * @param id 内容ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否删除成功
     */
    boolean deleteContent(Long id, Long userId);
    
    /**
     * 增加浏览次数
     * @param id 内容ID
     */
    void incrementViewCount(Long id);
    
    /**
     * 增加点赞次数
     * @param id 内容ID
     */
    void incrementLikeCount(Long id);
    
    /**
     * 保存内容为草稿
     * @param contentDto 内容DTO
     * @param userId 用户ID
     * @return 保存后的内容对象
     */
    Content saveDraft(ContentDto contentDto, Long userId);
} 