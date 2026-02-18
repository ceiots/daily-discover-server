package com.dailydiscover.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 帮助FAQ实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("help_faq")
public class HelpFaq {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 问题标题
     */
    @TableField("question")
    private String question;

    /**
     * 问题答案
     */
    @TableField("answer")
    private String answer;

    /**
     * FAQ类型：general, account, payment, order, delivery
     */
    @TableField("faq_type")
    private String faqType;

    /**
     * 排序权重
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 是否显示：true, false
     */
    @TableField("is_visible")
    private Boolean isVisible;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /**
     * FAQ类型枚举
     */
    public enum FaqType {
        GENERAL("general"),
        ACCOUNT("account"),
        PAYMENT("payment"),
        ORDER("order"),
        DELIVERY("delivery");

        private final String value;

        FaqType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}