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
        return productTagMapper.findPopularTags(100);
    }
    
    @Override
    public List<ProductTag> getTagsByType(String tagType) {
        return lambdaQuery().eq(ProductTag::getTagType, tagType).list();
    }
    
    @Override
    public ProductTag createTag(String tagName, String tagType) {
        ProductTag tag = new ProductTag();
        tag.setTagName(tagName);
        tag.setTagType(tagType);
        
        save(tag);
        return tag;
    }
    
    @Override
    public boolean updateTagStatus(Long tagId, String status) {
        return true;
    }
    
    @Override
    public boolean updateTagOrder(Long tagId, Integer tagOrder) {
        return true;
    }
    
    @Override
    public ProductTag getByTagName(String tagName) {
        return productTagMapper.findByTagName(tagName);
    }
    
    @Override
    public List<ProductTag> getPopularTags(int limit) {
        return productTagMapper.findPopularTags(limit);
    }
    
    @Override
    public boolean incrementUsageCount(Long tagId) {
        return true;
    }
}