package com.dailydiscover.discovery.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * 发现与商品关系（discovery_products 表，复合主键，无外键）
 */
@Entity
@Table(name = "discovery_products")
@IdClass(DiscoveryProductId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscoveryProduct {

    @Id
    @Column(name = "discovery_id")
    private Long discoveryId;

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
}
