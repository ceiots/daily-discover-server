package com.dailydiscover.content.domain;

import com.dailydiscover.scene.domain.Scene;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "content")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(name = "cover_image_url", length = 512)
    private String coverImageUrl;

    @Column(name = "content_type", length = 32, nullable = false)
    private String contentType = "ARTICLE";

    @Column(length = 16, nullable = false)
    private String status = "PUBLISHED";

    @Column(nullable = false)
    private Integer priority = 0;

    @Column(name = "publish_at", nullable = false)
    private LocalDateTime publishAt = LocalDateTime.now();

    @Column(name = "expire_at")
    private LocalDateTime expireAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(
        name = "content_scene",
        joinColumns = @JoinColumn(name = "content_id"),
        inverseJoinColumns = @JoinColumn(name = "scene_id")
    )
    @Builder.Default
    private List<Scene> scenes = new ArrayList<>();

    // 判断内容是否可展示
    public boolean isPublished() {
        return "PUBLISHED".equals(status) 
            && publishAt != null 
            && !publishAt.isAfter(LocalDateTime.now())
            && (expireAt == null || !expireAt.isBefore(LocalDateTime.now()));
    }
}