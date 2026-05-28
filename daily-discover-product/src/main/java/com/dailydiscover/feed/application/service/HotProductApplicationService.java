package com.dailydiscover.feed.application.service;

import com.dailydiscover.feed.domain.model.HotProduct;
import com.dailydiscover.feed.application.dto.HotProductQuery;

import java.util.List;

public interface HotProductApplicationService {

    List<HotProduct> getHotProducts(HotProductQuery query);

    long countProducts(HotProductQuery query);

    HotProduct getProductDetail(Long productId);
}