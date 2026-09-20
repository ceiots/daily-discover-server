package com.dailydiscover.product.repository;

import com.dailydiscover.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 查询一条发现包含的商品，按 sort_order 排序，只返回 ACTIVE 商品
     */
    @Query(value = "SELECT p FROM Product p WHERE p.id IN " +
            "(SELECT dp.productId FROM com.dailydiscover.discovery.domain.DiscoveryProduct dp " +
            "WHERE dp.discoveryId = :discoveryId) AND p.status = 'ACTIVE'")
    List<Product> findActiveProductsByDiscoveryId(@Param("discoveryId") Long discoveryId);
}
