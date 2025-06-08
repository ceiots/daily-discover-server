package com.example.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.common.exception.ApiException;
import com.example.mapper.ProductMapper;
import com.example.mapper.ProductReviewMapper;
import com.example.model.Product;
import com.example.model.ProductReview;
import com.example.service.ProductReviewService;
import com.example.service.ProductStatisticsService;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Service
public class ProductReviewServiceImpl implements ProductReviewService {

    @Autowired
    private ProductReviewMapper productReviewMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private ProductStatisticsService productStatisticsService;
    
    @Override
    @Transactional
    public ProductReview createReview(ProductReview review) {
        // 设置默认值
        if (review.getStatus() == null) {
            review.setStatus(1); // 默认显示
        }
        
        review.setHasImage(review.getImages() != null && !review.getImages().isEmpty());
        
        try {
            productReviewMapper.insert(review);
            
            // 更新商品的评价统计数据
            updateProductReviewStats(review.getProductId());
            
            return productReviewMapper.findById(review.getId());
        } catch (Exception e) {
            log.error("创建商品评价失败", e);
            throw new ApiException("创建商品评价失败: " + e.getMessage());
        }
    }
    
    @Override
    public Page<ProductReview> getProductReviews(Long productId, Boolean hasImage, Integer rating, Pageable pageable) {
        try {
            // 获取所有符合条件的评价
            List<ProductReview> allReviews = productReviewMapper.findByUserId(productId);
            
            // 进行筛选
            if (hasImage != null || rating != null) {
                allReviews = allReviews.stream()
                    .filter(review -> {
                        boolean match = true;
                        if (hasImage != null) {
                            match = match && review.getHasImage() == hasImage;
                        }
                        if (rating != null) {
                            match = match && review.getRating() == rating;
                        }
                        return match;
                    })
                    .toList();
            }
            
            // 手动分页
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), allReviews.size());
            
            if (start >= allReviews.size()) {
                return new PageImpl<>(Collections.emptyList(), pageable, allReviews.size());
            }
            
            List<ProductReview> pageContent = allReviews.subList(start, end);
            
            return new PageImpl<>(pageContent, pageable, allReviews.size());
        } catch (Exception e) {
            log.error("获取商品评价列表失败", e);
            throw new ApiException("获取商品评价列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public Map<String, Object> getProductReviewStats(Long productId) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 总评价数
            int totalReviews = productReviewMapper.countByProductId(productId);
            stats.put("totalReviews", totalReviews);
            
            // 好评数
            int positiveReviews = productReviewMapper.countPositiveByProductId(productId);
            stats.put("positiveReviews", positiveReviews);
            
            // 差评数
            int negativeReviews = productReviewMapper.countNegativeByProductId(productId);
            stats.put("negativeReviews", negativeReviews);
            
            // 平均评分
            double averageRating = productReviewMapper.getAverageRating(productId);
            stats.put("averageRating", averageRating);
            
            // 好评率
            double positiveRate = totalReviews > 0 ? (double) positiveReviews / totalReviews * 100 : 0;
            stats.put("positiveRate", positiveRate);
            
            // 各评分数量
            Map<Integer, Integer> ratingCounts = new HashMap<>();
            for (int i = 1; i <= 5; i++) {
                ratingCounts.put(i, productReviewMapper.countByRating(productId, i));
            }
            stats.put("ratingCounts", ratingCounts);
            
            // 有图评价数
            int withImageCount = productReviewMapper.countWithImage(productId);
            stats.put("withImageCount", withImageCount);
            
            return stats;
        } catch (Exception e) {
            log.error("获取商品评价统计失败", e);
            throw new ApiException("获取商品评价统计失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public ProductReview replyReview(Long reviewId, String replyContent, Long userId) {
        ProductReview review = productReviewMapper.findById(reviewId);
        if (review == null) {
            throw new ApiException("评价不存在");
        }
        
        // 验证权限：只有商品所属店铺的管理员可以回复
        Product product = productMapper.findById(review.getProductId());
        if (product == null || !product.getShopId().equals(userId)) {
            throw new ApiException("没有权限回复该评价");
        }
        
        try {
            productReviewMapper.updateReply(reviewId, replyContent);
            return productReviewMapper.findById(reviewId);
        } catch (Exception e) {
            log.error("回复商品评价失败", e);
            throw new ApiException("回复商品评价失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<ProductReview> getUserReviews(Long userId) {
        try {
            return productReviewMapper.findByUserId(userId);
        } catch (Exception e) {
            log.error("获取用户评价列表失败", e);
            throw new ApiException("获取用户评价列表失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public ProductReview updateReviewStatus(Long reviewId, Integer status, Long userId) {
        ProductReview review = productReviewMapper.findById(reviewId);
        if (review == null) {
            throw new ApiException("评价不存在");
        }
        
        // 验证权限：只有评价所有者或商品所属店铺的管理员可以更新状态
        if (!review.getUserId().equals(userId)) {
            Product product = productMapper.findById(review.getProductId());
            if (product == null || !product.getShopId().equals(userId)) {
                throw new ApiException("没有权限更新该评价状态");
            }
        }
        
        try {
            productReviewMapper.updateStatus(reviewId, status);
            
            // 状态变更后更新统计数据
            updateProductReviewStats(review.getProductId());
            
            return productReviewMapper.findById(reviewId);
        } catch (Exception e) {
            log.error("更新评价状态失败", e);
            throw new ApiException("更新评价状态失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean deleteReview(Long reviewId, Long userId) {
        ProductReview review = productReviewMapper.findById(reviewId);
        if (review == null) {
            throw new ApiException("评价不存在");
        }
        
        // 验证权限：只有评价所有者或商品所属店铺的管理员可以删除评价
        if (!review.getUserId().equals(userId)) {
            Product product = productMapper.findById(review.getProductId());
            if (product == null || !product.getShopId().equals(userId)) {
                throw new ApiException("没有权限删除该评价");
            }
        }
        
        try {
            int rows = productReviewMapper.deleteById(reviewId);
            if (rows > 0) {
                // 更新商品的评价统计数据
                updateProductReviewStats(review.getProductId());
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("删除评价失败", e);
            throw new ApiException("删除评价失败: " + e.getMessage());
        }
    }
    
    @Override
    public ProductReview getReviewById(Long reviewId) {
        return productReviewMapper.findById(reviewId);
    }
    
    @Override
    public List<ProductReview> getReviewsByIds(List<Long> reviewIds) {
        if (reviewIds == null || reviewIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<ProductReview> reviews = new ArrayList<>();
        for (Long id : reviewIds) {
            ProductReview review = productReviewMapper.findById(id);
            if (review != null) {
                reviews.add(review);
            }
        }
        return reviews;
    }
    
    @Override
    public double getProductPositiveRate(Long productId) {
        try {
            int totalReviews = productReviewMapper.countByProductId(productId);
            if (totalReviews == 0) {
                return 0;
            }
            
            int positiveReviews = productReviewMapper.countPositiveByProductId(productId);
            return (double) positiveReviews / totalReviews * 100;
        } catch (Exception e) {
            log.error("获取商品好评率失败", e);
            return 0;
        }
    }
    
    /**
     * 更新商品的评价统计数据
     */
    private void updateProductReviewStats(Long productId) {
        try {
            // 获取评价统计数据
            Map<String, Object> stats = getProductReviewStats(productId);
            
            // 更新商品统计表中的评价数据
            productStatisticsService.updateReviewStats(
                productId,
                (Double) stats.get("averageRating"),
                (Integer) stats.get("totalReviews"),
                (Integer) stats.get("positiveReviews"),
                (Integer) stats.get("negativeReviews")
            );
        } catch (Exception e) {
            log.error("更新商品评价统计数据失败", e);
        }
    }
} 