package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.model.Tag;

import java.util.List;

/**
 * 商品标签服务
 */
public interface TagService extends IService<Tag> {

    /**
     * 添加标签
     *
     * @param tag 标签信息
     * @param userId 创建人ID
     * @return 标签ID
     */
    Long addTag(Tag tag, Long userId);

    /**
     * 更新标签
     *
     * @param tag 标签信息
     * @return 是否成功
     */
    boolean updateTag(Tag tag);

    /**
     * 删除标签
     *
     * @param id 标签ID
     * @return 是否成功
     */
    boolean deleteTag(Long id);

    /**
     * 获取热门标签
     *
     * @param limit 返回数量
     * @return 标签列表
     */
    List<Tag> getPopularTags(int limit);

    /**
     * 获取分类下的标签
     *
     * @param categoryId 分类ID
     * @return 标签列表
     */
    List<Tag> getTagsByCategory(Long categoryId);
} 