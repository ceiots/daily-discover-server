package com.example.service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.mapper.UserMapper;
import com.example.model.User;
import com.example.util.JwtTokenUtil;

@Service
public class UserService {

    private final Map<String, String> verificationCodes = new HashMap<>();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public Map<String, Object> login(String phoneNumber, String password) {
        Map<String, Object> result = new HashMap<>();
        
        User user = userMapper.findByPhoneNumber(phoneNumber);
        
        if (user == null) {
            result.put("code", 400);
            result.put("message", "用户不存在");
            return result;
        }
        
        // 验证密码 (使用加密比对)
        if (!passwordEncoder.matches(password, user.getPassword())) {
            result.put("code", 400);
            result.put("message", "密码错误");
            return result;
        }
        
        // 生成JWT令牌
        String token = jwtTokenUtil.generateToken(user.getId());
        
        // 构建返回结果
        result.put("code", 200);
        result.put("message", "登录成功");
        result.put("token", token);
        
        // 返回用户信息(排除敏感字段)
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("phoneNumber", user.getPhoneNumber());
        userInfo.put("nickname", user.getNickname());
        result.put("userInfo", userInfo);
        
        return result;
    }

    public void registerUser(String email, String password) {
        // 对密码进行加密
        User user = new User();
        user.setPassword(passwordEncoder.encode(password));
        user.setPhoneNumber(email);
        user.setRegistrationTime(new Date());
        userMapper.registerUser(user);
    }

    public void register(User user) {
        // 对密码进行加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.registerUser(user);
    }

    public User findUserByPhoneNumber(String phoneNumber) {
        return userMapper.findByPhoneNumber(phoneNumber);
    }

    public String sendResetPasswordCode(String phoneNumber) {
        // 检查手机号是否存在
        if (!isUserExists(phoneNumber)) {
            return "手机号不存在";
        }

        // 生成验证码
        String verificationCode = generateVerificationCode();
        verificationCodes.put(phoneNumber, verificationCode);

        // 发送验证码邮件
        /* emailUtil.sendEmail("recipient-email@example.com", "密码重置验证码", "您的验证码是：" + verificationCode); */

        return "验证码已发送，请查收";
    }

    public String resetPassword(String phoneNumber, String newPassword, String confirmPassword, String verificationCode) {
        // 验证手机号和验证码
        if (!isUserExists(phoneNumber)) {
            return "手机号不存在";
        }

        if (!verificationCodes.get(phoneNumber).equals(verificationCode)) {
            return "验证码错误";
        }

        if (!newPassword.equals(confirmPassword)) {
            return "两次输入的密码不一致";
        }

        // 更新用户密码
        updatePassword(phoneNumber, newPassword);

        // 清除验证码
        verificationCodes.remove(phoneNumber);

        return "密码重置成功";
    }

    private boolean isUserExists(String phoneNumber) {
        // 实际的用户查询逻辑
        User user = userMapper.findByPhoneNumber(phoneNumber);
        return user != null;
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    private void updatePassword(String phoneNumber, String newPassword) {
        // 加密新密码
        String hashedPassword = passwordEncoder.encode(newPassword);
        // 更新数据库中的密码
        userMapper.updatePassword(phoneNumber, hashedPassword);
    }
}