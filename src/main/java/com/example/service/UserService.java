package com.example.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.mapper.UserMapper;
import com.example.model.User;

@Service
public class UserService {

    private final Map<String, String> verificationCodes = new HashMap<>();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserMapper userMapper;

    public void registerUser(String email, String password) {
        // 对密码进行加密
        User user = new User();
        /*
         * user.setPassword(passwordEncoder.encode(password));
         * user.setEmail(email);
         * user.setRegistrationTime(new Date());
         * userMapper.insert(user);
         */
    }

    public User login(String phoneNumber, String password) {
        return userMapper.findByPhoneNumberAndPassword(phoneNumber, password);
    }

    public void register(User user) {
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
        emailUtil.sendEmail("recipient-email@example.com", "密码重置验证码", "您的验证码是：" + verificationCode);

        return "验证码已发送，请查收";
    }

    public String resetPassword(String phoneNumber, String newPassword, String confirmPassword,
            String verificationCode) {
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
        // 这里可以替换为实际的用户查询逻辑
        return "1234567890".equals(phoneNumber); // 示例数据
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    private void updatePassword(String phoneNumber, String newPassword) {
        // 这里可以替换为实际的更新密码逻辑
        System.out.println("更新密码：" + phoneNumber + " 新密码：" + newPassword);
        // 示例：加密新密码
        String hashedPassword = passwordEncoder.encode(newPassword);
        System.out.println("加密后的密码：" + hashedPassword);
    }

}