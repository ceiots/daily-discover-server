package com.dailydiscover.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户发布实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_posts")
public class UserPost {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 发布类型：review, question, answer
     */
    @TableField("post_type")
    private String postType;

    /**
     * 发布内容
     */
    @TableField("content")
    private String content;

    /**
     * 关联的商品ID（可为空）
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 关联的订单ID（可为空）
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 点赞数
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 评论数
     */
    @TableField("comment_count")
    private Integer commentCount;

    /**
     * 发布状态：published, draft, deleted
     */
    @TableField("status")
    private String status;

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
     * 发布类型枚举
     */
    public enum PostType {
        REVIEW("review"),
        QUESTION("question"),
        ANSWER("answer");

        private final String value;

        PostType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 发布状态枚举
     */
    public enum Status {
        PUBLISHED("published"),
        DRAFT("draft"),
        DELETED("deleted");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}