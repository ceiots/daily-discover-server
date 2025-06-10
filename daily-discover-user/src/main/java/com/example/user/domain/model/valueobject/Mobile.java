package com.example.user.domain.model.valueobject;

import com.example.common.exception.BusinessException;
import com.example.common.result.ResultCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * 手机号值对象
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mobile implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    
    private String value;
    
    private boolean verified;
    
    public Mobile(String mobile) {
        if (mobile != null && !mobile.isEmpty()) {
            validate(mobile);
            this.value = mobile;
            this.verified = false;
        }
    }
    
    public static Mobile of(String mobile) {
        return new Mobile(mobile);
    }
    
    public Mobile markVerified() {
        this.verified = true;
        return this;
    }
    
    private void validate(String mobile) {
        if (!MOBILE_PATTERN.matcher(mobile).matches()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "手机号格式不正确");
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mobile mobile = (Mobile) o;
        return value != null ? value.equals(mobile.value) : mobile.value == null;
    }
    
    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return value;
    }
} 