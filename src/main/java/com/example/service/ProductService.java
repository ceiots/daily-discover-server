package com.example.service;

import com.example.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public interface ProductService {

    List<Product> getAllProducts();

    /**
     * 获取商品详情，不管状态
     */
    Product getProductById(Long id);
    
    /**
     * 根据ID获取商品，需要考虑查询者是否为商品所属店铺的拥有者
     * @param id 商品ID
     * @param userId 当前用户ID，如果为null表示未登录用户
     * @return 商品信息或null
     */
    Product getProductByIdWithPermission(Long id, Long userId);

    List<Product> getProductsByCategoryId(Long categoryId);

    List<Product> getRandomProducts();
    
    /**
     * 获取指定数量的随机商品
     * @param limit 要返回的商品数量
     * @return 随机商品列表
     */
    List<Product> getRandomProducts(int limit);

    List<Product> searchProducts(String keyword);
    
    /**
     * 获取店铺下的商品，只返回已通过审核的
     */
    List<Product> getProductsByShopId(Long shopId);
    
    /**
     * 创建商品
     */
    @Transactional
    Product createProduct(Product product) throws Exception;
    
    /**
     * 更新商品
     */
    @Transactional
    Product updateProduct(Product product) throws Exception;
    
    /**
     * 删除商品（逻辑删除）
     */
    @Transactional
    void deleteProduct(Long id);
    
    /**
     * 商品审核
     */
    @Transactional
    Product auditProduct(Long id, Integer auditStatus, String auditRemark);
    
    /**
     * 获取待审核商品列表
     */
    List<Product> getPendingAuditProducts();

    List<Product> getProductsWithPagination(int page, int size);
    
    int countApprovedProducts();
}