package com.dailydiscover.feed.domain.repository;

import com.dailydiscover.feed.domain.model.HotProduct;

import java.util.List;

public interface HotProductRepository {

    List<HotProduct> findTrendingProducts(int limit, int offset);

    List<HotProduct> findNewArrivalProducts(int limit, int offset);

    HotProduct findByProductId(Long productId);

    long countTrendingProducts();

    long countNewArrivalProducts();
}