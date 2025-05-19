package com.example.service.impl;

import com.example.dto.ContentDto;
import com.example.mapper.ContentMapper;
import com.example.model.Content;
import com.example.service.ContentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 内容服务实现类
 */
@Slf4j
@Service
public class ContentServiceImpl implements ContentService {
    
    @Autowired
    private ContentMapper contentMapper;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 保存内容（创建新内容或更新已有内容）
     */
    @Override
    @Transactional
    public Content saveContent(ContentDto contentDto, Long userId) throws JsonProcessingException {
        Content content = convertDtoToEntity(contentDto);
        content.setUserId(userId);
        content.setStatus(1); // 1表示已发布
        
        Date now = new Date();
        
        if (content.getId() == null) {
            // 新建内容
            content.setCreatedAt(now);
            content.setUpdatedAt(now);
            content.setViewCount(0);
            content.setLikeCount(0);
            content.setCommentCount(0);
            contentMapper.insert(content);
        } else {
            // 更新已有内容
            content.setUpdatedAt(now);
            contentMapper.update(content);
        }
        
        return content;
    }
    
    /**
     * 保存内容为草稿
     */
    @Override
    @Transactional
    public Content saveDraft(ContentDto contentDto, Long userId) throws JsonProcessingException {
        Content content = convertDtoToEntity(contentDto);
        content.setUserId(userId);
        content.setStatus(0); // 0表示草稿
        
        Date now = new Date();
        
        if (content.getId() == null) {
            // 新建草稿
            content.setCreatedAt(now);
            content.setUpdatedAt(now);
            content.setViewCount(0);
            content.setLikeCount(0);
            content.setCommentCount(0);
            contentMapper.insert(content);
        } else {
            // 更新已有草稿
            content.setUpdatedAt(now);
            contentMapper.update(content);
        }
        
        return content;
    }
    
    /**
     * 根据ID获取内容
     */
    @Override
    public Content getContentById(Long id) {
        return contentMapper.findById(id);
    }
    
    /**
     * 根据用户ID获取内容列表
     */
    @Override
    public List<Content> getContentsByUserId(Long userId) {
        return contentMapper.findByUserId(userId);
    }
    
    /**
     * 根据用户ID和状态获取内容列表
     */
    @Override
    public List<Content> getContentsByUserIdAndStatus(Long userId, Integer status) {
        return contentMapper.findByUserIdAndStatus(userId, status);
    }
    
    /**
     * 根据用户ID、状态和审核状态获取内容列表
     */
    @Override
    public List<Content> getContentsByUserIdAndStatusAndAuditStatus(Long userId, Integer status, Integer auditStatus) {
        return contentMapper.findByUserIdAndStatusAndAuditStatus(userId, status, auditStatus);
    }
    
    /**
     * 根据用户ID和审核状态获取内容列表
     */
    @Override
    public List<Content> getContentsByUserIdAndAuditStatus(Long userId, Integer auditStatus) {
        return contentMapper.findByUserIdAndAuditStatus(userId, auditStatus);
    }

    /**
     * 获取所有已发布的内容
     */
    @Override
    public List<Content> getAllPublishedContents() {
        return contentMapper.findAllPublished();
    }

    /**
     * 获取所有已发布且审核通过的内容
     */
    @Override
    public List<Content> getPublishedAndApprovedContents() {
        return contentMapper.findPublishedAndApproved();
    }

    /**
     * 获取所有待审核的内容
     */
    @Override
    public List<Content> getPendingAuditContents() {
        return contentMapper.findPendingAudit();
    }

    /**
     * 删除内容
     */
    @Override
    @Transactional
    public boolean deleteContent(Long id, Long userId) {
        Content content = contentMapper.findById(id);
        if (content != null && content.getUserId().equals(userId)) {
            contentMapper.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * 增加浏览次数
     */
    @Override
    public void incrementViewCount(Long id) {
        contentMapper.incrementViewCount(id);
    }
    
    /**
     * 增加点赞次数
     */
    @Override
    public void incrementLikeCount(Long id) {
        contentMapper.incrementLikeCount(id);
    }
    
    /**
     * 审核内容
     */
    @Override
    @Transactional
    public Content auditContent(Long id, Integer auditStatus, String auditRemark) {
        Content content = contentMapper.findById(id);
        if (content != null) {
            content.setAuditStatus(auditStatus);
            content.setAuditRemark(auditRemark);
            content.setUpdatedAt(new Date());
            contentMapper.update(content);
        }
        return content;
    }
    
    /**
     * 将DTO转换为实体
     */
    private Content convertDtoToEntity(ContentDto dto) {
        Content content = new Content();
        content.setId(dto.getId());
        content.setTitle(dto.getTitle());
        content.setContent(dto.getContent());
        
        try {
            // 将List<String>转换为JSON字符串
            if (dto.getImages() != null) {
                content.setImages(objectMapper.writeValueAsString(dto.getImages()));
            }
            
            if (dto.getTags() != null) {
                content.setTags(objectMapper.writeValueAsString(dto.getTags()));
            }
        } catch (JsonProcessingException e) {
            log.error("JSON转换异常", e);
            // 出现异常时设置为空数组
            content.setImages("[]");
            content.setTags("[]");
        }
        
        content.setStatus(dto.getStatus());
        
        return content;
    }
} 