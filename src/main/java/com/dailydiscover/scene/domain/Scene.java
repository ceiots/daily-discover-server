package com.dailydiscover.scene.domain;

import com.dailydiscover.content.domain.Content;
import com.dailydiscover.product.domain.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scene")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Scene {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "cover_image_url", length = 512)
    private String coverImageUrl;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(length = 16, nullable = false)
    private String status = "ACTIVE";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "scenes")
    @Builder.Default
    private List<Content> contents = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "scene_product",
        joinColumns = @JoinColumn(name = "scene_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    @Builder.Default
    private List<Product> products = new ArrayList<>();
}