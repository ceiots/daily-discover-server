package com.example.controller;

import com.example.common.api.CommonResult;
import com.example.dto.ContentDto;
import com.example.model.Content;
import com.example.service.ContentService;
import com.example.util.JwtTokenUtil;
import com.example.util.SshUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 内容控制器，处理内容相关的HTTP请求
 */
@Slf4j
@RestController
@RequestMapping("/content")
public class ContentController {

    @Autowired
    private ContentService contentService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private SshUtil sshUtil;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 从Authorization头或userId头中提取用户ID
     * @param token JWT令牌 (可选)
     * @param userIdHeader 用户ID请求头 (可选)
     * @return 用户ID，如果无法提取则返回null
     */
    private Long extractUserId(String token, String userIdHeader) {
        // 尝试从userId请求头中提取
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                return Long.parseLong(userIdHeader);
            } catch (NumberFormatException e) {
                log.warn("无效的userId请求头: {}", userIdHeader);
            }
        }
        
        // 尝试从JWT令牌中提取
        if (token != null && !token.isEmpty()) {
            // 处理Bearer token格式
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Long userId = jwtTokenUtil.extractUserId(token);
            log.debug("从token中提取的userId: {}", userId);
            return userId;
        }
        
        return null;
    }
    
    /**
     * 创建/更新内容
     * @param contentDto 内容DTO
     * @param token JWT令牌
     * @return 通用结果，包含创建好的内容
     */
    @PostMapping("/save")
    public CommonResult<Content> saveContent(
            @RequestBody ContentDto contentDto, 
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            Content content = contentService.saveContent(contentDto, userId);
            return CommonResult.success(content);
        } catch (Exception e) {
            log.error("保存内容时发生异常", e);
            return CommonResult.failed("保存内容失败：" + e.getMessage());
        }
    }
    
    /**
     * 保存草稿
     * @param contentDto 内容DTO
     * @param token JWT令牌
     * @return 通用结果，包含保存好的草稿
     */
    @PostMapping("/draft")
    public CommonResult<Content> saveDraft(
            @RequestBody ContentDto contentDto, 
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            Content content = contentService.saveDraft(contentDto, userId);
            return CommonResult.success(content);
        } catch (Exception e) {
            log.error("保存草稿时发生异常", e);
            return CommonResult.failed("保存草稿失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取内容详情
     * @param id 内容ID
     * @return 通用结果，包含内容详情
     */
    @GetMapping("/{id}")
    public CommonResult<Map<String, Object>> getContent(@PathVariable Long id) {
        try {
            Content content = contentService.getContentById(id);
            if (content == null) {
                return CommonResult.failed("内容不存在");
            }
            
            // 增加浏览次数
            contentService.incrementViewCount(id);
            
            // 将JSON字符串转换为List
            List<String> images = new ArrayList<>();
            List<String> tags = new ArrayList<>();
            
            if (content.getImages() != null && !content.getImages().isEmpty()) {
                images = objectMapper.readValue(content.getImages(), new TypeReference<List<String>>() {});
            }
            
            if (content.getTags() != null && !content.getTags().isEmpty()) {
                tags = objectMapper.readValue(content.getTags(), new TypeReference<List<String>>() {});
            }
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("id", content.getId());
            result.put("userId", content.getUserId());
            result.put("title", content.getTitle());
            result.put("content", content.getContent());
            result.put("images", images);
            result.put("tags", tags);
            result.put("status", content.getStatus());
            result.put("createdAt", content.getCreatedAt());
            result.put("updatedAt", content.getUpdatedAt());
            result.put("viewCount", content.getViewCount());
            result.put("likeCount", content.getLikeCount());
            result.put("commentCount", content.getCommentCount());
            
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("获取内容详情时发生异常", e);
            return CommonResult.failed("获取内容详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户内容列表
     * @param token JWT令牌
     * @param status 状态(可选)
     * @return 通用结果，包含内容列表
     */
    @GetMapping("/list")
    public CommonResult<List<Map<String, Object>>> getUserContents(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestParam(required = false) Integer status) {
        try {
            Long userId = extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            List<Content> contents;
            if (status != null) {
                contents = contentService.getContentsByUserIdAndStatus(userId, status);
            } else {
                contents = contentService.getContentsByUserId(userId);
            }
            
            List<Map<String, Object>> result = new ArrayList<>();
            for (Content content : contents) {
                // 将JSON字符串转换为List
                List<String> images = new ArrayList<>();
                List<String> tags = new ArrayList<>();
                
                if (content.getImages() != null && !content.getImages().isEmpty()) {
                    try {
                        images = objectMapper.readValue(content.getImages(), new TypeReference<List<String>>() {});
                    } catch (JsonProcessingException e) {
                        log.error("解析images JSON失败", e);
                    }
                }
                
                if (content.getTags() != null && !content.getTags().isEmpty()) {
                    try {
                        tags = objectMapper.readValue(content.getTags(), new TypeReference<List<String>>() {});
                    } catch (JsonProcessingException e) {
                        log.error("解析tags JSON失败", e);
                    }
                }
                
                // 构建返回结果
                Map<String, Object> item = new HashMap<>();
                item.put("id", content.getId());
                item.put("title", content.getTitle());
                item.put("content", content.getContent());
                item.put("images", images);
                item.put("tags", tags);
                item.put("status", content.getStatus());
                item.put("createdAt", content.getCreatedAt());
                item.put("viewCount", content.getViewCount());
                item.put("likeCount", content.getLikeCount());
                item.put("commentCount", content.getCommentCount());
                
                result.add(item);
            }
            
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("获取用户内容列表时发生异常", e);
            return CommonResult.failed("获取用户内容列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除内容
     * @param id 内容ID
     * @param token JWT令牌
     * @return 通用结果
     */
    @DeleteMapping("/{id}")
    public CommonResult<Void> deleteContent(
            @PathVariable Long id, 
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            boolean success = contentService.deleteContent(id, userId);
            if (success) {
                return CommonResult.success(null);
            } else {
                return CommonResult.failed("删除内容失败，可能内容不存在或无权限删除");
            }
        } catch (Exception e) {
            log.error("删除内容时发生异常", e);
            return CommonResult.failed("删除内容失败：" + e.getMessage());
        }
    }
    
    /**
     * 上传图片
     * @param file 图片文件
     * @param token JWT令牌
     * @return 通用结果，包含图片URL
     */
    @PostMapping("/upload")
    public CommonResult<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = extractUserId(token, userIdHeader);
            System.out.println("userId: " + userId);
            
            // 如果仍然没有userId，返回未授权错误
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            if (file.isEmpty()) {
                return CommonResult.failed("上传的文件为空");
            }
            
            // 生成唯一的文件名
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            
            try {
                // 确保远程目录存在
                sshUtil.ensureRemoteDirectoryExists();
                
                // 上传文件到远程服务器
                String imageUrl = sshUtil.uploadFile(file, fileName);
                
                Map<String, String> result = new HashMap<>();
                result.put("url", imageUrl);
                
                return CommonResult.success(result);
            } catch (JSchException | SftpException e) {
                log.error("上传图片到远程服务器失败", e);
                return CommonResult.failed("上传图片到远程服务器失败：" + e.getMessage());
            }
        } catch (IOException e) {
            log.error("上传图片时发生异常", e);
            return CommonResult.failed("上传图片失败：" + e.getMessage());
        }
    }
    
    /**
     * 点赞内容
     * @param id 内容ID
     * @param token JWT令牌
     * @return 通用结果
     */
    @PostMapping("/{id}/like")
    public CommonResult<Void> likeContent(
            @PathVariable Long id, 
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            Content content = contentService.getContentById(id);
            if (content == null) {
                return CommonResult.failed("内容不存在");
            }
            
            contentService.incrementLikeCount(id);
            return CommonResult.success(null);
        } catch (Exception e) {
            log.error("点赞内容时发生异常", e);
            return CommonResult.failed("点赞内容失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取所有已发布的内容
     * @return 通用结果，包含内容列表
     */
    @GetMapping("/public")
    public CommonResult<List<Map<String, Object>>> getPublicContents() {
        try {
            List<Content> contents = contentService.getAllPublishedContents();
            
            List<Map<String, Object>> result = new ArrayList<>();
            for (Content content : contents) {
                // 将JSON字符串转换为List
                List<String> images = new ArrayList<>();
                List<String> tags = new ArrayList<>();
                
                if (content.getImages() != null && !content.getImages().isEmpty()) {
                    try {
                        images = objectMapper.readValue(content.getImages(), new TypeReference<List<String>>() {});
                    } catch (JsonProcessingException e) {
                        log.error("解析images JSON失败", e);
                    }
                }
                
                if (content.getTags() != null && !content.getTags().isEmpty()) {
                    try {
                        tags = objectMapper.readValue(content.getTags(), new TypeReference<List<String>>() {});
                    } catch (JsonProcessingException e) {
                        log.error("解析tags JSON失败", e);
                    }
                }
                
                // 构建返回结果
                Map<String, Object> item = new HashMap<>();
                item.put("id", content.getId());
                item.put("userId", content.getUserId());
                item.put("title", content.getTitle());
                item.put("content", content.getContent());
                item.put("images", images);
                item.put("tags", tags);
                item.put("createdAt", content.getCreatedAt());
                item.put("viewCount", content.getViewCount());
                item.put("likeCount", content.getLikeCount());
                item.put("commentCount", content.getCommentCount());
                
                result.add(item);
            }
            
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("获取公开内容列表时发生异常", e);
            return CommonResult.failed("获取公开内容列表失败：" + e.getMessage());
        }
    }
} 