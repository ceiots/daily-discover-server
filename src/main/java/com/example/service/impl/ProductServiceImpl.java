package com.example.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.mapper.ProductMapper;
import com.example.model.Product;
import com.example.model.Specification;
import com.example.service.ProductService;
import com.example.service.ShopService;
import com.example.util.FileUploadUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import com.example.service.ProductSkuService;
import com.example.model.ProductSku;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private FileUploadUtil fileUploadUtil;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ShopService shopService;
    
    @Autowired
    private ProductSkuService productSkuService;
    
    @Override
    public List<Product> getAllProducts() {
        return productMapper.getAllProducts();
    }
    
    @Override
    public List<Product> getProductsWithPagination(int page, int size) {
        int offset = page * size;
        return productMapper.getProductsWithPagination(size, offset);
    }
    
    @Override
    public Product getProductById(Long id) {
        Product product = productMapper.findById(id);
        if (product != null) {
            // 加载商品SKU
            List<ProductSku> skus = productSkuService.getSkusByProductId(id);
            product.setSkus(skus);
        }
        return product;
    }
    
    @Override
    public Product getProductByIdWithPermission(Long id, Long userId) {
        Product product = productMapper.findById(id);
        
        // 如果商品不存在，直接返回null
        if (product == null) {
            return null;
        }
        
        // 如果商品已通过审核，任何人都可以查看
        if (product.getAuditStatus() != null && product.getAuditStatus() == 1) {
            return product;
        }
        
        // 否则，只有商品所属店铺的用户可以查看
        // 这里需要获取店铺信息，并检查当前用户是否是店铺的拥有者
        if (userId != null && product.getShopId() != null) {
            // 检查当前用户是否是该店铺的拥有者
            Long shopOwnerId = shopService.getShopOwnerIdByShopId(product.getShopId());
            if (shopOwnerId != null && shopOwnerId.equals(userId)) {
                return product;
            }
        }
        
        // 其他情况（未通过审核且不是店铺拥有者），返回null
        return null;
    }
    
    @Override
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productMapper.findByCategoryId(categoryId);
    }
    
    @Override
    public List<Product> getRandomProducts() {
        return productMapper.findRandom();
    }
    
    @Override
    public List<Product> getRandomProducts(int limit) {
        return productMapper.findRandomWithLimit(limit);
    }
    
    @Override
    public List<Product> searchProducts(String keyword) {
        return productMapper.searchProducts(keyword);
    }
    
    @Override
    public List<Product> getProductsByShopId(Long shopId) {
        return productMapper.findApprovedByShopId(shopId);
    }
    
    @Override
    @Transactional
    public Product createProduct(Product product) throws Exception {
        // 设置默认审核状态为"待审核"
        product.setAuditStatus(0);
        product.setCreatedAt(new Date());
        product.setSoldCount(0);
        
        productMapper.insert(product);
        
        // 如果有标签，处理标签关联
        if (product.getTagIds() != null && !product.getTagIds().isEmpty()) {
            // TODO: 处理标签关联
        }
        
        // 处理商品SKU
        List<ProductSku> skus = product.getSkus();
        if (skus != null && !skus.isEmpty()) {
            for (ProductSku sku : skus) {
                sku.setProductId(product.getId());
                productSkuService.createSku(sku);
            }
        }
        
        return product;
    }
    
    @Override
    @Transactional
    public Product updateProduct(Product product) throws Exception {
        // 更新操作后，商品状态修改为待审核
        product.setAuditStatus(0);
        
        productMapper.update(product);
        
        // 处理商品SKU
        List<ProductSku> skus = product.getSkus();
        if (skus != null) {
            // 先删除原有的SKU
            productSkuService.deleteSkusByProductId(product.getId());
            
            // 再保存新的SKU
            if (!skus.isEmpty()) {
                for (ProductSku sku : skus) {
                    sku.setId(null); // 确保是新增
                    sku.setProductId(product.getId());
                    productSkuService.createSku(sku);
                }
            }
        }
        
        return product;
    }
    
    @Override
    @Transactional
    public void deleteProduct(Long id) {
        // 删除商品SKU
        productSkuService.deleteSkusByProductId(id);
        
        productMapper.deleteById(id);
    }
    
    @Override
    public List<Product> getPendingAuditProducts() {
        return productMapper.findPendingAuditProducts();
    }
    
    @Override
    @Transactional
    public Product auditProduct(Long id, Integer auditStatus, String auditRemark) {
        productMapper.updateAuditStatus(id, auditStatus, auditRemark);
        return productMapper.findById(id);
    }
    
    @Override
    public int countApprovedProducts() {
        return productMapper.countApprovedProducts();
    }
    
    @Override
    public int countProducts() {
        // 使用已有的方法获取所有商品数量
        return productMapper.countApprovedProducts(); // 暂时使用已有方法，后续可以改进为统计所有商品
    }
} 