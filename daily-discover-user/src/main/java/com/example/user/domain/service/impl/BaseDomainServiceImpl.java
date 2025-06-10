package com.example.user.domain.service.impl;

import com.example.user.domain.service.BaseDomainService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 基础领域服务实现类
 */
@Service
public class BaseDomainServiceImpl implements BaseDomainService {

    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,16}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$");

    @Override
    public boolean verifyPassword(String plainPassword, String encodedPassword, String salt) {
        return PASSWORD_ENCODER.matches(plainPassword + salt, encodedPassword);
    }

    @Override
    public String encryptPassword(String plainPassword, String salt) {
        return PASSWORD_ENCODER.encode(plainPassword + salt);
    }

    @Override
    public String generateSalt() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    @Override
    public boolean isValidMobile(String mobile) {
        return mobile != null && MOBILE_PATTERN.matcher(mobile).matches();
    }

    @Override
    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    @Override
    public String generateVerifyCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    @Override
    public boolean validatePasswordStrength(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }
}