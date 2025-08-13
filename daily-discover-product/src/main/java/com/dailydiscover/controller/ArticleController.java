package com.dailydiscover.controller;

import com.dailydiscover.model.Article;
import com.dailydiscover.model.Product;
import com.dailydiscover.service.ArticleService;
import com.dailydiscover.service.ArticleOptimizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    
    @Autowired
    private ArticleService articleService;
    
    @Autowired
    private ArticleOptimizationService optimizationService;
    
    // 获取生活美学文章
    @GetMapping("/lifestyle")
    public List<Map<String, Object>> getLifestyleArticles() {
        List<Article> articles = articleService.getLifestyleArticles();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Article article : articles) {
            // 优化文章
            Article optimizedArticle = optimizationService.optimizeArticle(article);
            
            Map<String, Object> articleData = new HashMap<>();
            articleData.put("id", optimizedArticle.getId());
            articleData.put("title", optimizedArticle.getTitle());
            articleData.put("subtitle", optimizedArticle.getSubtitle());
            articleData.put("author", optimizedArticle.getAuthor());
            articleData.put("readTime", optimizedArticle.getReadTime());
            articleData.put("category", optimizedArticle.getCategory());
            articleData.put("image", optimizedArticle.getImage());
            
            // 提取摘要而不是完整内容
            String summary = optimizationService.extractSummary(optimizedArticle.getContent(), 100);
            articleData.put("summary", summary);
            
            // 提取关键点
            List<String> keyPoints = optimizationService.extractKeyPoints(optimizedArticle.getContent(), 3);
            articleData.put("keyPoints", keyPoints);
            
            articleData.put("tags", optimizedArticle.getTags());
            
            // 获取相关产品
            List<Product> relatedProducts = articleService.getRelatedProducts();
            List<Map<String, Object>> productList = new ArrayList<>();
            
            for (Product product : relatedProducts) {
                Map<String, Object> productData = new HashMap<>();
                productData.put("id", product.getId());
                productData.put("title", product.getTitle());
                productData.put("price", product.getPrice());
                productData.put("image", product.getImageUrl());
                productData.put("tag", product.getTag());
                
                // 添加产品特性
                productData.put("features", product.getFeatures());
                productData.put("isHotSale", product.getIsHotSale());
                productData.put("isHighQuality", product.getIsHighQuality());
                productData.put("isFastDelivery", product.getIsFastDelivery());
                
                productList.add(productData);
            }
            
            articleData.put("relatedProducts", productList);
            result.add(articleData);
        }
        
        return result;
    }
    
    // 获取文章详情
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getArticleById(@PathVariable Long id) {
        Article article = articleService.getArticleById(id);
        if (article != null) {
            // 优化文章
            Article optimizedArticle = optimizationService.optimizeArticle(article);
            
            Map<String, Object> articleData = new HashMap<>();
            articleData.put("id", optimizedArticle.getId());
            articleData.put("title", optimizedArticle.getTitle());
            articleData.put("subtitle", optimizedArticle.getSubtitle());
            articleData.put("author", optimizedArticle.getAuthor());
            articleData.put("readTime", optimizedArticle.getReadTime());
            articleData.put("category", optimizedArticle.getCategory());
            articleData.put("image", optimizedArticle.getImage());
            
            // 优化后的内容
            articleData.put("content", optimizedArticle.getContent());
            
            // 提取关键点
            List<String> keyPoints = optimizationService.extractKeyPoints(optimizedArticle.getContent(), 5);
            articleData.put("keyPoints", keyPoints);
            
            articleData.put("tags", optimizedArticle.getTags());
            
            // 获取相关产品
            List<Product> relatedProducts = articleService.getRelatedProducts();
            List<Map<String, Object>> productList = new ArrayList<>();
            
            for (Product product : relatedProducts) {
                Map<String, Object> productData = new HashMap<>();
                productData.put("id", product.getId());
                productData.put("title", product.getTitle());
                productData.put("price", product.getPrice());
                productData.put("image", product.getImageUrl());
                productData.put("tag", product.getTag());
                
                // 添加产品特性
                productData.put("features", product.getFeatures());
                productData.put("isHotSale", product.getIsHotSale());
                productData.put("isHighQuality", product.getIsHighQuality());
                productData.put("isFastDelivery", product.getIsFastDelivery());
                
                productList.add(productData);
            }
            
            articleData.put("relatedProducts", productList);
            return ResponseEntity.ok(articleData);
        }
        return ResponseEntity.notFound().build();
    }
    
    // 创建新文章
    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        Article newArticle = articleService.createArticle(article);
        return ResponseEntity.ok(newArticle);
    }
    
    // 更新文章
    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @RequestBody Article article) {
        try {
            Article updatedArticle = articleService.updateArticle(id, article);
            return ResponseEntity.ok(updatedArticle);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 删除文章
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        try {
            articleService.deleteArticle(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 根据分类获取文章
    @GetMapping("/category/{category}")
    public List<Article> getArticlesByCategory(@PathVariable String category) {
        return articleService.getArticlesByCategory(category);
    }
    
    // 搜索文章
    @GetMapping("/search")
    public List<Article> searchArticles(@RequestParam String keyword) {
        return articleService.searchArticles(keyword);
    }
}