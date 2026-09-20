package com.dailydiscover.discovery.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * 每日发现（discoveries 表，MVP 最核心数据）
 */
@Entity
@Table(name = "discoveries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Discovery {

    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_PUBLISHED = "PUBLISHED";
    public static final String STATUS_OFFLINE = "OFFLINE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String scene;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "suitable_for", columnDefinition = "TEXT")
    private String suitableFor;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "cover_url", columnDefinition = "TEXT")
    private String coverUrl;

    @Column(name = "action_title", length = 100)
    private String actionTitle;

    @Column(name = "action_url", columnDefinition = "TEXT")
    private String actionUrl;

    @Column(nullable = false, length = 32)
    private String status;

    @Column(nullable = false)
    private Integer priority;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * 是否允许展示：PUBLISHED + 已到发布时间 + 未过期（01 文档第 16 节）
     */
    public boolean isAvailableNow() {
        OffsetDateTime now = OffsetDateTime.now();
        return STATUS_PUBLISHED.equals(status)
                && publishedAt != null && !publishedAt.isAfter(now)
                && (expiresAt == null || expiresAt.isAfter(now));
    }
}
