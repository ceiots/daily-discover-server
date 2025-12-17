package com.dailydiscover.user.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * Token刷新请求DTO
 */
@Data
public class TokenRefreshRequest {
    
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;
}