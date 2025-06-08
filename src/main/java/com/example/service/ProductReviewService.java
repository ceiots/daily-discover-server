package com.example.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.ProductReview;

import java.util.List;
import java.util.Map;

public interface ProductReviewService {

    /**
     * 创建商品评价
     * @param review 评价信息
     * @return 创建后的评价
     */
    @Transactional
    ProductReview createReview(ProductReview review);
    
    /**
     * 获取商品评价列表（支持分页和筛选）
     * @param productId 商品ID
     * @param hasImage 是否包含图片
     * @param rating 评分筛选
     * @param pageable 分页参数
     * @return 评价分页结果
     */
    Page<ProductReview> getProductReviews(Long productId, Boolean hasImage, Integer rating, Pageable pageable);
    
    /**
     * 获取商品评价统计数据
     * @param productId 商品ID
     * @return 统计数据
     */
    Map<String, Object> getProductReviewStats(Long productId);
    
    /**
     * 商家回复评价
     * @param reviewId 评价ID
     * @param replyContent 回复内容
     * @param userId 回复用户ID
     * @return 更新后的评价
     */
    @Transactional
    ProductReview replyReview(Long reviewId, String replyContent, Long userId);
    
    /**
     * 获取用户的评价列表
     * @param userId 用户ID
     * @return 评价列表
     */
    List<ProductReview> getUserReviews(Long userId);
    
    /**
     * 更新评价状态
     * @param reviewId 评价ID
     * @param status 评价状态
     * @param userId 操作用户ID
     * @return 更新后的评价
     */
    @Transactional
    ProductReview updateReviewStatus(Long reviewId, Integer status, Long userId);
    
    /**
     * 删除评价
     * @param reviewId 评价ID
     * @param userId 操作用户ID
     * @return 是否成功
     */
    @Transactional
    boolean deleteReview(Long reviewId, Long userId);
    
    /**
     * 根据评价ID获取评价
     * @param reviewId 评价ID
     * @return 评价
     */
    ProductReview getReviewById(Long reviewId);
    
    /**
     * 批量获取评价
     * @param reviewIds 评价ID列表
     * @return 评价列表
     */
    List<ProductReview> getReviewsByIds(List<Long> reviewIds);
    
    /**
     * 获取商品好评率
     * @param productId 商品ID
     * @return 好评率
     */
    double getProductPositiveRate(Long productId);
} 