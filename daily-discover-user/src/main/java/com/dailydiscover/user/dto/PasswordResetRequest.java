package com.dailydiscover.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 密码重置请求DTO
 */
@Data
public class PasswordResetRequest {
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String newPassword;
    
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
    
    private String token;
}