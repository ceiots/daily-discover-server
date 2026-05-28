package com.dailydiscover.feed.infrastructure.persistence;

import com.dailydiscover.feed.domain.model.HotProduct;
import com.dailydiscover.feed.domain.repository.HotProductRepository;
import com.dailydiscover.feed.infrastructure.persistence.entity.HotProductReadModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class HotProductRepositoryImpl implements HotProductRepository {

    private final HotProductReadModelMapper mapper;

    @Override
    public List<HotProduct> findTrendingProducts(int limit, int offset) {
        return mapper.findTrendingProducts(limit, offset)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<HotProduct> findNewArrivalProducts(int limit, int offset) {
        return mapper.findNewArrivalProducts(limit, offset)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public HotProduct findByProductId(Long productId) {
        HotProductReadModel entity = mapper.findByProductId(productId);
        return entity != null ? toDomain(entity) : null;
    }

    @Override
    public long countTrendingProducts() {
        return mapper.countTrendingProducts();
    }

    @Override
    public long countNewArrivalProducts() {
        return mapper.countNewArrivalProducts();
    }

    private HotProduct toDomain(HotProductReadModel entity) {
        return HotProduct.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .title(entity.getTitle())
                .mainImageUrl(entity.getMainImageUrl())
                .minPrice(entity.getMinPrice())
                .goodsSlogan(entity.getGoodsSlogan())
                .recommendationReason(entity.getRecommendationReason())
                .categoryId(entity.getCategoryId())
                .brand(entity.getBrand())
                .salesCount(entity.getSalesCount() != null ? entity.getSalesCount() : 0)
                .viewCount(entity.getViewCount() != null ? entity.getViewCount() : 0)
                .favoriteCount(entity.getFavoriteCount() != null ? entity.getFavoriteCount() : 0)
                .salesGrowthRate(entity.getSalesGrowthRate())
                .hotScore(entity.getHotScore())
                .averageRating(entity.getAverageRating())
                .totalReviews(entity.getTotalReviews() != null ? entity.getTotalReviews() : 0)
                .positiveRate(entity.getPositiveRate())
                .isTrending(entity.getIsTrending() != null && entity.getIsTrending() == 1)
                .isNewArrival(entity.getIsNewArrival() != null && entity.getIsNewArrival() == 1)
                .hotTag(entity.getHotTag() != null ? entity.getHotTag() : "")
                .build();
    }
}