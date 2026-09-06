package com.dailydiscover.product.service;

import com.dailydiscover.product.domain.Product;
import com.dailydiscover.product.repository.ProductRepository;
import com.dailydiscover.common.exception.BusinessException;
import com.dailydiscover.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getActiveProducts() {
        return productRepository.findByStatusOrderByIdAsc("ACTIVE");
    }

    public Optional<Product> findActiveById(Long id) {
        return productRepository.findByIdAndStatus(id, "ACTIVE");
    }

    public List<Product> findActiveByIds(List<Long> ids) {
        return productRepository.findAllById(ids).stream()
                .filter(p -> "ACTIVE".equals(p.getStatus()))
                .toList();
    }

    @Transactional
    public Product createProduct(Product product) {
        product.setStatus("ACTIVE");
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product product) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setOriginalPrice(product.getOriginalPrice());
        existing.setCurrency(product.getCurrency());
        existing.setImageUrl(product.getImageUrl());
        existing.setProductUrl(product.getProductUrl());
        existing.setSource(product.getSource());
        existing.setSourceId(product.getSourceId());
        existing.setStatus(product.getStatus());
        return productRepository.save(existing);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}