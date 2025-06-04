package com.example.controller;

import com.example.common.api.CommonResult;
import com.example.model.Tag;
import com.example.service.TagService;
import com.example.util.UserIdExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import com.example.model.UserInterestRequest;
import com.example.model.UserIdRequest;
import com.example.service.UserInterestService;

/**
 * 商品标签接口
 */
@RestController
@RequestMapping("/tags")
@Slf4j
public class TagController {

    @Autowired
    private TagService tagService;

    @Autowired
    private UserIdExtractor userIdExtractor;

    @Autowired
    private UserInterestService userInterestService;
    

     /**
     * 检查用户是否需要冷启动
     */
    @GetMapping("/{userId}/need-cold-start")
    public CommonResult<Boolean> needColdStart(@PathVariable Long userId, HttpServletRequest request) {
        Long currentUserId = userIdExtractor.extractUserIdFromRequest(request);
        if (currentUserId == null || !currentUserId.equals(userId)) {
            return CommonResult.unauthorized(null);
        }
        
        try {
            boolean needColdStart = userInterestService.needColdStart(userId);
            return CommonResult.success(needColdStart);
        } catch (Exception e) {
            log.error("检查冷启动状态失败", e);
            return CommonResult.failed("检查冷启动状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 保存用户兴趣标签
     */
    @PostMapping("/interests")
    public CommonResult<Boolean> saveUserInterests(@RequestBody UserInterestRequest request, HttpServletRequest httpRequest) {
        Long currentUserId = userIdExtractor.extractUserIdFromRequest(httpRequest);
        if (currentUserId == null || !currentUserId.equals(request.getUserId())) {
            return CommonResult.unauthorized(null);
        }
        
        try {
            boolean result = userInterestService.saveUserInterests(request.getUserId(), request.getTagIds());
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("保存用户兴趣失败", e);
            return CommonResult.failed("保存用户兴趣失败: " + e.getMessage());
        }
    }
    
    /**
     * 跳过冷启动
     */
    @PostMapping("/skip-cold-start")
    public CommonResult<Boolean> skipColdStart(@RequestBody UserIdRequest request, HttpServletRequest httpRequest) {
        Long currentUserId = userIdExtractor.extractUserIdFromRequest(httpRequest);
        if (currentUserId == null || !currentUserId.equals(request.getUserId())) {
            return CommonResult.unauthorized(null);
        }
        
        try {
            boolean result = userInterestService.skipColdStart(request.getUserId());
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("跳过冷启动失败", e);
            return CommonResult.failed("跳过冷启动失败: " + e.getMessage());
        }
    }

    /**
     * 获取热门标签
     *
     * @param limit 返回数量
     * @return 通用结果，包含标签列表
     */
    @GetMapping("/popular")
    public CommonResult<List<Tag>> getPopularTags(@RequestParam(defaultValue = "20") int limit) {
        try {
            List<Tag> popularTags = tagService.getPopularTags(limit);
            return CommonResult.success(popularTags);
        } catch (Exception e) {
            log.error("获取热门标签失败", e);
            return CommonResult.failed("获取热门标签失败：" + e.getMessage());
        }
    }

    /**
     * 获取分类下的标签
     *
     * @param categoryId 分类ID
     * @return 通用结果，包含标签列表
     */
    @GetMapping("/category/{categoryId}")
    public CommonResult<List<Tag>> getTagsByCategory(@PathVariable("categoryId") Long categoryId) {
        try {
            List<Tag> tags = tagService.getTagsByCategory(categoryId);
            return CommonResult.success(tags);
        } catch (Exception e) {
            log.error("获取分类标签失败", e);
            return CommonResult.failed("获取分类标签失败：" + e.getMessage());
        }
    }

    /**
     * 创建标签
     *
     * @param tag 标签信息
     * @param request 请求对象
     * @return 通用结果，包含标签ID
     */
    @PostMapping
    public CommonResult<Long> addTag(@RequestBody Tag tag, HttpServletRequest request) {
        if (tag == null) {
            return CommonResult.failed("请求参数不能为空");
        }
        
        Long userId = userIdExtractor.extractUserIdFromRequest(request);
        if (userId == null) {
            return CommonResult.unauthorized(null);
        }
        
        try {
            Long tagId = tagService.addTag(tag);
            return CommonResult.success(tagId);
        } catch (Exception e) {
            log.error("添加标签失败", e);
            return CommonResult.failed("添加标签失败：" + e.getMessage());
        }
    }

    /**
     * 更新标签
     *
     * @param tag 标签信息
     * @param request 请求对象
     * @return 通用结果，包含是否成功
     */
    @PutMapping
    public CommonResult<Boolean> updateTag(@RequestBody Tag tag, HttpServletRequest request) {
        if (tag == null || tag.getId() == null || tag.getId() <= 0) {
            return CommonResult.failed("无效的标签信息");
        }
        
        Long userId = userIdExtractor.extractUserIdFromRequest(request);
        if (userId == null) {
            return CommonResult.unauthorized(null);
        }
        
        try {
            boolean result = tagService.updateTag(tag);
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("更新标签失败", e);
            return CommonResult.failed("更新标签失败：" + e.getMessage());
        }
    }

    /**
     * 删除标签
     *
     * @param id 标签ID
     * @param request 请求对象
     * @return 通用结果，包含是否成功
     */
    @DeleteMapping("/{id}")
    public CommonResult<Boolean> deleteTag(@PathVariable("id") Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            return CommonResult.failed("无效的标签ID");
        }
        
        Long userId = userIdExtractor.extractUserIdFromRequest(request);
        if (userId == null) {
            return CommonResult.unauthorized(null);
        }
        
        try {
            boolean result = tagService.deleteTag(id);
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("删除标签失败", e);
            return CommonResult.failed("删除标签失败：" + e.getMessage());
        }
    }
} 