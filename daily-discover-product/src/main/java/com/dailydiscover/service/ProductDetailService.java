package com.dailydiscover.service;

import com.dailydiscover.model.ProductImage;
import com.dailydiscover.model.ProductSpec;
import com.dailydiscover.model.ProductSku;
import com.dailydiscover.model.ProductDetail;
import com.dailydiscover.model.Product;
import java.util.List;

public interface ProductDetailService {
    
    /**
     * 获取商品图片列表
     * @param productId 商品ID
     * @return 图片列表
     */
    List<ProductImage> getProductImages(Long productId);
    
    /**
     * 获取商品规格参数
     * @param productId 商品ID
     * @return 规格列表
     */
    List<ProductSpec> getProductSpecifications(Long productId);
    
    /**
     * 获取商品SKU列表
     * @param productId 商品ID
     * @return SKU列表
     */
    List<ProductSku> getProductSKUs(Long productId);
    
    /**
     * 获取商品详情信息
     * @param productId 商品ID
     * @return 商品详情
     */
    ProductDetail getProductDetailInfo(Long productId);
    
    /**
     * 获取商品特性
     * @param productId 商品ID
     * @return 特性列表
     */
    List<String> getProductFeatures(Long productId);
    
    /**
     * 获取相关商品推荐
     * @param productId 商品ID
     * @return 相关商品列表
     */
    List<Product> getRelatedProducts(Long productId);
    
    /**
     * 获取商品描述信息
     * @param productId 商品ID
     * @return 描述信息
     */
    String getProductDescription(Long productId);
    
    /**
     * 获取商品包装信息
     * @param productId 商品ID
     * @return 包装信息
     */
    String getProductPackaging(Long productId);
}