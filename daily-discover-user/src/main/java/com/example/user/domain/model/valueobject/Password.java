package com.example.user.domain.model.valueobject;

import com.example.common.exception.BusinessException;
import com.example.user.infrastructure.common.result.ResultCode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * 密码值对象
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final Pattern PASSWORD_PATTERN = 
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$");
    
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();
    
    private String encodedValue;
    
    private Password(String encodedPassword) {
        this.encodedValue = encodedPassword;
    }
    
    public static Password create(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new BusinessException(ResultCode.PASSWORD_EMPTY);
        }
        validate(rawPassword);
        return new Password(ENCODER.encode(rawPassword));
    }
    
    public static Password createWithoutValidation(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new BusinessException(ResultCode.PASSWORD_EMPTY);
        }
        return new Password(ENCODER.encode(rawPassword));
    }
    
    public static Password ofEncoded(String encodedPassword) {
        return new Password(encodedPassword);
    }
    
    public boolean matches(String rawPassword) {
        return ENCODER.matches(rawPassword, encodedValue);
    }
    
    public boolean matches(Password password) {
        return this.encodedValue.equals(password.encodedValue);
    }
    
    private static void validate(String password) {
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new BusinessException(ResultCode.PASSWORD_FORMAT_ERROR);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Password password = (Password) o;
        return encodedValue != null ? encodedValue.equals(password.encodedValue) : password.encodedValue == null;
    }
    
    @Override
    public int hashCode() {
        return encodedValue != null ? encodedValue.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "******";
    }
} 