package com.example.dto;

import lombok.Data;

@Data
public class BrowsingDurationDTO {
    private Long userId;
    private Long productId;
    private Long categoryId;
    private Double duration;
}
