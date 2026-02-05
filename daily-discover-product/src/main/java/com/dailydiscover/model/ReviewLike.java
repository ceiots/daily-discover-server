package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewLike {
    private Long id;
    private Long reviewId;
    private Long userId;
    private LocalDateTime createdAt;
}