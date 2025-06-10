package com.example.user.domain.model.id;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户ID标识符
 */
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserId implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long value;
    
    private UserId(Long id) {
        this.value = id;
    }
    
    public static UserId of(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("用户ID必须大于0");
        }
        return new UserId(id);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
} 