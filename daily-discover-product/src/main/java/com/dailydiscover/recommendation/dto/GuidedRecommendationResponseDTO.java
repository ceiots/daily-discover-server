package com.dailydiscover.recommendation.dto;

import lombok.Data;
import java.util.List;

/**
 * 引导推荐响应DTO
 */
@Data
public class GuidedRecommendationResponseDTO {
    private List<GuidedProductDTO> products;      // 当前轮次的商品
    private List<GuidedOptionDTO> nextOptions;    // 下一轮次的推荐词
    private List<String> currentPath;             // 当前路径
    private Integer round;                        // 当前轮次
}