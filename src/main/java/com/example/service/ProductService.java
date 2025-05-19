package com.example.service;

import com.example.mapper.ProductMapper;
import com.example.model.Product;
import com.example.util.FileUploadUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private FileUploadUtil fileUploadUtil;
    
    @Autowired
    private ObjectMapper objectMapper;

    public List<Product> getAllProducts() {
        return productMapper.getAllProducts();
    }

    public Product getProductById(Long id) {
        return productMapper.findById(id);
    }

    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productMapper.findByCategoryId(categoryId);
    }

    public List<Product> getRandomProducts() {
        return productMapper.findRandom();
    }

    public List<Product> searchProducts(String keyword) {
        return productMapper.searchProducts(keyword);
    }
    
    public List<Product> getProductsByUserId(Long userId) {
        return productMapper.findByUserId(userId);
    }
    
    public List<Product> getProductsByShopId(Long shopId) {
        return productMapper.findByShopId(shopId);
    }
    
    /**
     * 创建商品
     */
    @Transactional
    public Product createProduct(Product product) throws Exception {
        // 设置创建时间
        product.setCreatedAt(new Date());
        product.setSoldCount(0);
        
        // 设置审核状态为待审核
        product.setAuditStatus(0);
        
        // 插入商品基本信息
        productMapper.insert(product);
        
        // 如果有标签，处理标签关联
        if (product.getTagIds() != null && !product.getTagIds().isEmpty()) {
            // TODO: 处理标签关联
        }
        
        return product;
    }
    
    /**
     * 更新商品
     */
    @Transactional
    public Product updateProduct(Product product) throws Exception {
        // 更新操作后，商品状态修改为待审核
        product.setAuditStatus(0);
        
        productMapper.update(product);
        return product;
    }
    
    /**
     * 删除商品（逻辑删除）
     */
    @Transactional
    public void deleteProduct(Long id) {
        productMapper.deleteById(id);
    }
    
    /**
     * 商品审核
     */
    @Transactional
    public Product auditProduct(Long id, Integer auditStatus, String auditRemark) {
        productMapper.updateAuditStatus(id, auditStatus, auditRemark);
        return productMapper.findById(id);
    }
    
    /**
     * 获取待审核商品列表
     */
    public List<Product> getPendingAuditProducts() {
        return productMapper.findPendingAuditProducts();
    }
}