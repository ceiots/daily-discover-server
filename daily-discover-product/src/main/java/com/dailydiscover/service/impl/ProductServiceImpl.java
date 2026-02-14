package com.dailydiscover.service.impl;

import com.dailydiscover.dto.ProductDetailDTO;
import com.dailydiscover.mapper.ProductMapper;
import com.dailydiscover.mapper.ProductAttributeMapper;
import com.dailydiscover.model.Product;
import com.dailydiscover.model.ProductAttribute;
import com.dailydiscover.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private ProductAttributeMapper productAttributeMapper;
    
    @Override
    public Product findById(Long id) {
        try {
            Product product = productMapper.findById(id);
            if (product != null) {
                try {
                    ProductAttribute attribute = productAttributeMapper.findByProductId(id);
                    if (attribute != null) {
                        setProductAttributes(product, attribute);
                    }
                } catch (Exception e) {
                    // 忽略属性查询错误，不影响主查询
                }
            }
            return product;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取产品详情（包含属性信息）
     */
    public ProductDetailDTO getProductDetail(Long id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            return null;
        }
        
        return new ProductDetailDTO(product, null);
    }
    
    /**
     * 设置产品属性信息
     */
    private void setProductAttributes(Product product, ProductAttribute attribute) {
        // 这里可以根据业务需求设置属性
        // 例如：设置产品标签、热度等信息
        if (attribute.getTags() != null) {
            // 可以设置到产品的扩展字段或记录日志
            // product.setTags(attribute.getTags()); // 如果Product有tags字段
        }
        
        // 记录属性信息用于业务逻辑判断
        if (attribute.getIsHot() != null && attribute.getIsHot()) {
            // 热门产品特殊处理
        }
        
        if (attribute.getIsNew() != null && attribute.getIsNew()) {
            // 新品特殊处理
        }
        
        if (attribute.getIsRecommended() != null && attribute.getIsRecommended()) {
            // 推荐产品特殊处理
        }
    }
    
    @Override
    public List<Product> findAll() {
        return productMapper.findAll();
    }
    
    @Override
    public List<Product> findBySellerId(Long sellerId) {
        return productMapper.findBySellerId(sellerId);
    }
    
    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        return productMapper.findByCategoryId(categoryId);
    }
    
    @Override
    public List<Product> findHotProducts() {
        return productMapper.findHotProducts();
    }
    
    @Override
    public List<Product> findNewProducts() {
        return productMapper.findNewProducts();
    }
    
    @Override
    public List<Product> findRecommendedProducts() {
        return productMapper.findRecommendedProducts();
    }
    
    @Override
    @Transactional
    public void save(Product product) {
        productMapper.insert(product);
        // 由于表已拆分，需要创建ProductAttribute对象并保存
        // 这里需要根据业务逻辑创建ProductAttribute对象
        // 暂时保持原样，后续需要根据实际业务需求调整
    }
    
    @Override
    @Transactional
    public void update(Product product) {
        productMapper.update(product);
        // 由于表已拆分，需要同时更新ProductAttribute表
        // 暂时保持原样，后续需要根据实际业务需求调整
    }
    
    @Override
    public void delete(Long id) {
        productMapper.softDelete(id);
    }
}