package com.example.user.domain.model.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 会员等级实体
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberLevel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;
    
    /**
     * 设置ID
     *
     * @param id ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 等级
     */
    private Integer level;

    /**
     * 等级名称
     */
    private String name;

    /**
     * 等级图标
     */
    private String icon;

    /**
     * 成长值下限
     */
    private Integer growthMin;

    /**
     * 成长值上限
     */
    private Integer growthMax;

    /**
     * 折扣
     */
    private BigDecimal discount;

    /**
     * 描述
     */
    private String description;

    /**
     * 会员权益
     */
    private String benefits;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 是否包邮：0-否，1-是
     */
    private Boolean freeShipping;

    /**
     * 备注
     */
    private String note;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建会员等级
     */
    public static MemberLevel create(Integer level, String name, Integer growthMin, Integer growthMax) {
        MemberLevel memberLevel = new MemberLevel();
        memberLevel.level = level;
        memberLevel.name = name;
        memberLevel.growthMin = growthMin;
        memberLevel.growthMax = growthMax;
        memberLevel.discount = BigDecimal.ONE;
        memberLevel.status = 1;
        memberLevel.freeShipping = false;
        memberLevel.createTime = LocalDateTime.now();
        memberLevel.updateTime = LocalDateTime.now();
        return memberLevel;
    }

    /**
     * 更新会员等级信息
     */
    public void update(String name, String icon, BigDecimal discount, String description, 
                       String benefits, Boolean freeShipping) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
        if (icon != null) {
            this.icon = icon;
        }
        if (discount != null) {
            this.discount = discount;
        }
        if (description != null) {
            this.description = description;
        }
        if (benefits != null) {
            this.benefits = benefits;
        }
        if (freeShipping != null) {
            this.freeShipping = freeShipping;
        }
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新成长值范围
     */
    public void updateGrowthRange(Integer growthMin, Integer growthMax) {
        if (growthMin != null) {
            this.growthMin = growthMin;
        }
        if (growthMax != null) {
            this.growthMax = growthMax;
        }
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 禁用
     */
    public void disable() {
        this.status = 0;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 启用
     */
    public void enable() {
        this.status = 1;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 是否在成长值范围内
     */
    public boolean isInGrowthRange(Integer growthValue) {
        return growthValue >= growthMin && (growthMax == null || growthValue <= growthMax);
    }

    /**
     * 是否启用
     */
    public boolean isEnabled() {
        return this.status != null && this.status == 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberLevel that = (MemberLevel) o;
        return Objects.equals(id, that.id) || Objects.equals(level, that.level);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, level);
    }
} 