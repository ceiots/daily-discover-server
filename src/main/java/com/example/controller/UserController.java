package com.example.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.config.ImageConfig;
import com.example.dto.ResetPasswordRequest;
import com.example.model.User;
import com.example.service.UserService;
import com.example.util.JwtTokenUtil;

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
            return "手机号已被注册";
        }

        // 设置注册时间（可选，Service层已设置）
        user.setRegistrationTime(new Date());

        userService.register(user);
        return "注册成功";
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
        Map<String, Object> result = userService.sendResetPasswordCode(request.getPhoneNumber());
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        // Add null check for request body
        if (request == null) {
            return ResponseEntity.badRequest().body("请求体不能为空");
        }

        // Add parameter validation
        String message = userService.resetPassword(
                request.getPhoneNumber(),
                request.getNewPassword(),
                request.getConfirmPassword(),
                request.getVerificationCode());
        return ResponseEntity.ok().body(message);
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestParam Long userId) {
        User user = userService.findUserById(userId);
        if (user != null) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("nickname", user.getNickname());
            userInfo.put("phoneNumber", user.getPhoneNumber());
            userInfo.put("memberLevel", user.getMemberLevel() != null ? user.getMemberLevel() : "普通会员");

            // 处理头像路径
            String avatar = user.getAvatar();
            if (avatar != null && !avatar.startsWith("http")) {
                // 如果不是完整URL，可以在这里添加处理逻辑
                // 例如，可以添加相对路径前缀
                avatar = ImageConfig.getFullImageUrl(avatar);
            }
            userInfo.put("avatar", avatar);

            return ResponseEntity.ok(userInfo);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file, @RequestParam Long userId) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("上传的文件为空");
            }
    
            // 定义本地存储路径
            String uploadDir = "E:\\images\\avatar";
            Path uploadPath = Paths.get(uploadDir);
    
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
    
            // 生成唯一的文件名
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
    
            // 保存文件到本地
            Files.write(filePath, file.getBytes());
    
            // 构建完整的文件路径
            String avatarUrl = filePath.toString();
    
            // 调用 UserService 更新用户头像
            userService.updateUserAvatar(userId, avatarUrl);
    
            return ResponseEntity.ok().body(Collections.singletonMap("avatar", avatarUrl));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("头像上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        // 这里可以实现token失效逻辑
        return ResponseEntity.ok().body("退出成功");
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> requestBody, HttpServletRequest request) {
        try {
            String oldPassword = requestBody.get("oldPassword");
            String newPassword = requestBody.get("newPassword");
            String confirmPassword = requestBody.get("confirmPassword");
    
            if (oldPassword == null || newPassword == null || confirmPassword == null) {
                return ResponseEntity.badRequest().body("参数不能为空");
            }
    
            if (!newPassword.equals(confirmPassword)) {
                return ResponseEntity.badRequest().body("两次输入的密码不一致");
            }
    
            // 修改部分：从 request.getAttribute("userId") 获取 userId
            Long userId = (Long) request.getAttribute("userId");
            System.out.println("userId: " + userId); // 打印 userId
            if (userId == null) {
                return ResponseEntity.status(401).body("无效的用户 ID");
            }
    
            // 调用 service 层修改密码
            String result = userService.changePassword(userId, oldPassword, newPassword);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("修改密码失败：" + e.getMessage());
        }
    }
}