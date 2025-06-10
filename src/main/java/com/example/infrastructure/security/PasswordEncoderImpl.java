package com.example.infrastructure.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码加密实现类
 */
@Component
public class PasswordEncoderImpl implements com.example.domain.model.user.PasswordEncoder {
    
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    
    public PasswordEncoderImpl(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    
    @Override
    public String encode(String rawPassword) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }
    
    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
} 