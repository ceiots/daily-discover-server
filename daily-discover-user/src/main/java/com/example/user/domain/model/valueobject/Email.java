package com.example.user.domain.model.valueobject;

import com.example.common.exception.BusinessException;
import com.example.common.result.ResultCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * 邮箱值对象
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Email implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    
    private String value;
    
    private boolean verified;
    
    public Email(String email) {
        if (email != null && !email.isEmpty()) {
            validate(email);
            this.value = email;
            this.verified = false;
        }
    }
    
    public static Email of(String email) {
        return new Email(email);
    }
    
    public Email markVerified() {
        this.verified = true;
        return this;
    }
    
    private void validate(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "邮箱格式不正确");
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return value != null ? value.equalsIgnoreCase(email.value) : email.value == null;
    }
    
    @Override
    public int hashCode() {
        return value != null ? value.toLowerCase().hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return value;
    }
} 