package com.example.user.application.service;

import com.example.common.exception.BusinessException;
import com.example.user.domain.service.BaseDomainService;
import com.example.user.infrastructure.common.result.ResultCode;

import org.springframework.beans.BeanUtils;

/**
 * 基础应用服务接口
 * 包含应用层通用逻辑，如参数验证、对象转换等
 */
public interface BaseApplicationService {
    
    /**
     * 获取基础领域服务
     * 
     * @return 基础领域服务
     */
    BaseDomainService getBaseDomainService();
    
    /**
     * 验证密码
     * 
     * @param plainPassword 明文密码
     * @param encodedPassword 加密后的密码
     * @param salt 盐值
     * @return 是否匹配
     */
    default boolean verifyPassword(String plainPassword, String encodedPassword, String salt) {
        return getBaseDomainService().verifyPassword(plainPassword, encodedPassword, salt);
    }
    
    /**
     * 加密密码
     * 
     * @param plainPassword 明文密码
     * @param salt 盐值
     * @return 加密后的密码
     */
    default String encryptPassword(String plainPassword, String salt) {
        return getBaseDomainService().encryptPassword(plainPassword, salt);
    }
    
    /**
     * 生成盐值
     * 
     * @return 盐值
     */
    default String generateSalt() {
        return getBaseDomainService().generateSalt();
    }
    
    /**
     * 验证手机号格式
     * 
     * @param mobile 手机号
     * @return 是否有效
     */
    default boolean isValidMobile(String mobile) {
        return getBaseDomainService().isValidMobile(mobile);
    }
    
    /**
     * 验证邮箱格式
     * 
     * @param email 邮箱
     * @return 是否有效
     */
    default boolean isValidEmail(String email) {
        return getBaseDomainService().isValidEmail(email);
    }
    
    /**
     * 验证用户名格式
     * 
     * @param username 用户名
     * @return 是否有效
     */
    default boolean isValidUsername(String username) {
        return getBaseDomainService().isValidUsername(username);
    }
    
    /**
     * 生成随机验证码
     * 
     * @param length 长度
     * @return 验证码
     */
    default String generateVerifyCode(int length) {
        return getBaseDomainService().generateVerifyCode(length);
    }
    
    /**
     * 验证密码强度
     * 
     * @param password 密码
     */
    default void validatePassword(String password) {
        if (!getBaseDomainService().validatePasswordStrength(password)) {
            throw new BusinessException(ResultCode.PASSWORD_TOO_WEAK);
        }
    }
    
    /**
     * 对象属性复制
     * 
     * @param source 源对象
     * @param target 目标对象
     */
    default void copyProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target);
    }
    
    /**
     * 验证验证码
     * 
     * @param mobile 手机号
     * @param code 验证码
     * @return 是否有效
     */
    default boolean verifyMobileCode(String mobile, String code) {
        // TODO: 实现验证码验证逻辑
        return true; // 默认通过，实际项目中需要实现真正的验证逻辑
    }
    
    /**
     * 验证邮箱验证码
     * 
     * @param email 邮箱
     * @param code 验证码
     * @return 是否有效
     */
    default boolean verifyEmailCode(String email, String code) {
        // TODO: 实现验证码验证逻辑
        return true; // 默认通过，实际项目中需要实现真正的验证逻辑
    }
} 