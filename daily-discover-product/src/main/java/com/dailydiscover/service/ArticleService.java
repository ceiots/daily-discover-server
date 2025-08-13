package com.dailydiscover.service;

import com.dailydiscover.model.Article;
import com.dailydiscover.model.Product;
import com.dailydiscover.mapper.ArticleMapper;
import com.dailydiscover.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class ArticleService {
    
    @Autowired
    private ArticleMapper articleMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    // 获取所有活跃的生活美学文章
    public List<Article> getLifestyleArticles() {
        return articleMapper.findByIsActiveTrue();
    }
    
    // 根据ID获取文章
    public Article getArticleById(Long id) {
        return articleMapper.findById(id);
    }
    
    // 创建新文章
    public Article createArticle(Article article) {
        articleMapper.insert(article);
        return article;
    }
    
    // 更新文章
    public Article updateArticle(Long id, Article articleDetails) {
        Article article = articleMapper.findById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        
        article.setTitle(articleDetails.getTitle());
        article.setSubtitle(articleDetails.getSubtitle());
        article.setAuthor(articleDetails.getAuthor());
        article.setReadTime(articleDetails.getReadTime());
        article.setCategory(articleDetails.getCategory());
        article.setImage(articleDetails.getImage());
        article.setContent(articleDetails.getContent());
        article.setTags(articleDetails.getTags());
        article.setUpdatedAt(java.time.LocalDateTime.now());
        
        articleMapper.update(article);
        return article;
    }
    
    // 删除文章
    public void deleteArticle(Long id) {
        Article article = articleMapper.findById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        article.setIsActive(false);
        article.setUpdatedAt(java.time.LocalDateTime.now());
        articleMapper.update(article);
    }
    
    // 获取文章的相关产品（随机选择3个产品）
    public List<Product> getRelatedProducts() {
        List<Product> allProducts = productMapper.findAllByIsActiveTrue();
        Random random = new Random();
        
        // 如果产品数量少于3个，返回所有产品
        if (allProducts.size() <= 3) {
            return allProducts;
        }
        
        // 随机选择3个产品
        return random.ints(0, allProducts.size())
                .distinct()
                .limit(3)
                .mapToObj(allProducts::get)
                .toList();
    }
    
    // 根据分类获取文章
    public List<Article> getArticlesByCategory(String category) {
        return articleMapper.findByCategoryAndIsActiveTrue(category);
    }
    
    // 搜索文章
    public List<Article> searchArticles(String keyword) {
        return articleMapper.searchActiveArticles(keyword);
    }
}