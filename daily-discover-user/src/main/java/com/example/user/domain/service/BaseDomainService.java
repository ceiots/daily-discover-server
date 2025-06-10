package com.example.user.domain.service;

import com.example.user.domain.model.valueobject.Email;
import com.example.user.domain.model.valueobject.Mobile;
import com.example.user.domain.model.valueobject.Password;

import java.util.regex.Pattern;

/**
 * 领域服务基础接口
 * 所有领域服务都应该实现此接口，提供通用的领域服务功能
 */
public interface BaseDomainService {

    /**
     * 验证密码
     *
     * @param plainPassword 明文密码
     * @param encodedPassword 加密后的密码
     * @param salt 盐值
     * @return 是否匹配
     */
    boolean verifyPassword(String plainPassword, String encodedPassword, String salt);

    /**
     * 加密密码
     *
     * @param plainPassword 明文密码
     * @param salt 盐值
     * @return 加密后的密码
     */
    String encryptPassword(String plainPassword, String salt);

    /**
     * 生成盐值
     *
     * @return 盐值
     */
    String generateSalt();

    /**
     * 验证手机号格式
     *
     * @param mobile 手机号
     * @return 是否有效
     */
    boolean isValidMobile(String mobile);

    /**
     * 验证邮箱格式
     *
     * @param email 邮箱
     * @return 是否有效
     */
    boolean isValidEmail(String email);

    /**
     * 验证用户名格式
     *
     * @param username 用户名
     * @return 是否有效
     */
    boolean isValidUsername(String username);

    /**
     * 生成随机验证码
     *
     * @param length 长度
     * @return 验证码
     */
    String generateVerifyCode(int length);

    /**
     * 验证密码强度
     * 
     * @param password 密码
     * @return 是否符合要求
     */
    boolean validatePasswordStrength(String password);
    
    /**
     * 生成加密密码
     * 
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    default String encryptPassword(String rawPassword) {
        return Password.create(rawPassword).getValue();
    }
    
    /**
     * 验证密码
     * 
     * @param rawPassword 原始密码
     * @param encryptedPassword 加密后的密码
     * @return 是否匹配
     */
    default boolean matchPassword(String rawPassword, String encryptedPassword) {
        return Password.matches(rawPassword, encryptedPassword);
    }
} 