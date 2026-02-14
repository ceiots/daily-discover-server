package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.dto.ProductDetailDTO;
import com.dailydiscover.mapper.*;
import com.dailydiscover.model.*;
import com.dailydiscover.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * 商品控制器 - RESTful风格
 */
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @Autowired
    private ProductImageMapper productImageMapper;
    
    @Autowired
    private ProductSpecMapper productSpecMapper;
    
    @Autowired
    private ProductSkuMapper productSkuMapper;
    
    @Autowired
    private ProductDetailMapper productDetailMapper;
    
    @Autowired
    private UserReviewMapper userReviewMapper;
    
    @GetMapping("/{id}")
    @ApiLog("获取产品详情")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.findById(id);
            if (product != null) {
                return ResponseEntity.ok(product);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/detail")
    @ApiLog("获取产品完整详情")
    public ResponseEntity<ProductDetailDTO> getProductDetail(@PathVariable Long id) {
        try {
            ProductDetailDTO productDetail = productService.getProductDetail(id);
            if (productDetail != null) {
                return ResponseEntity.ok(productDetail);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping
    @ApiLog("获取所有产品")
    public ResponseEntity<List<Product>> getAllProducts() {
        try {
            List<Product> products = productService.findAll();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/seller/{sellerId}")
    @ApiLog("根据商家获取产品")
    public ResponseEntity<List<Product>> getProductsBySeller(@PathVariable Long sellerId) {
        try {
            List<Product> products = productService.findBySellerId(sellerId);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/category/{categoryId}")
    @ApiLog("根据分类获取产品")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId) {
        try {
            List<Product> products = productService.findByCategoryId(categoryId);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/hot")
    @ApiLog("获取热门产品")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<Product>> getHotProducts() {
        try {
            List<Product> products = productService.findHotProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/new")
    @ApiLog("获取新品产品")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<Product>> getNewProducts() {
        try {
            List<Product> products = productService.findNewProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/recommended")
    @ApiLog("获取推荐产品")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<Product>> getRecommendedProducts() {
        try {
            List<Product> products = productService.findRecommendedProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/daily-new")
    @ApiLog("获取每日上新商品")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<Product>> getDailyNewProducts() {
        try {
            List<Product> products = productService.findNewProducts(); // 暂时使用新品接口
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/hotspots")
    @ApiLog("获取实时热点")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<Product>> getHotspots() {
        try {
            List<Product> products = productService.findHotProducts(); // 暂时使用热门接口
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/tomorrow-contents")
    @ApiLog("获取明日内容")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<Product>> getTomorrowContents() {
        try {
            List<Product> products = productService.findRecommendedProducts(); // 暂时使用推荐接口
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/coupons")
    @ApiLog("获取优惠券")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<Product>> getCoupons() {
        try {
            List<Product> products = productService.findHotProducts(); // 暂时使用热门接口
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    @ApiLog("创建产品")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        try {
            productService.save(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    @ApiLog("更新产品")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try {
            product.setId(id);
            productService.update(product);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    @ApiLog("删除产品")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/images")
    @ApiLog("获取商品图片列表")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ProductImage>> getProductImages(@PathVariable Long id) {
        try {
            List<ProductImage> images = productImageMapper.findByProductId(id);
            return ResponseEntity.ok(images);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/specifications")
    @ApiLog("获取商品规格参数")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ProductSpec>> getProductSpecifications(@PathVariable Long id) {
        try {
            List<ProductSpec> specs = productSpecMapper.findByProductId(id);
            return ResponseEntity.ok(specs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/skus")
    @ApiLog("获取商品SKU列表")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ProductSku>> getProductSKUs(@PathVariable Long id) {
        try {
            List<ProductSku> skus = productSkuMapper.findByProductId(id);
            return ResponseEntity.ok(skus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/details")
    @ApiLog("获取商品详情信息")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ProductDetail> getProductDetailInfo(@PathVariable Long id) {
        try {
            ProductDetail detail = productDetailMapper.findByProductId(id);
            if (detail != null) {
                return ResponseEntity.ok(detail);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/reviews")
    @ApiLog("获取商品评价列表")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<UserReview>> getProductReviews(@PathVariable Long id) {
        try {
            List<UserReview> reviews = userReviewMapper.findByProductId(id);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/features")
    @ApiLog("获取商品特性")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<String>> getProductFeatures(@PathVariable Long id) {
        try {
            ProductDetail detail = productDetailMapper.findByProductId(id);
            if (detail != null) {
                return ResponseEntity.ok(detail.getFeaturesList());
            }
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/related")
    @ApiLog("获取相关商品推荐")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<Product>> getRelatedProducts(@PathVariable Long id) {
        try {
            // 暂时返回同分类的其他商品作为相关商品
            Product product = productService.findById(id);
            if (product != null) {
                List<Product> products = productService.findByCategoryId(product.getCategoryId());
                // 过滤掉当前商品
                products = products.stream()
                    .filter(p -> !p.getId().equals(id))
                    .limit(10)
                    .collect(Collectors.toList());
                return ResponseEntity.ok(products);
            }
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}