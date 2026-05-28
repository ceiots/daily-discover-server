package com.dailydiscover.feed.application.service.impl;

import com.dailydiscover.feed.application.dto.HotProductQuery;
import com.dailydiscover.feed.application.service.HotProductApplicationService;
import com.dailydiscover.feed.domain.model.HotProduct;
import com.dailydiscover.feed.domain.repository.HotProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotProductApplicationServiceImpl implements HotProductApplicationService {

    private final HotProductRepository hotProductRepository;

    @Override
    public List<HotProduct> getHotProducts(HotProductQuery query) {
        try {
            if ("new".equals(query.getTab())) {
                return hotProductRepository.findNewArrivalProducts(query.getLimit(), query.getOffset());
            }
            return hotProductRepository.findTrendingProducts(query.getLimit(), query.getOffset());
        } catch (Exception e) {
            log.error("获取今日热点商品失败, tab: {}, page: {}", query.getTab(), query.getPage(), e);
            return List.of();
        }
    }

    @Override
    public long countProducts(HotProductQuery query) {
        try {
            if ("new".equals(query.getTab())) {
                return hotProductRepository.countNewArrivalProducts();
            }
            return hotProductRepository.countTrendingProducts();
        } catch (Exception e) {
            log.error("统计今日热点商品数量失败, tab: {}", query.getTab(), e);
            return 0;
        }
    }

    @Override
    public HotProduct getProductDetail(Long productId) {
        try {
            return hotProductRepository.findByProductId(productId);
        } catch (Exception e) {
            log.error("获取今日热点商品详情失败, productId: {}", productId, e);
            return null;
        }
    }
}