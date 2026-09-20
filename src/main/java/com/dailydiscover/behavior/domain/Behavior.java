package com.dailydiscover.behavior.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * 用户行为（behaviors 表，anonymous_id / discovery_id 只保存业务 ID，无外键）
 * 行为采用追加记录，不修改过去的行为（03 API-MVP.md 第 9/16 节）
 */
@Entity
@Table(name = "behaviors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Behavior {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "anonymous_id", nullable = false, length = 64)
    private String anonymousId;

    @Column(name = "discovery_id", nullable = false)
    private Long discoveryId;

    @Column(name = "behavior_type", nullable = false, length = 32)
    private String behaviorType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSONB")
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
