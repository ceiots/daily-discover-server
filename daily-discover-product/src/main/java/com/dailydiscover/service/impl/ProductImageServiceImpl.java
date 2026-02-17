package com.dailydiscover.service.impl;

import com.dailydiscover.dto.ProductImageDTO;
import com.dailydiscover.mapper.ProductImageMapper;
import com.dailydiscover.model.ProductImage;
import com.dailydiscover.model.ImageCategory;
import com.dailydiscover.service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品图片服务实现类
 */
@Service
public class ProductImageServiceImpl implements ProductImageService {
    
    @Autowired
    private ProductImageMapper productImageMapper;
    
    @Override
    public Map<String, List<ProductImageDTO>> getCategorizedImagesByProductId(Long productId) {
        // 获取商品的所有图片
        List<ProductImage> images = productImageMapper.findByProductId(productId);
        
        // 转换为DTO并按分类分组
        return images.stream()
                .filter(image -> image.getIsVisible() != null && image.getIsVisible())
                .map(this::convertToDTO)
                .collect(Collectors.groupingBy(
                    ProductImageDTO::getCategoryName,
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> list.stream()
                                .sorted((a, b) -> Integer.compare(
                                    a.getDisplayOrder() != null ? a.getDisplayOrder() : 0,
                                    b.getDisplayOrder() != null ? b.getDisplayOrder() : 0
                                ))
                                .collect(Collectors.toList())
                    )
                ));
    }
    
    @Override
    public List<ImageCategory> getAllActiveCategories() {
        // 获取所有启用的图片分类
        return productImageMapper.findAllActiveCategories();
    }
    
    @Override
    public List<ImageCategory> getRecommendedCategoriesByProductType(String productType) {
        // 根据商品类型推荐分类
        List<ImageCategory> allCategories = getAllActiveCategories();
        
        // 根据商品类型过滤推荐分类
        return allCategories.stream()
                .filter(category -> isRecommendedForProductType(category, productType))
                .collect(Collectors.toList());
    }
    
    private ProductImageDTO convertToDTO(ProductImage image) {
        ProductImageDTO dto = new ProductImageDTO();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setAltText(image.getAltText());
        dto.setCustomLabel(image.getCustomLabel());
        dto.setDisplayOrder(image.getDisplayOrder());
        dto.setIsPrimary(image.getIsPrimary());
        
        // 获取分类显示名称
        ImageCategory category = productImageMapper.findCategoryById(image.getCategoryId());
        if (category != null) {
            dto.setCategoryName(category.getCategoryName());
            dto.setDisplayName(category.getDisplayName());
        }
        
        return dto;
    }
    
    private boolean isRecommendedForProductType(ImageCategory category, String productType) {
        // 根据商品类型推荐分类
        switch (productType) {
            case "electronics":
                return category.getCategoryType().equals("product") || 
                       category.getCategoryType().equals("detail") ||
                       category.getCategoryType().equals("certificate");
            case "clothing":
                return category.getCategoryType().equals("product") || 
                       category.getCategoryType().equals("scene") ||
                       category.getCategoryType().equals("custom");
            case "home":
                return category.getCategoryType().equals("scene") || 
                       category.getCategoryType().equals("detail") ||
                       category.getCategoryType().equals("custom");
            default:
                return true;
        }
    }
}