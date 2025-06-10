package com.example.user.domain.model.id;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

/**
 * 用户ID值对象
 */
@Getter
@EqualsAndHashCode
public class UserId implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID值
     */
    private final Long value;

    /**
     * 构造函数
     *
     * @param value 用户ID值
     */
    public UserId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("用户ID必须大于0");
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
} 