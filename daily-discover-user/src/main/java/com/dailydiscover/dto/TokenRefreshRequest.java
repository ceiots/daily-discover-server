package com.dailydiscover.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * Token刷新请求DTO
 */
@Data
public class TokenRefreshRequest {
    
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;
}