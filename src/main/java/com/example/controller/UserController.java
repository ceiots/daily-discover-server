package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.ResetPasswordRequest;
import com.example.model.User;
import com.example.service.UserService;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Map<String, Object> result = userService.login(user.getPhoneNumber(), user.getPassword());
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        // 检查手机号是否已存在
        User existingUser = userService.findUserByPhoneNumber(user.getPhoneNumber());
        if (existingUser != null) {
            return "手机号已被注册"; // 返回失败消息
        }

        // 注册用户
        userService.register(user);
        return "注册成功"; // 返回成功消息
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String phoneNumber) {
        User user = userService.findUserByPhoneNumber(phoneNumber);
        if (user != null) {
            // 这里可以添加发送重置密码邮件或短信的逻辑
            return "重置密码链接已发送到您的手机";
        } else {
            return "手机号未注册";
        }
    }

    @PostMapping("/reset-password-code")
    public ResponseEntity<?> sendResetPasswordCode(@RequestBody ResetPasswordRequest request) {
        String message = userService.sendResetPasswordCode(request.getPhoneNumber());
        return ResponseEntity.ok().body(message);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        String message = userService.resetPassword(request.getPhoneNumber(), request.getNewPassword(),
                request.getConfirmPassword(), request.getVerificationCode());
        return ResponseEntity.ok().body(message);
    }

}