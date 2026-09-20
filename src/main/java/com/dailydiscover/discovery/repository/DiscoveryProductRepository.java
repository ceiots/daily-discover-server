package com.dailydiscover.discovery.repository;

import com.dailydiscover.discovery.domain.DiscoveryProduct;
import com.dailydiscover.discovery.domain.DiscoveryProductId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscoveryProductRepository extends JpaRepository<DiscoveryProduct, DiscoveryProductId> {
    List<DiscoveryProduct> findByDiscoveryIdOrderBySortOrderAsc(Long discoveryId);
}
