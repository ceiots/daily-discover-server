package com.dailydiscover.discovery.service;

import com.dailydiscover.common.exception.BusinessException;
import com.dailydiscover.common.exception.ErrorCode;
import com.dailydiscover.discovery.domain.Discovery;
import com.dailydiscover.discovery.dto.DiscoveryDetailResponse;
import com.dailydiscover.discovery.repository.DiscoveryProductRepository;
import com.dailydiscover.discovery.repository.DiscoveryRepository;
import com.dailydiscover.product.domain.Product;
import com.dailydiscover.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscoveryService {

    private final DiscoveryRepository discoveryRepository;
    private final DiscoveryProductRepository discoveryProductRepository;
    private final ProductRepository productRepository;

    /**
     * 今日发现（03 API-MVP.md 第 12 节）
     * PUBLISHED + 时间过滤 + priority DESC，第一版不做个性化推荐
     */
    public List<Discovery> getTodayDiscoveries() {
        return discoveryRepository.findTodayDiscoveries(OffsetDateTime.now());
    }

    /**
     * 发现详情：id 有效 → 存在 → 允许展示，否则 404（03 API-MVP.md 第 14 节）
     */
    public Discovery getAvailableDiscovery(Long id) {
        Discovery discovery = discoveryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DISCOVERY_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
        if (!discovery.isAvailableNow()) {
            throw new BusinessException(ErrorCode.DISCOVERY_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        return discovery;
    }

    /**
     * 详情组装：discoveries + products + discovery_products（03 API-MVP.md 第 13 节）
     * 客户端不负责自己拼接 Discovery 与 Product 之间的关系
     */
    public DiscoveryDetailResponse buildDetail(Discovery discovery) {
        List<Product> products = discoveryProductRepository
                .findByDiscoveryIdOrderBySortOrderAsc(discovery.getId()).stream()
                .map(dp -> productRepository.findById(dp.getProductId()))
                .flatMap(java.util.Optional::stream)
                .filter(p -> "ACTIVE".equals(p.getStatus()))
                .toList();

        List<DiscoveryDetailResponse.ProductItem> productItems = products.stream()
                .map(p -> DiscoveryDetailResponse.ProductItem.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .description(p.getDescription())
                        .price(p.getPrice())
                        .imageUrl(p.getImageUrl())
                        .purchaseUrl(p.getPurchaseUrl())
                        .platform(p.getPlatform())
                        .build())
                .toList();

        return DiscoveryDetailResponse.builder()
                .id(discovery.getId())
                .name(discovery.getName())
                .scene(discovery.getScene())
                .reason(discovery.getReason())
                .suitableFor(discovery.getSuitableFor())
                .price(discovery.getPrice())
                .coverUrl(discovery.getCoverUrl())
                .actionTitle(discovery.getActionTitle())
                .actionUrl(discovery.getActionUrl())
                .products(productItems)
                .build();
    }
}
