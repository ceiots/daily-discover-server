package com.dailydiscover.service.impl;

import com.dailydiscover.mapper.ProductDetailMapper;
import com.dailydiscover.model.*;
import com.dailydiscover.service.ProductDetailService;
import com.dailydiscover.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductDetailServiceImpl implements ProductDetailService {
    
    private final ProductDetailMapper productDetailMapper;
    private final ProductService productService;
    
    @Override
    public List<ProductImage> getProductImages(Long productId) {
        try {
            log.info("获取商品图片列表: productId={}", productId);
            return productDetailMapper.findImagesByProductId(productId);
        } catch (Exception e) {
            log.error("获取商品图片列表失败: productId={}", productId, e);
            throw new RuntimeException("获取商品图片列表失败", e);
        }
    }
    
    @Override
    public List<ProductSpec> getProductSpecifications(Long productId) {
        try {
            log.info("获取商品规格参数: productId={}", productId);
            return productDetailMapper.findSpecsByProductId(productId);
        } catch (Exception e) {
            log.error("获取商品规格参数失败: productId={}", productId, e);
            throw new RuntimeException("获取商品规格参数失败", e);
        }
    }
    
    @Override
    public List<ProductSku> getProductSKUs(Long productId) {
        try {
            log.info("获取商品SKU列表: productId={}", productId);
            return productDetailMapper.findSkusByProductId(productId);
        } catch (Exception e) {
            log.error("获取商品SKU列表失败: productId={}", productId, e);
            throw new RuntimeException("获取商品SKU列表失败", e);
        }
    }
    
    @Override
    public ProductDetail getProductDetailInfo(Long productId) {
        try {
            log.info("获取商品详情信息: productId={}", productId);
            return productDetailMapper.findByProductId(productId);
        } catch (Exception e) {
            log.error("获取商品详情信息失败: productId={}", productId, e);
            throw new RuntimeException("获取商品详情信息失败", e);
        }
    }
    
    @Override
    public List<String> getProductFeatures(Long productId) {
        try {
            log.info("获取商品特性: productId={}", productId);
            ProductDetail detail = productDetailMapper.findByProductId(productId);
            if (detail != null) {
                return detail.getFeaturesList();
            }
            return List.of();
        } catch (Exception e) {
            log.error("获取商品特性失败: productId={}", productId, e);
            throw new RuntimeException("获取商品特性失败", e);
        }
    }
    
    @Override
    public List<Product> getRelatedProducts(Long productId) {
        try {
            log.info("获取相关商品推荐: productId={}", productId);
            Product product = productService.findById(productId);
            if (product != null) {
                List<Product> products = productService.findByCategoryId(product.getCategoryId());
                products = products.stream()
                    .filter(p -> !p.getId().equals(productId))
                    .limit(10)
                    .collect(Collectors.toList());
                return products;
            }
            return List.of();
        } catch (Exception e) {
            log.error("获取相关商品推荐失败: productId={}", productId, e);
            throw new RuntimeException("获取相关商品推荐失败", e);
        }
    }
    
    @Override
    public String getProductDescription(Long productId) {
        try {
            log.info("获取商品描述信息: productId={}", productId);
            ProductDetail detail = productDetailMapper.findByProductId(productId);
            if (detail != null) {
                // 使用usageInstructions作为描述信息
                return detail.getUsageInstructions();
            }
            return "";
        } catch (Exception e) {
            log.error("获取商品描述信息失败: productId={}", productId, e);
            throw new RuntimeException("获取商品描述信息失败", e);
        }
    }
    
    @Override
    public String getProductPackaging(Long productId) {
        try {
            log.info("获取商品包装信息: productId={}", productId);
            ProductDetail detail = productDetailMapper.findByProductId(productId);
            if (detail != null) {
                // 使用packageContents作为包装信息
                return detail.getPackageContents();
            }
            return "";
        } catch (Exception e) {
            log.error("获取商品包装信息失败: productId={}", productId, e);
            throw new RuntimeException("获取商品包装信息失败", e);
        }
    }
}