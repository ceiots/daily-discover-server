package com.dailydiscover.product.repository;

import com.dailydiscover.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndStatus(Long id, String status);

    @Query("SELECT p FROM Product p WHERE p.status = :status ORDER BY p.id ASC")
    List<Product> findByStatus(@Param("status") String status);

    List<Product> findByStatusOrderByIdAsc(String status);

    Optional<Product> findBySourceAndSourceIdAndStatus(String source, String sourceId, String status);
}