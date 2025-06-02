package com.example.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.model.User;
import com.example.service.UserPaymentService;
import com.example.service.UserService;

/**
 * 用户支付服务实现类
 */
@Service
public class UserPaymentServiceImpl implements UserPaymentService {

    @Autowired
    private UserService userService;
    
    // 使用BCrypt加密支付密码
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    // 模拟验证码存储，实际项目中应该使用Redis等缓存
    private static final Map<Long, String> verificationCodes = new ConcurrentHashMap<>();
    
    @Override
    public Map<String, Object> getPaymentPasswordStatus(Long userId) {
        Map<String, Object> result = new HashMap<>();
        User user = userService.findUserById(userId);
        
        // 判断用户是否已设置支付密码
        boolean hasPaymentPassword = user != null && user.getPaymentPassword() != null;
        result.put("hasPaymentPassword", hasPaymentPassword);
        
        return result;
    }

    @Override
    public boolean sendVerificationCode(Long userId) {
        try {
            User user = userService.findUserById(userId);
            if (user == null) {
                return false;
            }
            
            // 生成6位随机验证码
            String verificationCode = generateRandomCode(6);
            
            // 存储验证码，实际项目中应该设置过期时间
            verificationCodes.put(userId, verificationCode);
            
            // 模拟发送验证码到用户手机
            // 实际项目中应该调用短信服务发送验证码
            System.out.println("向用户 " + userId + " 发送验证码: " + verificationCode);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean verifyPaymentPassword(Long userId, String paymentPassword) {
        User user = userService.findUserById(userId);
        if (user == null || user.getPaymentPassword() == null) {
            return false;
        }
        
        // 验证支付密码
        return passwordEncoder.matches(paymentPassword, user.getPaymentPassword());
    }

    @Override
    public boolean updatePaymentPassword(Long userId, String currentPassword, String newPassword, String verificationCode) throws Exception {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new Exception("用户不存在");
        }
        
        // 验证验证码
        String storedCode = verificationCodes.get(userId);
        if (storedCode == null || !storedCode.equals(verificationCode)) {
            throw new Exception("验证码错误或已过期");
        }
        
        // 如果已设置支付密码，需要验证当前密码
        if (user.getPaymentPassword() != null && currentPassword != null) {
            if (!passwordEncoder.matches(currentPassword, user.getPaymentPassword())) {
                throw new Exception("当前支付密码错误");
            }
        }
        
        // 验证新密码格式（6位数字）
        if (newPassword == null || !newPassword.matches("\\d{6}")) {
            throw new Exception("支付密码必须为6位数字");
        }
        
        // 加密新密码
        String encodedPassword = passwordEncoder.encode(newPassword);
        
        // 更新用户支付密码
        user.setPaymentPassword(encodedPassword);
        userService.updateUser(user);
        
        // 清除验证码
        verificationCodes.remove(userId);
        
        return true;
    }
    
    /**
     * 生成指定长度的随机数字验证码
     * @param length 验证码长度
     * @return 随机验证码
     */
    private String generateRandomCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
} 