package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.dto.ProductFullDetailDTO;
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
    
    @Override
    public ProductFullDetailDTO getProductFullDetail(Long productId) {
        try {
            // 创建完整的商品详情DTO
            ProductFullDetailDTO productDetail = new ProductFullDetailDTO();
            
            // 获取商品基础信息
            ProductDetail detail = findByProductId(productId);
            if (detail == null) {
                return null;
            }
            
            // 设置基础信息
            productDetail.setId(detail.getProductId());
            productDetail.setTitle(detail.getTitle());
            productDetail.setDescription(detail.getDescription());
            productDetail.setImageUrl(detail.getMediaUrl());
            productDetail.setCategoryId(detail.getCategoryId());
            productDetail.setBrand(detail.getBrand());
            productDetail.setModel(detail.getModel());
            
            // 设置价格信息（使用min_price和max_price）
            productDetail.setPrice(detail.getMinPrice());
            productDetail.setCurrentPrice(detail.getMinPrice());
            productDetail.setOriginalPrice(detail.getMaxPrice());
            
            // 计算折扣
            if (detail.getMaxPrice() != null && detail.getMinPrice() != null && 
                detail.getMaxPrice().compareTo(detail.getMinPrice()) > 0) {
                BigDecimal discount = detail.getMaxPrice().subtract(detail.getMinPrice())
                    .divide(detail.getMaxPrice(), 2, java.math.RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100));
                productDetail.setDiscount(discount.intValue());
            } else {
                productDetail.setDiscount(0);
            }
            
            // 设置评分和销量（需要关联查询，这里使用默认值）
            productDetail.setRating(new BigDecimal("4.5"));
            productDetail.setReviewCount(100);
            productDetail.setTotalSales(500);
            productDetail.setMonthlySales(50);
            
            // 设置状态和标签
            productDetail.setIsNew(isNewProduct(detail.getCreatedAt()));
            productDetail.setIsHot(false);
            productDetail.setIsRecommended(true);
            productDetail.setTags(parseTags(detail.getTags()));
            productDetail.setStatus(detail.getStatus());
            
            // 设置商家信息（需要关联查询，这里使用默认值）
            productDetail.setSellerId(detail.getSellerId());
            productDetail.setSellerName("官方旗舰店");
            productDetail.setSellerRating(new BigDecimal("4.8"));
            productDetail.setSellerLogoUrl("https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=200&h=200");
            
            // 设置时间信息
            productDetail.setCreatedAt(detail.getCreatedAt());
            productDetail.setUpdatedAt(detail.getUpdatedAt());
            
            // 设置商品图片
            productDetail.setProductImages(getProductImagesDTOs(productId));
            
            // 设置商品规格
            productDetail.setSpecifications(getProductSpecificationsDTOs(productId));
            
            // 设置商品特性
            productDetail.setFeatures(getProductFeatures(productId));
            
            // 设置使用说明、注意事项、包装内容（需要扩展数据库字段）
            productDetail.setUsageInstructions(java.util.List.of("请按照说明书正确使用"));
            productDetail.setPrecautions(java.util.List.of("请避免高温环境"));
            productDetail.setPackageContents(java.util.List.of("主机", "充电器", "说明书"));
            
            // 设置相关商品
            productDetail.setRelatedProducts(getRelatedProductsDTOs(productId));
            
            return productDetail;
            
        } catch (Exception e) {
            log.error("获取完整商品详情失败: {}", productId, e);
            return null;
        }
    }
    
    // 辅助方法：判断是否新品
    private boolean isNewProduct(java.time.LocalDateTime createdAt) {
        if (createdAt == null) return false;
        java.time.LocalDateTime thirtyDaysAgo = java.time.LocalDateTime.now().minusDays(30);
        return createdAt.isAfter(thirtyDaysAgo);
    }
    
    // 辅助方法：解析标签
    private java.util.List<String> parseTags(String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        try {
            // 尝试解析JSON格式的tags
            return java.util.Arrays.asList(tags.split(","));
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }
    
    // 辅助方法：获取商品图片DTO列表
    private java.util.List<ProductFullDetailDTO.ProductImageDTO> getProductImagesDTOs(Long productId) {
        List<String> images = getProductImages(productId);
        return images.stream()
                .map(imageUrl -> {
                    ProductFullDetailDTO.ProductImageDTO imageDTO = new ProductFullDetailDTO.ProductImageDTO();
                    imageDTO.setImageUrl(imageUrl);
                    imageDTO.setImageType("main");
                    imageDTO.setSortOrder(0);
                    imageDTO.setThumbnailUrl(imageUrl);
                    return imageDTO;
                })
                .collect(java.util.stream.Collectors.toList());
    }
    
    // 辅助方法：获取商品规格DTO列表
    private java.util.List<ProductFullDetailDTO.ProductSpecDTO> getProductSpecificationsDTOs(Long productId) {
        List<ProductSkuSpec> specs = productSkuSpecMapper.findByProductId(productId);
        return specs.stream()
                .map(spec -> {
                    ProductFullDetailDTO.ProductSpecDTO specDTO = new ProductFullDetailDTO.ProductSpecDTO();
                    specDTO.setId(spec.getId());
                    specDTO.setSpecName(spec.getSpecName());
                    specDTO.setSpecValue(spec.getSpecName()); // 这里需要根据实际业务调整
                    specDTO.setSortOrder(spec.getSortOrder());
                    return specDTO;
                })
                .collect(java.util.stream.Collectors.toList());
    }
    
    // 辅助方法：获取相关商品DTO列表
    private java.util.List<ProductFullDetailDTO.RelatedProductDTO> getRelatedProductsDTOs(Long productId) {
        // 这里实现相关商品逻辑，暂时返回空列表
        return java.util.Collections.emptyList();
    }
}