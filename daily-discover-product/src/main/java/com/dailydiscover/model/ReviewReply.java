package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewReply {
    private Long id;
    private Long reviewId;
    private Long userId;
    private Long parentReplyId;
    private String content;
    private Boolean isSellerReply;
    private Integer likeCount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}