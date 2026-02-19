package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ProductDetailMapper;
import com.dailydiscover.mapper.ProductSkuSpecMapper;
import com.dailydiscover.mapper.ProductTagMapper;
import com.dailydiscover.mapper.ProductTagRelationMapper;
import com.dailydiscover.model.ProductDetail;
import com.dailydiscover.model.ProductSkuSpec;
import com.dailydiscover.model.ProductTag;
import com.dailydiscover.model.ProductTagRelation;
import com.dailydiscover.service.ProductDetailService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductDetailServiceImpl extends ServiceImpl<ProductDetailMapper, ProductDetail> implements ProductDetailService {
    
    @Autowired
    private ProductDetailMapper productDetailMapper;
    
    @Autowired
    private ProductSkuSpecMapper productSkuSpecMapper;
    
    @Autowired
    private ProductTagMapper productTagMapper;
    
    @Autowired
    private ProductTagRelationMapper productTagRelationMapper;
    
    @Override
    public ProductDetail findByProductId(Long productId) {
        return productDetailMapper.findByProductId(productId);
    }
    
    @Override
    public ProductDetail findById(Long id) {
        return getById(id);
    }
    
    @Override
    public List<ProductDetail> findAll() {
        return list();
    }
    
    @Override
    public boolean save(ProductDetail productDetail) {
        return super.save(productDetail);
    }
    
    @Override
    public ProductDetail update(ProductDetail productDetail) {
        updateById(productDetail);
        return productDetail;
    }
    
    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }
    
    @Override
    public java.util.List<String> getProductImages(Long productId) {
        ProductDetail detail = findByProductId(productId);
        if (detail != null && detail.getMediaUrl() != null) {
            return java.util.List.of(detail.getMediaUrl().split(","));
        }
        return java.util.Collections.emptyList();
    }
    
    @Override
    public java.util.List<String> getProductSpecifications(Long productId) {
        // 查询商品规格信息
        List<ProductSkuSpec> specs = productSkuSpecMapper.findByProductId(productId);
        
        // 提取规格名称列表
        return specs.stream()
                .map(ProductSkuSpec::getSpecName)
                .collect(Collectors.toList());
    }
    
    @Override
    public java.util.List<String> getProductFeatures(Long productId) {
        // 查询商品标签关联关系
        List<ProductTagRelation> tagRelations = productTagRelationMapper.findByProductId(productId);
        
        // 提取标签ID列表
        List<Long> tagIds = tagRelations.stream()
                .map(ProductTagRelation::getTagId)
                .collect(Collectors.toList());
        
        if (tagIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        
        // 查询标签详细信息
        List<ProductTag> tags = productTagMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ProductTag>()
                        .in("id", tagIds)
                        .eq("tag_type", "feature")
        );
        
        // 提取特性标签名称列表
        return tags.stream()
                .map(ProductTag::getTagName)
                .collect(Collectors.toList());
    }
}