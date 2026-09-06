package com.dailydiscover.feedback.domain;

import com.dailydiscover.content.domain.Content;
import com.dailydiscover.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedback", indexes = {
    @Index(name = "idx_feedback_user_id", columnList = "user_id"),
    @Index(name = "idx_feedback_content_id", columnList = "content_id"),
    @Index(name = "idx_feedback_type", columnList = "feedback_type"),
    @Index(name = "idx_feedback_created_at", columnList = "created_at"),
    @Index(name = "idx_feedback_user_content_type", columnList = "user_id, content_id, feedback_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Column(name = "feedback_type", nullable = false, length = 16)
    private String feedbackType;

    @Column(length = 255)
    private String reason;

    @Column(name = "extra_data", columnDefinition = "JSONB")
    private String extraData;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}