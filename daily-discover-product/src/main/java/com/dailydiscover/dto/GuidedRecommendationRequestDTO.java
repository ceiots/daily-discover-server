package com.dailydiscover.dto;

import lombok.Data;
import java.util.List;

/**
 * 引导推荐请求DTO
 */
@Data
public class GuidedRecommendationRequestDTO {
    private String sessionId;     // 会话ID
    private String optionId;      // 本次点击的推荐词ID
    private List<String> path;    // 当前路径
    private Integer round;        // 当前轮次
    private Integer limit;        // 返回商品数量限制
    private Long userId;          // 用户ID（可选）
}