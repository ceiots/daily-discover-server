package com.example.controller;

import com.example.common.api.CommonResult;
import com.example.dto.ContentDto;
import com.example.model.Content;
import com.example.service.ContentService;
import com.example.util.JwtTokenUtil;
import com.example.util.SshUtil;
import com.example.util.UserIdExtractor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
    private UserIdExtractor userIdExtractor;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    
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
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // 设置内容为待审核状态
            contentDto.setAuditStatus(0); // 0-待审核
            
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
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // 草稿不需要审核
            contentDto.setAuditStatus(null);
            
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
            result.put("auditStatus", content.getAuditStatus());
            result.put("auditRemark", content.getAuditRemark());
            
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
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer auditStatus) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            List<Content> contents;
            
            // 根据参数决定查询方式
            if (status != null && auditStatus != null) {
                contents = contentService.getContentsByUserIdAndStatusAndAuditStatus(userId, status, auditStatus);
            } else if (status != null) {
                contents = contentService.getContentsByUserIdAndStatus(userId, status);
            } else if (auditStatus != null) {
                contents = contentService.getContentsByUserIdAndAuditStatus(userId, auditStatus);
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
                item.put("auditStatus", content.getAuditStatus());
                item.put("auditRemark", content.getAuditRemark());
                
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
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
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
     * @param file 图片文件，可以是单个文件或多个文件
     * @param token JWT令牌
     * @return 通用结果，包含图片URL列表
     */
    @PostMapping("/upload")
    public CommonResult<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile[] files,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            System.out.println("userId: " + userId);
            
            // 如果仍然没有userId，返回未授权错误
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            if (files.length == 0) {
                return CommonResult.failed("上传的文件为空");
            }
            
            // 存储所有上传成功的图片URL
            List<String> successUrls = new ArrayList<>();
            List<Map<String, String>> errorFiles = new ArrayList<>();
            
            // 处理每个文件
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }
                
                try {
                    // 生成唯一的文件名
                    String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
                    
                    // 尝试本地保存文件
                    String imageUrl = saveFileLocally(file, fileName);
                    if (imageUrl != null) {
                        // 本地保存成功
                        successUrls.add(imageUrl);
                    } else {
                        // 保存失败，记录错误
                        Map<String, String> errorFile = new HashMap<>();
                        errorFile.put("name", file.getOriginalFilename());
                        errorFile.put("error", "文件保存失败");
                        errorFiles.add(errorFile);
                    }
                } catch (Exception e) {
                    // 处理单个文件上传失败
                    log.error("上传单个文件失败: {}", file.getOriginalFilename(), e);
                    Map<String, String> errorFile = new HashMap<>();
                    errorFile.put("name", file.getOriginalFilename());
                    errorFile.put("error", e.getMessage());
                    errorFiles.add(errorFile);
                }
            }
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            
            if (successUrls.isEmpty() && !errorFiles.isEmpty()) {
                // 所有文件都上传失败
                return CommonResult.failed("所有文件上传失败");
            } else if (!successUrls.isEmpty() && !errorFiles.isEmpty()) {
                // 部分文件上传成功
                result.put("urls", successUrls);
                result.put("errorFiles", errorFiles);
                result.put("partialSuccess", true);
                // 返回第一个成功上传的URL作为主URL
                result.put("url", successUrls.get(0));
                return CommonResult.success(result);
            } else {
                // 所有文件都上传成功
                result.put("urls", successUrls);
                // 返回第一个上传的URL作为主URL
                result.put("url", successUrls.get(0));
                return CommonResult.success(result);
            }
        } catch (Exception e) {
            log.error("上传图片时发生异常", e);
            return CommonResult.failed("上传图片失败：" + e.getMessage());
        }
    }
    
    /**
     * 本地保存文件
     * @param file 要保存的文件
     * @param fileName 文件名
     * @return 访问URL，保存失败则返回null
     */
    private String saveFileLocally(MultipartFile file, String fileName) {
        try {
            // 获取当前日期，用于创建文件夹层级
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; // 月份从0开始，需要+1
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            
            // 构建目录结构：基础目录/年/月/日/
            String baseDir = "E:/media/content/";
            String dateDir = year + "/" + (month < 10 ? "0" + month : month) + "/" + (day < 10 ? "0" + day : day) + "/";
            String uploadDir = baseDir + dateDir;
            
            // 检查上传目录是否存在，不存在则创建
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                // 创建目录结构
                if (!dir.mkdirs()) {
                    log.error("无法创建按日期分层的上传目录: {}", uploadDir);
                    
                    // 如果创建日期目录失败，回退到基础目录
                    log.info("尝试使用基础目录保存文件");
                    dir = new File(baseDir);
                    if (!dir.exists() && !dir.mkdirs()) {
                        log.error("无法创建基础上传目录: {}", baseDir);
                        return null;
                    }
                    dateDir = ""; // 重置日期目录路径
                }
            }
            
            // 保存文件
            File destFile = new File(dir, fileName);
            file.transferTo(destFile);
            
            log.info("文件已成功保存到本地: {}", destFile.getAbsolutePath());
            
            // 返回URL
            return "https://dailydiscover.top/media/content/" + dateDir + fileName;
        } catch (IOException e) {
            log.error("本地保存文件失败", e);
            return null;
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
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
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
            // 获取已发布且审核通过的内容
            List<Content> contents = contentService.getPublishedAndApprovedContents();
            
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
    
    /**
     * 内容审核接口 (管理员使用)
     */
    @PostMapping("/{id}/audit")
    public CommonResult<Content> auditContent(
            @PathVariable Long id,
            @RequestParam Integer auditStatus,
            @RequestParam(required = false) String auditRemark,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // TODO: 应当进行管理员权限检查
            
            Content content = contentService.auditContent(id, auditStatus, auditRemark);
            if (content == null) {
                return CommonResult.failed("内容不存在");
            }
            
            return CommonResult.success(content);
        } catch (Exception e) {
            log.error("审核内容时发生异常", e);
            return CommonResult.failed("审核内容失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取待审核内容列表 (管理员使用)
     */
    @GetMapping("/pending-audit")
    public CommonResult<List<Map<String, Object>>> getPendingAuditContents(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // TODO: 应当进行管理员权限检查
            
            List<Content> contents = contentService.getPendingAuditContents();
            
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
                item.put("status", content.getStatus());
                item.put("createdAt", content.getCreatedAt());
                item.put("auditStatus", content.getAuditStatus());
                
                result.add(item);
            }
            
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("获取待审核内容列表时发生异常", e);
            return CommonResult.failed("获取待审核内容列表失败：" + e.getMessage());
        }
    }
} 