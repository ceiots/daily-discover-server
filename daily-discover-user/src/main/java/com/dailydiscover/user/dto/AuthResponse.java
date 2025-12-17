package com.dailydiscover.dto;

import com.dailydiscover.user.entity.User;
import lombok.Data;

/**
 * 认证响应DTO
 */
@Data
public class AuthResponse {
    
    private User user;
    
    private String token;
    
    private String refreshToken;
    
    private Long expiresIn;
}