package com.example.service;

import java.util.Map;

/**
 * 用户支付服务接口
 */
public interface UserPaymentService {
    
    /**
     * 获取用户支付密码状态
     * @param userId 用户ID
     * @return 包含支付密码状态的Map
     */
    Map<String, Object> getPaymentPasswordStatus(Long userId);
    
    /**
     * 发送支付密码验证码
     * @param userId 用户ID
     * @return 是否发送成功
     */
    boolean sendVerificationCode(Long userId);
    
    /**
     * 验证支付密码
     * @param userId 用户ID
     * @param paymentPassword 支付密码
     * @return 是否验证成功
     */
    boolean verifyPaymentPassword(Long userId, String paymentPassword);
    
    /**
     * 更新支付密码
     * @param userId 用户ID
     * @param currentPassword 当前支付密码（可为null，表示首次设置）
     * @param newPassword 新支付密码
     * @param verificationCode 验证码
     * @return 是否更新成功
     * @throws Exception 更新失败时抛出异常
     */
    boolean updatePaymentPassword(Long userId, String currentPassword, String newPassword, String verificationCode) throws Exception;
} 