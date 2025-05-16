package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.common.api.CommonResult;
import com.example.model.Product;
import com.example.model.Shop;
import com.example.service.ProductService;
import com.example.service.ShopService;
import com.example.util.UserIdExtractor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private ShopService shopService;
    
    @Autowired
    private UserIdExtractor userIdExtractor;

    @GetMapping("")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategoryId(@PathVariable Long categoryId) {
        return productService.getProductsByCategoryId(categoryId);
    }

    @GetMapping("/random")
    public List<Product> getRandomProducts() {
        return productService.getRandomProducts();
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }
    
    /**
     * 根据用户ID获取商品列表
     */
    @GetMapping("/user")
    public CommonResult<List<Product>> getUserProducts(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            List<Product> products = productService.getProductsByUserId(userId);
            return CommonResult.success(products);
        } catch (Exception e) {
            log.error("获取用户商品列表时发生异常", e);
            return CommonResult.failed("获取用户商品列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据店铺ID获取商品列表
     */
    @GetMapping("/shop/{shopId}")
    public CommonResult<List<Product>> getProductsByShopId(@PathVariable Long shopId) {
        try {
            List<Product> products = productService.getProductsByShopId(shopId);
            return CommonResult.success(products);
        } catch (Exception e) {
            log.error("获取店铺商品列表时发生异常", e);
            return CommonResult.failed("获取店铺商品列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 创建商品
     */
    @PostMapping("")
    public CommonResult<Product> createProduct(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestParam("title") String title,
            @RequestParam("price") String price,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "specifications", required = false) String specifications,
            @RequestParam(value = "productDetails", required = false) String productDetails,
            @RequestParam(value = "purchaseNotices", required = false) String purchaseNotices,
            @RequestParam(value = "tagIds", required = false) List<Long> tagIds,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // 检查用户是否有店铺
            Shop shop = shopService.getShopByUserId(userId);
            if (shop == null) {
                return CommonResult.failed("您还没有创建店铺，请先创建店铺");
            }
            
            // 检查店铺状态
            if (shop.getStatus() != 1) {
                return CommonResult.failed("您的店铺当前状态不允许创建商品");
            }
            
            Product product = new Product();
            product.setTitle(title);
            product.setPrice(new java.math.BigDecimal(price));
            product.setCategoryId(categoryId);
            product.setUserId(userId);
            product.setShopId(shop.getId());
            product.setShopName(shop.getShopName());
            product.setShopAvatarUrl(shop.getShopLogo());
            
            Product createdProduct = productService.createProduct(product, specifications, productDetails, purchaseNotices, tagIds, image);
            return CommonResult.success(createdProduct);
        } catch (Exception e) {
            log.error("创建商品时发生异常", e);
            return CommonResult.failed("创建商品失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新商品
     */
    @PutMapping("/{id}")
    public CommonResult<Product> updateProduct(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestParam("title") String title,
            @RequestParam("price") String price,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "specifications", required = false) String specifications,
            @RequestParam(value = "productDetails", required = false) String productDetails,
            @RequestParam(value = "purchaseNotices", required = false) String purchaseNotices,
            @RequestParam(value = "tagIds", required = false) List<Long> tagIds,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // 验证商品所有权
            Product existingProduct = productService.getProductById(id);
            if (existingProduct == null) {
                return CommonResult.failed("商品不存在");
            }
            
            if (!existingProduct.getUserId().equals(userId)) {
                return CommonResult.failed("您没有权限修改该商品");
            }
            
            existingProduct.setTitle(title);
            existingProduct.setPrice(new java.math.BigDecimal(price));
            existingProduct.setCategoryId(categoryId);
            
            Product updatedProduct = productService.updateProduct(existingProduct, specifications, productDetails, purchaseNotices, tagIds, image);
            return CommonResult.success(updatedProduct);
        } catch (Exception e) {
            log.error("更新商品时发生异常", e);
            return CommonResult.failed("更新商品失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除商品（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public CommonResult<Void> deleteProduct(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // 验证商品所有权
            Product existingProduct = productService.getProductById(id);
            if (existingProduct == null) {
                return CommonResult.failed("商品不存在");
            }
            
            if (!existingProduct.getUserId().equals(userId)) {
                return CommonResult.failed("您没有权限删除该商品");
            }
            
            productService.deleteProduct(id);
            return CommonResult.success(null);
        } catch (Exception e) {
            log.error("删除商品时发生异常", e);
            return CommonResult.failed("删除商品失败：" + e.getMessage());
        }
    }
}