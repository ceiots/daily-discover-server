package com.dailydiscover.controller;

import com.dailydiscover.model.Product;
import com.dailydiscover.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.findById(id);
    }
    
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAll();
    }
    
    @GetMapping("/seller/{sellerId}")
    public List<Product> getProductsBySeller(@PathVariable Long sellerId) {
        return productService.findBySellerId(sellerId);
    }
    
    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategory(@PathVariable Long categoryId) {
        return productService.findByCategoryId(categoryId);
    }
    
    @GetMapping("/hot")
    public List<Product> getHotProducts() {
        return productService.findHotProducts();
    }
    
    @GetMapping("/new")
    public List<Product> getNewProducts() {
        return productService.findNewProducts();
    }
    
    @GetMapping("/recommended")
    public List<Product> getRecommendedProducts() {
        return productService.findRecommendedProducts();
    }
    
    @PostMapping
    public void createProduct(@RequestBody Product product) {
        productService.save(product);
    }
    
    @PutMapping("/{id}")
    public void updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        productService.update(product);
    }
    
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.delete(id);
    }
}