package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ProductTagRelationMapper;
import com.dailydiscover.model.ProductTagRelation;
import com.dailydiscover.service.ProductTagRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductTagRelationServiceImpl extends ServiceImpl<ProductTagRelationMapper, ProductTagRelation> implements ProductTagRelationService {
    
    @Autowired
    private ProductTagRelationMapper productTagRelationMapper;
    
    @Override
    public List<ProductTagRelation> getRelationsByProductId(Long productId) {
        return lambdaQuery().eq(ProductTagRelation::getProductId, productId).list();
    }
    
    @Override
    public List<ProductTagRelation> getRelationsByTagId(Long tagId) {
        return lambdaQuery().eq(ProductTagRelation::getTagId, tagId).list();
    }
    
    @Override
    public boolean addTagToProduct(Long productId, Long tagId) {
        ProductTagRelation relation = new ProductTagRelation();
        relation.setProductId(productId);
        relation.setTagId(tagId);
        
        return save(relation);
    }
    
    @Override
    public boolean removeTagFromProduct(Long productId, Long tagId) {
        return lambdaUpdate()
                .eq(ProductTagRelation::getProductId, productId)
                .eq(ProductTagRelation::getTagId, tagId)
                .remove();
    }
    
    @Override
    public boolean batchAddTagsToProduct(Long productId, List<Long> tagIds) {
        List<ProductTagRelation> relations = tagIds.stream()
                .map(tagId -> {
                    ProductTagRelation relation = new ProductTagRelation();
                    relation.setProductId(productId);
                    relation.setTagId(tagId);
                    return relation;
                })
                .collect(java.util.stream.Collectors.toList());
        
        return saveBatch(relations);
    }
}