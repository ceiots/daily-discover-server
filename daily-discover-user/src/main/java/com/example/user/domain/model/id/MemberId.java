package com.example.user.domain.model.id;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

/**
 * 会员ID值对象
 */
@Getter
@EqualsAndHashCode
public class MemberId implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会员ID值
     */
    private final Long value;

    /**
     * 构造函数
     *
     * @param value 会员ID值
     */
    public MemberId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("会员ID必须大于0");
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
} 