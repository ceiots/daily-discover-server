package com.dailydiscover.product.domain;

import com.dailydiscover.scene.domain.Scene;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @Column(length = 3, nullable = false)
    private String currency = "CNY";

    @Column(name = "image_url", length = 512)
    private String imageUrl;

    @Column(name = "product_url", length = 512)
    private String productUrl;

    @Column(length = 32)
    private String source;

    @Column(name = "source_id", length = 128)
    private String sourceId;

    @Column(length = 16, nullable = false)
    private String status = "ACTIVE";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "products")
    @Builder.Default
    private List<Scene> scenes = new ArrayList<>();
}