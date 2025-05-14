package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.api.CommonResult;
import com.example.model.Product;
import com.example.service.ProductService;
import com.example.util.JwtTokenUtil;
import com.example.util.UserIdExtractor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    @Autowired
    private ProductService productService;
    
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
     * @param token JWT令牌
     * @param userIdHeader 用户ID请求头
     * @return 通用结果，包含商品列表
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
    
}