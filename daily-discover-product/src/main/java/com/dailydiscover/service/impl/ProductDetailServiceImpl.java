package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ProductDetailMapper;
import com.dailydiscover.model.ProductDetail;
import com.dailydiscover.service.ProductDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品详情服务实现类（电商媒体管理）
 */
@Service
@Slf4j
public class ProductDetailServiceImpl extends ServiceImpl<ProductDetailMapper, ProductDetail> implements ProductDetailService {
    
    @Autowired
    private ProductDetailMapper productDetailMapper;
    
    @Override
    public List<ProductDetail> findByProductId(Long productId) {
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
    public List<ProductDetail> findByProductIdAndMediaType(Long productId, Integer mediaType) {
        return productDetailMapper.findByProductIdAndMediaType(productId, mediaType);
    }
    
    @Override
    public List<ProductDetail> getProductCarousel(Long productId) {
        return productDetailMapper.getProductCarousel(productId);
    }
    
    @Override
    public List<ProductDetail> getProductDetailImages(Long productId) {
        return productDetailMapper.getProductDetailImages(productId);
    }
    
    @Override
    public List<ProductDetail> getProductVideos(Long productId) {
        return productDetailMapper.getProductVideos(productId);
    }
    
    @Override
    public List<ProductDetail> getProductImages(Long productId) {
        return productDetailMapper.getProductImages(productId);
    }
    
    @Override
    public boolean deleteByProductId(Long productId) {
        return productDetailMapper.deleteByProductId(productId) > 0;
    }
    
    @Override
    public boolean deleteByProductIdAndMediaType(Long productId, Integer mediaType) {
        return productDetailMapper.deleteByProductIdAndMediaType(productId, mediaType) > 0;
    }
}
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