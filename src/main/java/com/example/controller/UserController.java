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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Value("${file.upload.base-url}")
    private String baseUrl;

    @Value("${default.avatar}")
    private String defaultAvatar;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


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
        user.setAvatar(defaultAvatar);
        user.setMemberLevel("普通会员");
        user.setIsOfficial(false);
        
        // 生成随机昵称，例如：User_13800138000_ABC123
        String randomSuffix = UUID.randomUUID().toString().substring(0, 6); // 截取前6位
        String nickname = "User_" + user.getPhoneNumber() + "_" + randomSuffix;
        user.setNickname(nickname);

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
            userInfo.put("isOfficial", user.getIsOfficial() != null ? user.getIsOfficial() : false);

            String avatar = user.getAvatar();
            
            userInfo.put("avatar", avatar);

            // 统一响应格式
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", userInfo);

            return ResponseEntity.ok(result);
        }
        // 用户不存在时也返回统一格式
        Map<String, Object> result = new HashMap<>();
        result.put("code", 404);
        result.put("message", "用户不存在");
        result.put("data", null);
        return ResponseEntity.status(404).body(result);
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
    
            // 拼接完整URL
            String avatarUrl = baseUrl + fileName;
    
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

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        boolean success = userService.updateUser(user);
        if (success) {
            return ResponseEntity.ok("资料更新成功");
        } else {
            return ResponseEntity.status(500).body("资料更新失败");
        }
    }

    // 支付密码相关接口
    @GetMapping("/payment-password/status")
    public ResponseEntity<?> getPaymentPasswordStatus(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 401);
            response.put("message", "未登录或登录已过期");
            return ResponseEntity.status(401).body(response);
        }

        try {
            User user = userService.findUserById(userId);
            boolean hasPaymentPassword = user != null && user.getPaymentPassword() != null;
            
            Map<String, Object> data = new HashMap<>();
            data.put("hasPaymentPassword", hasPaymentPassword);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取支付密码状态成功");
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取支付密码状态失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/payment-password/set")
    public ResponseEntity<?> setPaymentPassword(@RequestBody Map<String, String> requestBody, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 401);
            response.put("message", "未登录或登录已过期");
            return ResponseEntity.status(401).body(response);
        }

        String password = requestBody.get("password");
        if (password == null || password.length() != 6 || !password.matches("\\d{6}")) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", "支付密码必须为6位数字");
            return ResponseEntity.status(400).body(response);
        }

        try {
            User user = userService.findUserById(userId);
            if (user == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 404);
                response.put("message", "用户不存在");
                return ResponseEntity.status(404).body(response);
            }

            // 对支付密码进行加密处理，实际应用中应使用更安全的加密方式
            // 这里使用简单的加密方式示例
            String encryptedPassword = userService.encryptPassword(password);
            user.setPaymentPassword(encryptedPassword);
            
            boolean success = userService.updateUser(user);
            if (success) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 200);
                response.put("message", "设置支付密码成功");
                response.put("data", Collections.singletonMap("userId", userId));
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 500);
                response.put("message", "设置支付密码失败");
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "设置支付密码失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/payment-password/verify")
    public ResponseEntity<?> verifyPaymentPassword(@RequestBody Map<String, String> requestBody, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 401);
            response.put("message", "未登录或登录已过期");
            return ResponseEntity.status(401).body(response);
        }

        String password = requestBody.get("paymentPassword");
        if (password == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", "支付密码不能为空");
            return ResponseEntity.status(400).body(response);
        }

        try {
            User user = userService.findUserById(userId);
            if (user == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 404);
                response.put("message", "用户不存在");
                return ResponseEntity.status(404).body(response);
            }

            if (user.getPaymentPassword() == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "您尚未设置支付密码");
                return ResponseEntity.status(400).body(response);
            }

            // 验证支付密码
            String encryptedPassword = userService.encryptPassword(password);
            if (encryptedPassword.equals(user.getPaymentPassword())) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 200);
                response.put("message", "支付密码验证成功");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "支付密码错误");
                return ResponseEntity.status(400).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "验证支付密码失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/payment-password/update")
    public ResponseEntity<?> updatePaymentPassword(@RequestBody Map<String, String> requestBody, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 401);
            response.put("message", "未登录或登录已过期");
            return ResponseEntity.status(401).body(response);
        }

        String currentPassword = requestBody.get("currentPassword");
        String newPassword = requestBody.get("newPassword");
        String verificationCode = requestBody.get("verificationCode");

        if (newPassword == null || newPassword.length() != 6 || !newPassword.matches("\\d{6}")) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", "新支付密码必须为6位数字");
            return ResponseEntity.status(400).body(response);
        }

        try {
            User user = userService.findUserById(userId);
            if (user == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 404);
                response.put("message", "用户不存在");
                return ResponseEntity.status(404).body(response);
            }

            // 如果已有支付密码，需要验证当前密码
            if (user.getPaymentPassword() != null) {
                if (currentPassword == null) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("code", 400);
                    response.put("message", "当前支付密码不能为空");
                    return ResponseEntity.status(400).body(response);
                }

                String encryptedCurrentPassword = userService.encryptPassword(currentPassword);
                if (!encryptedCurrentPassword.equals(user.getPaymentPassword())) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("code", 400);
                    response.put("message", "当前支付密码错误");
                    return ResponseEntity.status(400).body(response);
                }
            }

            // 验证码校验逻辑 (简化示例，实际应该检查验证码的有效性)
            if (verificationCode == null || verificationCode.length() != 6) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "验证码错误");
                return ResponseEntity.status(400).body(response);
            }

            // 更新支付密码
            String encryptedNewPassword = userService.encryptPassword(newPassword);
            user.setPaymentPassword(encryptedNewPassword);
            
            boolean success = userService.updateUser(user);
            if (success) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 200);
                response.put("message", "更新支付密码成功");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 500);
                response.put("message", "更新支付密码失败");
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "更新支付密码失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/payment-password/send-code")
    public ResponseEntity<?> sendPaymentPasswordVerificationCode(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 401);
            response.put("message", "未登录或登录已过期");
            return ResponseEntity.status(401).body(response);
        }

        try {
            User user = userService.findUserById(userId);
            if (user == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 404);
                response.put("message", "用户不存在");
                return ResponseEntity.status(404).body(response);
            }

            // 模拟发送验证码
            // 实际应用中应调用短信服务发送验证码，并保存验证码和过期时间
            String verificationCode = String.format("%06d", (int)(Math.random() * 1000000));
            System.out.println("发送验证码到用户手机: " + user.getPhoneNumber() + ", 验证码: " + verificationCode);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "验证码发送成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "发送验证码失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}