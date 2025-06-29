package com.example.user.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员实体类
 */
@Data
@Accessors(chain = true)
@TableName("t_member")
public class MemberEntity {

    /**
     * 会员ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 会员等级ID
     */
    private Long levelId;

    /**
     * 会员等级名称
     */
    private String levelName;

    /**
     * 会员积分
     */
    private Integer points;
    
    /**
     * 已使用积分
     */
    private Integer usedPoints;
    
    /**
     * 会员等级
     */
    private Integer memberLevel;
    
    /**
     * 成长值
     */
    private Integer growthValue;
    
    /**
     * 是否永久会员
     */
    private Boolean isForever;
    
    /**
     * 会员开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 会员结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 免邮次数
     */
    private Integer freeShippingCount;
    
    /**
     * 免退次数
     */
    private Integer freeReturnCount;

    /**
     * 会员余额
     */
    private BigDecimal balance;

    /**
     * 会员消费总额
     */
    private BigDecimal totalAmount;

    /**
     * 会员状态（0-正常，1-冻结）
     */
    private Integer status;

    /**
     * 会员到期时间
     */
    private LocalDateTime expireTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    private Integer isDeleted;
} 