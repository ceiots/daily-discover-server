package com.example.user.application.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户收藏DTO
 */
@Data
@Accessors(chain = true)
public class UserFavoriteDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 收藏类型:1-商品,2-店铺,3-内容,4-活动
     */
    private Integer type;

    /**
     * 收藏对象ID
     */
    private Long targetId;

    /**
     * 收藏时间
     */
    private LocalDateTime collectTime;

    /**
     * 备注
     */
    private String note;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}