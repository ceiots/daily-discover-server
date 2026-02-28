package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ProductMapper;
import com.dailydiscover.mapper.ProductRecommendationMapper;
import com.dailydiscover.model.ProductRecommendation;
import com.dailydiscover.model.dto.ProductBasicInfoDTO;
import com.dailydiscover.model.dto.RelatedProductDTO;
import com.dailydiscover.service.ProductRecommendationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductRecommendationServiceImpl extends ServiceImpl<ProductRecommendationMapper, ProductRecommendation> implements ProductRecommendationService {
    
    @Autowired
    private ProductRecommendationMapper productRecommendationMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    public List<ProductRecommendation> getRecommendationsByProductId(Long productId) {
        try {
            // 使用修复后的Mapper方法查询相关推荐
            return productRecommendationMapper.findRelatedProductsByProductId(productId, 10);
        } catch (Exception e) {
            log.error("获取商品推荐失败，productId: {}", productId, e);
            return List.of();
        }
    }
    
    @Override
    public List<ProductRecommendation> getPersonalizedRecommendations(Long userId) {
        try {
            // 使用修复后的Mapper方法查询个性化推荐
            return productRecommendationMapper.findByUserId(userId);
        } catch (Exception e) {
            log.error("获取个性化推荐失败，userId: {}", userId, e);
            return List.of();
        }
    }
    
    @Override
    public List<ProductRecommendation> getRecommendationsByType(String recommendationType) {
        try {
            // 使用修复后的Mapper方法查询特定类型推荐
            return productRecommendationMapper.findByTypeWithLimit(recommendationType, 20);
        } catch (Exception e) {
            log.error("获取类型推荐失败，type: {}", recommendationType, e);
            return List.of();
        }
    }
    
    @Override
    public List<ProductRecommendation> getDailyDiscoverRecommendations(Long userId) {
        try {
            List<Map<String, Object>> resultMaps = productRecommendationMapper.findDailyDiscoverProducts(userId, 10);
            return convertMapListToProductRecommendations(resultMaps);
        } catch (Exception e) {
            log.error("获取每日发现推荐失败，userId: {}", userId, e);
            return List.of();
        }
    }
    
    @Override
    public List<ProductRecommendation> getSimilarRecommendations(Long productId) {
        try {
            return productRecommendationMapper.findByProductIdAndType(productId, "similar");
        } catch (Exception e) {
            log.error("获取相似商品推荐失败，productId: {}", productId, e);
            return List.of();
        }
    }
    
    @Override
    public List<ProductRecommendation> getComplementaryRecommendations(Long productId) {
        try {
            return productRecommendationMapper.findByProductIdAndType(productId, "complementary");
        } catch (Exception e) {
            log.error("获取搭配商品推荐失败，productId: {}", productId, e);
            return List.of();
        }
    }
    
    @Override
    public List<ProductRecommendation> getGeneralRecommendations(int limit) {
        try {
            return productRecommendationMapper.findGeneralRecommendations(limit);
        } catch (Exception e) {
            log.error("获取通用推荐失败", e);
            return List.of();
        }
    }
    
    @Override
    public List<RelatedProductDTO> getProductDetailRecommendations(Long productId, Double currentPrice, Integer limit) {
        try {
            // 1. 并行获取三路推荐数据
            List<Map<String, Object>> similarProducts = productRecommendationMapper.findSimilarProducts(productId, 4);
            List<Map<String, Object>> complementaryProducts = productRecommendationMapper.findComplementaryProducts(productId, 3);
            
            // 价格敏感推荐（基于当前价格）
            List<Map<String, Object>> priceSensitiveProducts = currentPrice != null ? 
                productRecommendationMapper.findPriceSensitiveProducts(productId, currentPrice, 3) : 
                new ArrayList<>();
            
            // 2. 计算优先级和相关性分数
            List<Map<String, Object>> allRecommendations = new ArrayList<>();
            
            // 相似商品推荐（优先级1.0）
            for (Map<String, Object> product : similarProducts) {
                product.put("priority", 1.0);
                product.put("relevance_score", product.get("recommendation_score"));
                product.put("recommendation_type", "similar");
                allRecommendations.add(product);
            }
            
            // 搭配商品推荐（优先级0.9）
            for (Map<String, Object> product : complementaryProducts) {
                product.put("priority", 0.9);
                product.put("relevance_score", product.get("relationship_strength"));
                product.put("recommendation_type", "complementary");
                allRecommendations.add(product);
            }
            
            // 价格敏感推荐（优先级0.8）
            for (Map<String, Object> product : priceSensitiveProducts) {
                product.put("priority", 0.8);
                // 价格匹配度计算：越接近当前价格，分数越高
                Double productPrice = (Double) product.get("max_price");
                Double priceMatch = currentPrice != null ? 1 - Math.abs(productPrice - currentPrice) / currentPrice : 0.5;
                product.put("relevance_score", priceMatch);
                product.put("recommendation_type", "price_sensitive");
                allRecommendations.add(product);
            }
            
            // 3. 去重和排序
            // 基于商品ID去重，保留优先级更高的推荐
            Map<Long, Map<String, Object>> uniqueRecommendations = new HashMap<>();
            for (Map<String, Object> recommendation : allRecommendations) {
                Long recommendedProductId = (Long) recommendation.get("recommended_product_id");
                Map<String, Object> existing = uniqueRecommendations.get(recommendedProductId);
                
                if (existing == null) {
                    uniqueRecommendations.put(recommendedProductId, recommendation);
                } else {
                    // 如果已存在，保留优先级更高的推荐
                    Double existingPriority = (Double) existing.get("priority");
                    Double currentPriority = (Double) recommendation.get("priority");
                    if (currentPriority > existingPriority) {
                        uniqueRecommendations.put(recommendedProductId, recommendation);
                    }
                }
            }
            
            // 4. 最终排序：先按优先级降序，再按相关性分数降序
            List<Map<String, Object>> finalRecommendations = new ArrayList<>(uniqueRecommendations.values());
            finalRecommendations.sort((a, b) -> {
                // 安全转换优先级，处理 BigDecimal 和 Double 类型
                Double priorityA = convertToDouble(a.get("priority"));
                Double priorityB = convertToDouble(b.get("priority"));
                if (!priorityA.equals(priorityB)) {
                    return Double.compare(priorityB, priorityA); // 优先级降序
                }
                
                // 安全转换相关性分数，处理 BigDecimal 和 Double 类型
                Double scoreA = convertToDouble(a.get("relevance_score"));
                Double scoreB = convertToDouble(b.get("relevance_score"));
                return Double.compare(scoreB, scoreA); // 相关性分数降序
            });
            
            // 5. 提取推荐商品ID列表
            List<Long> recommendedProductIds = finalRecommendations.stream()
                .map(recommendation -> (Long) recommendation.get("recommended_product_id"))
                .collect(Collectors.toList());
            
            if (recommendedProductIds.isEmpty()) {
                return new ArrayList<>();
            }
            
            // 6. 查询推荐商品的详细信息
            List<ProductBasicInfoDTO> productDetails = productMapper.findBasicInfoByIds(recommendedProductIds);
            
            // 7. 在代码中进行排序，保持推荐顺序
            Map<Long, ProductBasicInfoDTO> productMap = productDetails.stream()
                .collect(Collectors.toMap(ProductBasicInfoDTO::getId, Function.identity()));
            
            List<ProductBasicInfoDTO> sortedProducts = recommendedProductIds.stream()
                .map(productMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            
            // 8. 转换为RelatedProductDTO对象
            List<RelatedProductDTO> result = new ArrayList<>();
            for (ProductBasicInfoDTO productDetail : sortedProducts) {
                RelatedProductDTO dto = new RelatedProductDTO();
                
                // 设置商品基本信息
                dto.setId(productDetail.getId().toString());
                dto.setName(productDetail.getTitle());
                dto.setImage(productDetail.getMainImageUrl());
                dto.setPrice(productDetail.getMaxPrice());
                dto.setOriginalPrice(productDetail.getMinPrice());
                dto.setDiscount(productDetail.getDiscount());
                dto.setRating(productDetail.getAverageRating());
                dto.setSales(productDetail.getSalesCount());
                dto.setCategory(productDetail.getCategoryId().toString());
                dto.setReviewCount(productDetail.getTotalReviews());
                
                // 设置推荐相关信息
                // 找到对应的推荐信息
                Long productIdFromDetail = productDetail.getId();
                Map<String, Object> recommendationInfo = uniqueRecommendations.get(productIdFromDetail);
                if (recommendationInfo != null) {
                    dto.setRecommendationType((String) recommendationInfo.get("recommendation_type"));
                    Object score = recommendationInfo.get("relevance_score");
                    if (score != null) {
                        if (score instanceof Double) {
                            dto.setSimilarity(BigDecimal.valueOf((Double) score));
                        } else if (score instanceof BigDecimal) {
                            dto.setSimilarity((BigDecimal) score);
                        }
                    }
                    dto.setPriority(((Double) recommendationInfo.get("priority")).intValue());
                }
                
                result.add(dto);
            }
            
            // 最终展示指定数量的推荐项
            return result.stream().limit(limit != null ? limit : 10).collect(Collectors.toList());
            
        } catch (Exception e) {
            log.error("获取商品详情页推荐失败，productId: {}", productId, e);
            return List.of();
        }
    }
    

    
    private List<ProductRecommendation> convertMapListToProductRecommendations(List<Map<String, Object>> mapList) {
        List<ProductRecommendation> result = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            ProductRecommendation recommendation = new ProductRecommendation();
            
            // 设置推荐商品ID
            Object itemId = map.get("item_id");
            if (itemId != null) {
                recommendation.setRecommendedProductId(((Number) itemId).longValue());
            }
            
            // 设置推荐分数
            Object score = map.get("recommendation_score");
            if (score != null) {
                if (score instanceof Double) {
                    recommendation.setRecommendationScore(BigDecimal.valueOf((Double) score));
                } else if (score instanceof BigDecimal) {
                    recommendation.setRecommendationScore((BigDecimal) score);
                }
            }
            
            recommendation.setIsActive(true);
            result.add(recommendation);
        }
        return result;
    }
    
    /**
     * 安全转换对象为 Double 类型
     * 支持 BigDecimal、Double、Integer、Long、Float 等数字类型
     */
    private Double convertToDouble(Object value) {
        if (value == null) {
            return 0.0;
        }
        
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal) value).doubleValue();
        } else if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else if (value instanceof Long) {
            return ((Long) value).doubleValue();
        } else if (value instanceof Float) {
            return ((Float) value).doubleValue();
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        } else {
            // 对于其他类型，尝试转换为字符串再解析
            try {
                return Double.parseDouble(value.toString());
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
    }

    // ==================== 首页推荐四模块 ====================

    @Override
    public List<Map<String, Object>> getDailyDiscoveryRecommendations(Long userId) {
        try {
            List<Map<String, Object>> recommendations = productRecommendationMapper.findDailyDiscoverProducts(userId, 10);
            
            // 后端去重逻辑：基于 item_id 去重，保留第一个出现的
            return recommendations.stream()
                .filter(item -> item.get("item_id") != null)
                .collect(Collectors.toMap(
                    item -> item.get("item_id"), // key: item_id
                    item -> item,                 // value: item本身
                    (existing, replacement) -> existing // 重复时保留第一个
                ))
                .values()
                .stream()
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取今日发现推荐失败，userId: {}", userId, e);
            return List.of();
        }
    }

    @Override
    public List<Map<String, Object>> getLifeScenarioRecommendations(Long userId, String timeContext, String locationContext) {
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            
            // 解析locationContext获取locationKey
            String locationKey = extractLocationKey(locationContext);
            
            // 1. 先查询用户专属推荐（最多2条）
            if (userId != null) {
                List<Map<String, Object>> userRecommendations = productRecommendationMapper.findUserLifeScenarioRecommendations(userId, timeContext, locationKey);
                result.addAll(userRecommendations);
            }
            
            // 2. 如果用户专属推荐不足2条，补充通用推荐
            if (result.size() < 2) {
                List<Map<String, Object>> generalRecommendations = productRecommendationMapper.findGeneralLifeScenarioRecommendations(timeContext, locationKey);
                
                // 只补充到总共2条
                int remaining = 2 - result.size();
                if (remaining > 0 && generalRecommendations.size() > 0) {
                    result.addAll(generalRecommendations.subList(0, Math.min(remaining, generalRecommendations.size())));
                }
            }
            
            // 3. 确保返回最多2条记录
            return result.size() > 2 ? result.subList(0, 2) : result;
        } catch (Exception e) {
            log.error("获取生活场景推荐失败，userId: {}, timeContext: {}, locationContext: {}", userId, timeContext, locationContext, e);
            return List.of();
        }
    }
    
    /**
     * 获取locationKey（现在locationContext已经是简单字符串）
     */
    private String extractLocationKey(String locationContext) {
        if (locationContext == null || locationContext.trim().isEmpty()) {
            return "home"; // 默认值
        }
        return locationContext.trim(); // 直接返回字符串
    }

    @Override
    public List<Map<String, Object>> getCommunityHotList() {
        try {
            return productRecommendationMapper.findCommunityHotList();
        } catch (Exception e) {
            log.error("获取社区热榜推荐失败", e);
            return List.of();
        }
    }

    @Override
    public List<Map<String, Object>> getPersonalizedDiscoveryStream(Long userId) {
        try {
            List<Map<String, Object>> recommendations = productRecommendationMapper.findPersonalizedDiscoveryStream(userId);
            
            // 后端去重逻辑：基于 item_id 去重，保留第一个出现的
            return recommendations.stream()
                .filter(item -> item.get("item_id") != null)
                .collect(Collectors.toMap(
                    item -> item.get("item_id"), // key: item_id
                    item -> item,                 // value: item本身
                    (existing, replacement) -> existing // 重复时保留第一个
                ))
                .values()
                .stream()
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取个性化发现流推荐失败，userId: {}", userId, e);
            return List.of();
        }
    }
}