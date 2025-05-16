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
            Long tagId = tagService.addTag(tag, userId);
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