package com.example.service;

import com.example.mapper.ProductMapper;
import com.example.model.Product;
import com.example.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.model.Specification;
import com.example.model.ProductDetail;
import com.example.model.PurchaseNotice;

import java.util.List;
import java.util.Date;

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
    
    /**
     * 根据店铺ID获取商品列表
     */
    public List<Product> getProductsByShopId(Long shopId) {
        return productMapper.findByShopId(shopId);
    }
    
    /**
     * 创建商品
     */
    public Product createProduct(Product product, String specificationsJson, String productDetailsJson, 
                                String purchaseNoticesJson, List<Long> tagIds, MultipartFile image) throws Exception {
        // 设置创建时间
        product.setCreatedAt(new Date());
        product.setSoldCount(0);
        
        // 处理商品图片
        if (image != null && !image.isEmpty()) {
            String imagePath = fileUploadUtil.uploadFile(image, "product/image");
            product.setImageUrl(imagePath);
        }
        
        // 处理规格信息
        if (specificationsJson != null && !specificationsJson.isEmpty()) {
            List<Specification> specifications = objectMapper.readValue(specificationsJson, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Specification.class));
            product.setSpecifications(specifications);
        }
        
        // 处理商品详情
        if (productDetailsJson != null && !productDetailsJson.isEmpty()) {
            List<ProductDetail> productDetails = objectMapper.readValue(productDetailsJson, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ProductDetail.class));
            product.setProductDetails(productDetails);
        }
        
        // 处理购买须知
        if (purchaseNoticesJson != null && !purchaseNoticesJson.isEmpty()) {
            List<PurchaseNotice> purchaseNotices = objectMapper.readValue(purchaseNoticesJson, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, PurchaseNotice.class));
            product.setPurchaseNotices(purchaseNotices);
        }
        
        // 插入商品基本信息
        productMapper.insert(product);
        
        // TODO: 处理标签关联
        
        return product;
    }
    
    /**
     * 更新商品
     */
    public Product updateProduct(Product product, String specificationsJson, String productDetailsJson, 
                                String purchaseNoticesJson, List<Long> tagIds, MultipartFile image) throws Exception {
        // 处理商品图片
        if (image != null && !image.isEmpty()) {
            String imagePath = fileUploadUtil.uploadFile(image, "product/image");
            product.setImageUrl(imagePath);
        }
        
        // 处理规格信息
        if (specificationsJson != null && !specificationsJson.isEmpty()) {
            List<Specification> specifications = objectMapper.readValue(specificationsJson, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Specification.class));
            product.setSpecifications(specifications);
        }
        
        // 处理商品详情
        if (productDetailsJson != null && !productDetailsJson.isEmpty()) {
            List<ProductDetail> productDetails = objectMapper.readValue(productDetailsJson, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ProductDetail.class));
            product.setProductDetails(productDetails);
        }
        
        // 处理购买须知
        if (purchaseNoticesJson != null && !purchaseNoticesJson.isEmpty()) {
            List<PurchaseNotice> purchaseNotices = objectMapper.readValue(purchaseNoticesJson, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, PurchaseNotice.class));
            product.setPurchaseNotices(purchaseNotices);
        }
        
        // 更新商品信息
        productMapper.update(product);
        
        // TODO: 处理标签关联
        
        return productMapper.findById(product.getId());
    }
    
    /**
     * 删除商品（逻辑删除）
     */
    public void deleteProduct(Long id) {
        productMapper.deleteById(id);
    }
}