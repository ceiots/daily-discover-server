package com.example.service;

import com.example.dto.ContentDto;
import com.example.mapper.ContentMapper;
import com.example.model.Content;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 内容服务接口
 */
public interface ContentService {
    
    /**
     * 保存内容（发布）
     * @param contentDto 内容DTO
     * @param userId 用户ID
     * @return 保存后的内容对象
     */
    Content saveContent(ContentDto contentDto, Long userId) throws JsonProcessingException;
    
    /**
     * 根据ID获取内容
     * @param id 内容ID
     * @return 内容对象
     */
    Content getContentById(Long id);
    
    /**
     * 根据用户ID获取所有内容
     * @param userId 用户ID
     * @return 内容列表
     */
    List<Content> getContentsByUserId(Long userId);
    
    /**
     * 根据用户ID和状态获取内容
     * @param userId 用户ID
     * @param status 状态
     * @return 内容列表
     */
    List<Content> getContentsByUserIdAndStatus(Long userId, Integer status);
    
    /**
     * 根据用户ID和审核状态获取内容
     * @param userId 用户ID
     * @param auditStatus 审核状态
     * @return 内容列表
     */
    List<Content> getContentsByUserIdAndAuditStatus(Long userId, Integer auditStatus);
    
    /**
     * 根据用户ID、状态和审核状态获取内容
     * @param userId 用户ID
     * @param status 状态
     * @param auditStatus 审核状态
     * @return 内容列表
     */
    List<Content> getContentsByUserIdAndStatusAndAuditStatus(Long userId, Integer status, Integer auditStatus);
    
    /**
     * 获取所有已发布的内容
     * @return 内容列表
     */
    List<Content> getAllPublishedContents();
    
    /**
     * 获取所有已发布且审核通过的内容
     * @return 内容列表
     */
    List<Content> getPublishedAndApprovedContents();
    
    /**
     * 获取所有待审核的内容
     * @return 内容列表
     */
    List<Content> getPendingAuditContents();
    
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
    Content saveDraft(ContentDto contentDto, Long userId) throws JsonProcessingException;
    
    /**
     * 审核内容
     * @param id 内容ID
     * @param auditStatus 审核状态
     * @param auditRemark 审核备注
     * @return 审核后的内容对象
     */
    Content auditContent(Long id, Integer auditStatus, String auditRemark);
} 