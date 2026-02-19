package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ProductTagMapper;
import com.dailydiscover.model.ProductTag;
import com.dailydiscover.service.ProductTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductTagServiceImpl extends ServiceImpl<ProductTagMapper, ProductTag> implements ProductTagService {
    
    @Autowired
    private ProductTagMapper productTagMapper;
    
    @Override
    public List<ProductTag> getActiveTags() {
        return productTagMapper.findActiveTags();
    }
    
    @Override
    public List<ProductTag> getTagsByType(String tagType) {
        return productTagMapper.findByType(tagType);
    }
    
    @Override
    public ProductTag createTag(String tagName, String tagType) {
        ProductTag tag = new ProductTag();
        tag.setTagName(tagName);
        tag.setTagType(tagType);
        tag.setStatus("active");
        
        save(tag);
        return tag;
    }
    
    @Override
    public boolean updateTagStatus(Long tagId, String status) {
        ProductTag tag = getById(tagId);
        if (tag != null) {
            tag.setStatus(status);
            return updateById(tag);
        }
        return false;
    }
    
    @Override
    public boolean updateTagOrder(Long tagId, Integer tagOrder) {
        ProductTag tag = getById(tagId);
        if (tag != null) {
            tag.setTagOrder(tagOrder);
            return updateById(tag);
        }
        return false;
    }
    
    @Override
    public ProductTag getByTagName(String tagName) {
        return lambdaQuery().eq(ProductTag::getTagName, tagName).eq(ProductTag::getStatus, "active").one();
    }
    
    @Override
    public List<ProductTag> getPopularTags(int limit) {
        return lambdaQuery()
                .eq(ProductTag::getStatus, "active")
                .orderByDesc(ProductTag::getUsageCount)
                .last("LIMIT " + limit)
                .list();
    }
    
    @Override
    public boolean incrementUsageCount(Long tagId) {
        ProductTag tag = getById(tagId);
        if (tag != null) {
            tag.setUsageCount(tag.getUsageCount() + 1);
            return updateById(tag);
        }
        return false;
    }
}