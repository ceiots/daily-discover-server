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

    /**
     * 获取商品详情，不管状态
     */
    public Product getProductById(Long id) {
        return productMapper.findById(id);
    }
    
    /**
     * 根据ID获取商品，需要考虑查询者是否为商品拥有者
     * @param id 商品ID
     * @param userId 当前用户ID，如果为null表示未登录用户
     * @return 商品信息或null
     */
    public Product getProductByIdWithPermission(Long id, Long userId) {
        Product product = productMapper.findById(id);
        
        // 商品不存在，返回null
        if (product == null) {
            return null;
        }
        
        // 商品已通过审核，任何人都可以查看
        if (product.getAuditStatus() != null && product.getAuditStatus() == 1) {
            return product;
        }
        
        // 商品未通过审核，但当前用户是商品拥有者，可以查看
        if (userId != null && product.getUserId().equals(userId)) {
            return product;
        }
        
        // TODO: 如果需要管理员权限判断，可以在此处添加
        
        // 其他情况（未通过审核且不是拥有者），返回null
        return null;
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
    
    /**
     * 获取用户创建的所有商品，包括待审核、已审核和审核未通过的商品
     */
    public List<Product> getProductsByUserId(Long userId) {
        return productMapper.findByUserId(userId);
    }
    
    /**
     * 获取店铺下的商品，只返回已通过审核的
     */
    public List<Product> getProductsByShopId(Long shopId) {
        return productMapper.findApprovedByShopId(shopId);
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

    public List<Product> getProductsWithPagination(int page, int size) {
        int offset = page * size;
        return productMapper.getProductsWithPagination(size, offset);
    }
    
    public int countApprovedProducts() {
        return productMapper.countApprovedProducts();
    }
}