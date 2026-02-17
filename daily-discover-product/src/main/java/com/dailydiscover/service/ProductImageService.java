package com.dailydiscover.service;

import com.dailydiscover.dto.ProductImageDTO;
import com.dailydiscover.model.ProductImage;
import com.dailydiscover.model.ImageCategory;
import java.util.List;
import java.util.Map;

/**
 * 商品图片服务接口
 */
public interface ProductImageService {
    
    /**
     * 根据商品ID获取分类后的图片列表
     */
    Map<String, List<ProductImageDTO>> getCategorizedImagesByProductId(Long productId);
    
    /**
     * 获取所有启用的图片分类
     */
    List<ImageCategory> getAllActiveCategories();
    
    /**
     * 根据商品类型推荐图片分类
     */
    List<ImageCategory> getRecommendedCategoriesByProductType(String productType);
}