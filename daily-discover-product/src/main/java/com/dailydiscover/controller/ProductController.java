package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.*;
import com.dailydiscover.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping("/{id}")
    @ApiLog("根据ID获取商品")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getById(id);
            return product != null ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    

    
    @GetMapping
    @ApiLog("获取所有商品")
    public ResponseEntity<List<Product>> getAllProducts() {
        try {
            List<Product> products = productService.list();
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
            List<Product> products = productService.findNewProducts();
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
            List<Product> products = productService.findHotProducts();
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
            List<Product> products = productService.findRecommendedProducts();
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
            List<Product> products = productService.findHotProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    @ApiLog("创建商品")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        try {
            boolean success = productService.save(product);
            return success ? ResponseEntity.status(HttpStatus.CREATED).body(product) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    @ApiLog("更新商品")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try {
            product.setId(id);
            boolean success = productService.updateById(product);
            return success ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    @ApiLog("删除商品")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            boolean success = productService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
