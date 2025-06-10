package com.example.user.application.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户关系DTO
 */
@Data
@Accessors(chain = true)
public class UserRelationshipDTO implements Serializable {
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
     * 关联用户ID
     */
    private Long relatedUserId;

    /**
     * 关系类型:1-好友,2-黑名单,3-特别关注
     */
    private Integer relationType;

    /**
     * 状态:0-解除,1-有效
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}