package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.TagMapper;
import com.example.model.Tag;
import com.example.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 商品标签服务实现类
 */
@Slf4j
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Override
    @Transactional
    public Long addTag(Tag tag) {
        
        
        // 如果未指定状态，默认为启用
        if (tag.getStatus() == null) {
            tag.setStatus(1);
        }
        
        // 如果未指定热度，默认为0
        if (tag.getHot() == null) {
            tag.setHot(0);
        }
        
        // 设置创建时间和更新时间
        Date now = new Date();
        tag.setCreatedAt(now);
        tag.setUpdatedAt(now);
        
        // 保存标签
        boolean success = this.save(tag);
        if (success) {
            return tag.getId();
        } else {
            throw new RuntimeException("添加标签失败");
        }
    }

    @Override
    @Transactional
    public boolean updateTag(Tag tag) {
        Tag existingTag = this.getById(tag.getId());
        if (existingTag == null) {
            throw new RuntimeException("标签不存在");
        }
        
        // 更新时间
        tag.setUpdatedAt(new Date());
        
        // 保留创建时间和创建用户ID
        tag.setCreatedAt(existingTag.getCreatedAt());
        
        // 更新标签
        return this.updateById(tag);
    }

    @Override
    @Transactional
    public boolean deleteTag(Long id) {
        return this.removeById(id);
    }

    @Override
    public List<Tag> getPopularTags(int limit) {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getStatus, 1)
                .orderByDesc(Tag::getHot)
                .last("LIMIT " + limit);
        
        return this.list(queryWrapper);
    }

    @Override
    public List<Tag> getTagsByCategory(Long categoryId) {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        
        // 如果categoryId为空，则查询通用标签（categoryId为null的标签）
        if (categoryId == null || categoryId == 0) {
            queryWrapper.isNull(Tag::getCategoryId);
        } else {
            queryWrapper.eq(Tag::getCategoryId, categoryId);
        }
        
        queryWrapper.eq(Tag::getStatus, 1)
                .orderByDesc(Tag::getHot);
        
        return this.list(queryWrapper);
    }
} 