package com.dailydiscover.discovery.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * discovery_products 复合主键
 */
public class DiscoveryProductId implements Serializable {

    private Long discoveryId;
    private Long productId;

    public DiscoveryProductId() {
    }

    public DiscoveryProductId(Long discoveryId, Long productId) {
        this.discoveryId = discoveryId;
        this.productId = productId;
    }

    public Long getDiscoveryId() { return discoveryId; }
    public void setDiscoveryId(Long discoveryId) { this.discoveryId = discoveryId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscoveryProductId that = (DiscoveryProductId) o;
        return Objects.equals(discoveryId, that.discoveryId)
                && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(discoveryId, productId);
    }
}
