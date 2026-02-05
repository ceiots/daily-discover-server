package com.dailydiscover.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReviewStats {
    private Long id;
    private Long productId;
    private Integer totalReviews;
    private BigDecimal averageRating;
    private String ratingDistribution;
    private Integer verifiedReviewsCount;
    private Integer imageReviewsCount;
    private Integer videoReviewsCount;
    private Integer last30DaysReviews;
    private Integer helpfulReviewsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}