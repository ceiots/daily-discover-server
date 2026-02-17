package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserReview {
    private Long id;
    private Long productId;
    private Long userId;
    private String userAvatar;
    private Long orderId;
    private Integer rating;
    private String title;
    private String comment;
    private String imageUrls;
    private String videoUrl;
    private Boolean isAnonymous;
    private Boolean isVerifiedPurchase;
    private Integer helpfulCount;
    private Integer replyCount;
    private Integer likeCount;
    private LocalDate reviewDate;
    private String status;
    private String moderationNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}