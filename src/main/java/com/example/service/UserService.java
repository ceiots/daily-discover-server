package com.example.service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mapper.UserMapper;
import com.example.model.User;
import com.example.util.JwtTokenUtil;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;

@Service
public class UserService {
    // 新增配置项
    //@Value("${aliyun.sms.accessKeyId}")
    private String accessKeyId;
    
    //@Value("${aliyun.sms.accessSecret}")
    private String accessSecret;
    
    @Value("${aliyun.sms.signName}")
    private String signName;
    
    @Value("${aliyun.sms.templateCode}")
    private String templateCode;

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
        System.out.println("token: " + token);
        
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
        // 设置注册时间为当前时间
        user.setRegistrationTime(new Date());
        userMapper.registerUser(user);
    }

    public User findUserByPhoneNumber(String phoneNumber) {
        return userMapper.findByPhoneNumber(phoneNumber);
    }

    public Map<String, Object> sendResetPasswordCode(String phoneNumber) {
        Map<String, Object> result = new HashMap<>();
        
        if (!isUserExists(phoneNumber)) {
            result.put("code", 400);
            result.put("message", "手机号不存在");
            return result;
        }
    
        String verificationCode = generateVerificationCode();
        verificationCodes.put(phoneNumber, verificationCode);
    
        // 模拟实际发送操作，正式环境应调用短信/邮件服务
        boolean sendSuccess = sendVerificationCode(phoneNumber, verificationCode);
        
        if (sendSuccess) {
            result.put("code", 200);
            result.put("message", "验证码已发送，请查收");
        } else {
            result.put("code", 500);
            result.put("message", "验证码发送失败，请重试");
        }
        return result;
    }
    
    // 新增模拟发送方法
    // 修改发送方法
    private boolean sendVerificationCode(String phoneNumber, String code) {
        try {
            DefaultProfile profile = DefaultProfile.getProfile(
                "cn-shenzhen",
                accessKeyId,
                accessSecret);
            IAcsClient client = new DefaultAcsClient(profile);

            SendSmsRequest request = new SendSmsRequest();
            request.setPhoneNumbers(phoneNumber);
            request.setSignName("阿里云短信测试");
            request.setTemplateCode(templateCode);
            request.setTemplateParam("{\"code\":\"" + code + "\"}");

            SendSmsResponse response = client.getAcsResponse(request);
           
            return "OK".equalsIgnoreCase(response.getCode());
        } catch (ClientException e) {
            System.out.println("短信发送失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
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
    
    /**
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return 用户对象，如果不存在则返回null
     */
    public User findUserById(Long userId) {
        return userMapper.findById(userId);
    }

    public void updateUserAvatar(Long userId, String avatarUrl) {
        userMapper.updateAvatar(userId, avatarUrl);
    }
    
    /**
     * 修改用户密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果信息
     */
    @Transactional
    public String changePassword(Long userId, String oldPassword, String newPassword) {
        // 这里可以添加旧密码验证逻辑，例如从数据库中查询用户的旧密码并比较
        // 假设已经验证通过
        int rows = userMapper.changePassword(userId, newPassword);
        if (rows > 0) {
            return "修改密码成功";
        } else {
            return "修改密码失败";
        }
    }

   
}