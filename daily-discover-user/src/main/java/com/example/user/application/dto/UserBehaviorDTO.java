package com.example.user.application.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户行为DTO
 */
@Data
@Accessors(chain = true)
public class UserBehaviorDTO implements Serializable {
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
     * 行为类型:1-浏览,2-点赞,3-收藏,4-评论,5-分享,6-购买,7-搜索
     */
    private Integer behaviorType;

    /**
     * 目标ID
     */
    private Long targetId;

    /**
     * 目标类型:1-内容,2-商品,3-用户,4-话题,5-评论
     */
    private Integer targetType;

    /**
     * 行为时间
     */
    private LocalDateTime behaviorTime;

    /**
     * 设备类型:1-iOS,2-Android,3-H5,4-小程序,5-PC
     */
    private Integer deviceType;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}