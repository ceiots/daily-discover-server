package com.dailydiscover.behavior.domain;

import com.dailydiscover.content.domain.Content;
import com.dailydiscover.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "behavior", indexes = {
    @Index(name = "idx_behavior_user_id", columnList = "user_id"),
    @Index(name = "idx_behavior_content_id", columnList = "content_id"),
    @Index(name = "idx_behavior_type", columnList = "behavior_type"),
    @Index(name = "idx_behavior_created_at", columnList = "created_at"),
    @Index(name = "idx_behavior_user_content_type", columnList = "user_id, content_id, behavior_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Behavior {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Column(name = "behavior_type", nullable = false, length = 16)
    private String behaviorType;

    @Column(name = "extra_data", columnDefinition = "JSONB")
    private String extraData;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}