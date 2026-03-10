package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ProductMapper;
import com.dailydiscover.mapper.ProductRecommendationMapper;
import com.dailydiscover.model.ProductRecommendation;
import com.dailydiscover.dto.RelatedProductDTO;
import com.dailydiscover.dto.DailyDiscoveryResponseDTO;
import com.dailydiscover.dto.LifeScenarioResponseDTO;
import com.dailydiscover.dto.CommunityHotListResponseDTO;
import com.dailydiscover.dto.PersonalizedDiscoveryResponseDTO;
import com.dailydiscover.dto.ProductBasicInfoDTO;
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
            // 优先使用个性化推荐，如果没有则使用每日发现推荐
            List<ProductRecommendation> personalizedRecommendations = productRecommendationMapper.findByUserId(userId);
            
            if (!personalizedRecommendations.isEmpty()) {
                return personalizedRecommendations;
            }
            
            // 如果没有个性化推荐，则使用每日发现推荐（第一页，10条数据）
            List<Map<String, Object>> resultMaps = productRecommendationMapper.findDailyDiscoverProducts(userId, 10, 0);
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
    public List<DailyDiscoveryResponseDTO> getDailyDiscoveryRecommendations(Long userId, Integer limit, Integer page) {
        try {
            // 设置默认值
            int finalLimit = limit != null ? limit : 20;
            int finalPage = page != null ? page : 1;
            int offset = (finalPage - 1) * finalLimit;
            
            // 第一步：查询推荐的商品ID集合（简单查询）
            List<Map<String, Object>> productIdMaps = productRecommendationMapper.findDailyDiscoverProductIds(userId, finalLimit * 2, offset);
            
            if (productIdMaps.isEmpty()) {
                return List.of();
            }
            
            // 提取商品ID列表
            List<Long> productIds = productIdMaps.stream()
                .map(map -> (Long) map.get("item_id"))
                .collect(Collectors.toList());
            
            // 第二步：根据商品ID集合查询完整商品信息（使用ProductMapper的专用方法）
            List<Map<String, Object>> productsInfo = productMapper.findProductsInfoForRecommendation(productIds);
            
            // 合并推荐分数到商品信息中
            Map<Long, Double> recommendationScores = productIdMaps.stream()
                .collect(Collectors.toMap(
                    map -> (Long) map.get("item_id"),
                    map -> getDoubleValue(map.get("recommendation_score"))
                ));
            
            List<Map<String, Object>> mergedRecommendations = new ArrayList<>();
            for (Map<String, Object> productInfo : productsInfo) {
                Long productId = (Long) productInfo.get("item_id");
                Double recommendationScore = recommendationScores.get(productId);
                
                if (recommendationScore != null) {
                    Map<String, Object> merged = new HashMap<>(productInfo);
                    merged.put("recommendation_score", recommendationScore);
                    mergedRecommendations.add(merged);
                }
            }
            
            // Java端智能排序
            List<Map<String, Object>> sortedRecommendations = sortRecommendationsByIntelligentAlgorithm(mergedRecommendations);
            
            // 转换为DTO并限制数量
            return sortedRecommendations.stream()
                .map(this::convertToDailyDiscoveryResponseDTO)
                .limit(finalLimit)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取今日发现推荐失败，userId: {}, page: {}", userId, page, e);
            return List.of();
        }
    }

    // 获取双精度值
    private Double getDoubleValue(Object value) {
        if (value == null) return null;
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Float) {
            return ((Float) value).doubleValue();
        } else if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else if (value instanceof Long) {
            return ((Long) value).doubleValue();
        } else {
            try {
                return Double.parseDouble(value.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    // 获取整数值
    private Integer getIntegerValue(Object value) {
        if (value == null) return null;
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Long) {
            return ((Long) value).intValue();
        } else if (value instanceof Double) {
            return ((Double) value).intValue();
        } else {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    @Override
    public List<LifeScenarioResponseDTO> getLifeScenarioRecommendations(Long userId, String dateTime) {
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            
            // 解析日期时间，计算时间、日期、季节维度
            ScenarioDimensions dimensions = parseDateTimeToDimensions(dateTime);
            
            // 1. 先查询用户专属推荐（最多4条）
            if (userId != null) {
                List<Map<String, Object>> userRecommendations = productRecommendationMapper.findUserLifeScenarioRecommendations(userId, dimensions.timePeriod, dimensions.dayType, dimensions.seasonType);
                result.addAll(userRecommendations);
            }
            
            // 2. 如果用户专属推荐不足4条，补充通用推荐
            if (result.size() < 4) {
                List<Map<String, Object>> generalRecommendations = productRecommendationMapper.findGeneralLifeScenarioRecommendations(dimensions.timePeriod, dimensions.dayType, dimensions.seasonType);
                
                // 只补充到总共4条
                int remaining = 4 - result.size();
                if (remaining > 0 && generalRecommendations.size() > 0) {
                    result.addAll(generalRecommendations.subList(0, Math.min(remaining, generalRecommendations.size())));
                }
            }
            
            // 3. 解析推荐商品ID并查询完整商品信息（全局去重）
            List<LifeScenarioResponseDTO> finalResult = new ArrayList<>();
            Set<Long> processedProductIds = new HashSet<>(); // 用于跟踪已处理的商品ID
            
            for (Map<String, Object> scenario : result) {
                // 转换为DTO
                LifeScenarioResponseDTO dto = convertToLifeScenarioResponseDTO(scenario);
                dto.setScenarioTimeType(dimensions.timePeriod);
                dto.setScenarioDayType(dimensions.dayType);
                dto.setScenarioSeasonType(dimensions.seasonType);
                
                // 解析recommended_products字段（JSON字符串格式）
                String recommendedProductsJson = (String) scenario.get("recommended_products");
                if (recommendedProductsJson != null && !recommendedProductsJson.isEmpty()) {
                    try {
                        // 解析JSON字符串为商品ID列表
                        List<Long> productIds = new com.fasterxml.jackson.databind.ObjectMapper()
                                .readValue(recommendedProductsJson, new com.fasterxml.jackson.core.type.TypeReference<List<Long>>() {});
                        
                        // 过滤掉已经处理过的商品ID
                        List<Long> uniqueProductIds = productIds.stream()
                                .filter(id -> !processedProductIds.contains(id))
                                .collect(java.util.stream.Collectors.toList());
                        
                        // 记录已处理的商品ID
                        uniqueProductIds.forEach(processedProductIds::add);
                        
                        // 设置推荐商品列表
                        dto.setRecommendedProducts(uniqueProductIds);
                    } catch (Exception e) {
                        log.warn("解析推荐商品JSON失败: {}", recommendedProductsJson);
                        // 如果解析失败，设置为空列表
                        dto.setRecommendedProducts(new ArrayList<>());
                    }
                } else {
                    dto.setRecommendedProducts(new ArrayList<>());
                }
                
                finalResult.add(dto);
            }
            
            // 4. 确保返回最多4条记录（前端横向滚动需要更多数据）
            return finalResult.size() > 4 ? finalResult.subList(0, 4) : finalResult;
        } catch (Exception e) {
            log.error("获取生活场景推荐失败，userId: {}, dateTime: {}", userId, dateTime, e);
            return List.of();
        }
    }
    



    @Override
    public List<CommunityHotListResponseDTO> getCommunityHotList() {
        try {
            List<Map<String, Object>> recommendations = productRecommendationMapper.findCommunityHotList();
            
            // 转换为DTO并添加排名信息
            List<CommunityHotListResponseDTO> dtos = new ArrayList<>();
            for (int i = 0; i < recommendations.size(); i++) {
                CommunityHotListResponseDTO dto = convertToCommunityHotListResponseDTO(recommendations.get(i));
                dto.setRank(i + 1); // 设置排名
                dtos.add(dto);
            }
            
            return dtos;
        } catch (Exception e) {
            log.error("获取社区热榜推荐失败", e);
            return List.of();
        }
    }

    @Override
    public List<PersonalizedDiscoveryResponseDTO> getPersonalizedDiscoveryStream(Long userId) {
        try {
            List<Map<String, Object>> recommendations = productRecommendationMapper.findPersonalizedDiscoveryStream(userId);
            
            // 后端去重逻辑：基于 item_id 去重，保留第一个出现的
            List<Map<String, Object>> distinctRecommendations = recommendations.stream()
                .filter(item -> item.get("item_id") != null)
                .collect(Collectors.toMap(
                    item -> item.get("item_id"), // key: item_id
                    item -> item,                 // value: item本身
                    (existing, replacement) -> existing // 重复时保留第一个
                ))
                .values()
                .stream()
                .collect(Collectors.toList());
            
            // 转换为DTO
            return distinctRecommendations.stream()
                .map(this::convertToPersonalizedDiscoveryResponseDTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取个性化发现流推荐失败，userId: {}", userId, e);
            return List.of();
        }
    }

    // ==================== DTO转换方法 ====================

    /**
     * 智能排序算法 - 多维度综合评分
     */
    private List<Map<String, Object>> sortRecommendationsByIntelligentAlgorithm(List<Map<String, Object>> recommendations) {
        if (recommendations == null || recommendations.isEmpty()) {
            return recommendations;
        }
        
        // 为每个推荐项计算综合评分
        List<Map<String, Object>> scoredRecommendations = new ArrayList<>();
        
        for (Map<String, Object> recommendation : recommendations) {
            // 计算综合评分（权重可调整）
            double recommendationScore = getDoubleValue(recommendation.get("recommendation_score")) * 0.4; // 推荐分数权重40%
            double viewCountScore = Math.log10(getDoubleValue(recommendation.get("view_count")) + 1) * 0.3; // 浏览量权重30%
            double ratingScore = getDoubleValue(recommendation.get("avg_rating")) * 0.2; // 评分权重20%
            double priceScore = (1.0 - Math.min(getDoubleValue(recommendation.get("price")) / 1000.0, 1.0)) * 0.1; // 价格权重10%
            
            double totalScore = recommendationScore + viewCountScore + ratingScore + priceScore;
            
            // 添加评分到推荐项
            Map<String, Object> scoredRecommendation = new HashMap<>(recommendation);
            scoredRecommendation.put("intelligent_score", totalScore);
            scoredRecommendations.add(scoredRecommendation);
        }
        
        // 按综合评分降序排序
        scoredRecommendations.sort((a, b) -> {
            Double scoreA = getDoubleValue(a.get("intelligent_score"));
            Double scoreB = getDoubleValue(b.get("intelligent_score"));
            return Double.compare(scoreB, scoreA); // 降序排序
        });
        
        return scoredRecommendations;
    }

    /**
     * 将Map转换为DailyDiscoveryResponseDTO
     */
    private DailyDiscoveryResponseDTO convertToDailyDiscoveryResponseDTO(Map<String, Object> map) {
        DailyDiscoveryResponseDTO dto = new DailyDiscoveryResponseDTO();
        
        if (map.get("item_id") != null) {
            dto.setItemId(((Number) map.get("item_id")).toString());
        }
        if (map.get("item_type") != null) {
            dto.setItemType((String) map.get("item_type"));
        }
        if (map.get("title") != null) {
            dto.setTitle((String) map.get("title"));
        }
        if (map.get("image_url") != null) {
            dto.setImageUrl((String) map.get("image_url"));
        }
        if (map.get("view_count") != null) {
            dto.setViewCount(getIntegerValue(map.get("view_count")));
        }
        if (map.get("avg_rating") != null) {
            dto.setAvgRating(BigDecimal.valueOf(getDoubleValue(map.get("avg_rating"))));
        }
        if (map.get("goods_slogan") != null) {
            dto.setGoodsSlogan((String) map.get("goods_slogan"));
        }
        if (map.get("price") != null) {
            dto.setPrice(BigDecimal.valueOf(getDoubleValue(map.get("price"))));
        }
        if (map.get("original_price") != null) {
            dto.setOriginalPrice(BigDecimal.valueOf(getDoubleValue(map.get("original_price"))));
        }
        if (map.get("recommendation_score") != null) {
            dto.setRecommendationScore(BigDecimal.valueOf(getDoubleValue(map.get("recommendation_score"))));
        }
        
        return dto;
    }

    /**
     * 场景维度信息类
     */
    private static class ScenarioDimensions {
        String timePeriod;    // 时间段：morning/afternoon/evening/night
        String dayType;       // 日期类型：weekday/weekend/holiday
        String seasonType;    // 季节类型：spring/summer/autumn/winter
    }

    /**
     * 将日期时间字符串解析为场景维度
     */
    private ScenarioDimensions parseDateTimeToDimensions(String dateTime) {
        ScenarioDimensions dimensions = new ScenarioDimensions();
        
        // 如果未提供日期时间，使用当前时间
        java.time.LocalDateTime now;
        if (dateTime == null || dateTime.trim().isEmpty()) {
            now = java.time.LocalDateTime.now();
        } else {
            try {
                // 解析ISO格式的日期时间字符串
                now = java.time.LocalDateTime.parse(dateTime);
            } catch (Exception e) {
                // 解析失败，使用当前时间
                now = java.time.LocalDateTime.now();
            }
        }
        
        // 计算时间段
        int hour = now.getHour();
        if (hour >= 5 && hour < 12) {
            dimensions.timePeriod = "morning";
        } else if (hour >= 12 && hour < 18) {
            dimensions.timePeriod = "afternoon";
        } else if (hour >= 18 && hour < 23) {
            dimensions.timePeriod = "evening";
        } else {
            dimensions.timePeriod = "night";
        }
        
        // 计算日期类型
        java.time.DayOfWeek dayOfWeek = now.getDayOfWeek();
        if (dayOfWeek == java.time.DayOfWeek.SATURDAY || dayOfWeek == java.time.DayOfWeek.SUNDAY) {
            // TODO: 这里可以添加节假日判断逻辑
            dimensions.dayType = "weekend";
        } else {
            dimensions.dayType = "weekday";
        }
        
        // 计算季节类型
        int month = now.getMonthValue();
        if (month >= 3 && month <= 5) {
            dimensions.seasonType = "spring";
        } else if (month >= 6 && month <= 8) {
            dimensions.seasonType = "summer";
        } else if (month >= 9 && month <= 11) {
            dimensions.seasonType = "autumn";
        } else {
            dimensions.seasonType = "winter";
        }
        
        return dimensions;
    }

    /**
     * 将Map转换为LifeScenarioResponseDTO
     */
    private LifeScenarioResponseDTO convertToLifeScenarioResponseDTO(Map<String, Object> map) {
        LifeScenarioResponseDTO dto = new LifeScenarioResponseDTO();
        
        if (map.get("recommendation_title") != null) {
            dto.setRecommendationTitle((String) map.get("recommendation_title"));
        }
        if (map.get("recommendation_description") != null) {
            dto.setRecommendationDescription((String) map.get("recommendation_description"));
        }
        
        return dto;
    }

    /**
     * 将Map转换为CommunityHotListResponseDTO
     */
    private CommunityHotListResponseDTO convertToCommunityHotListResponseDTO(Map<String, Object> map) {
        CommunityHotListResponseDTO dto = new CommunityHotListResponseDTO();
        
        if (map.get("item_id") != null) {
            dto.setItemId(((Number) map.get("item_id")).longValue());
        }
        if (map.get("title") != null) {
            dto.setTitle((String) map.get("title"));
        }
        if (map.get("image_url") != null) {
            dto.setImageUrl((String) map.get("image_url"));
        }
        if (map.get("sales_count") != null) {
            dto.setSalesCount(getIntegerValue(map.get("sales_count")));
        }
        if (map.get("view_count") != null) {
            dto.setViewCount(getIntegerValue(map.get("view_count")));
        }
        if (map.get("avg_rating") != null) {
            dto.setAvgRating(BigDecimal.valueOf(getDoubleValue(map.get("avg_rating"))));
        }
        if (map.get("goods_slogan") != null) {
            dto.setGoodsSlogan((String) map.get("goods_slogan"));
        }
        
        return dto;
    }

    /**
     * 将Map转换为PersonalizedDiscoveryResponseDTO
     */
    private PersonalizedDiscoveryResponseDTO convertToPersonalizedDiscoveryResponseDTO(Map<String, Object> map) {
        PersonalizedDiscoveryResponseDTO dto = new PersonalizedDiscoveryResponseDTO();
        
        if (map.get("item_id") != null) {
            dto.setItemId(((Number) map.get("item_id")).longValue());
        }
        if (map.get("title") != null) {
            dto.setTitle((String) map.get("title"));
        }
        if (map.get("image_url") != null) {
            dto.setImageUrl((String) map.get("image_url"));
        }
        if (map.get("recommendation_score") != null) {
            dto.setRecommendationScore(BigDecimal.valueOf(getDoubleValue(map.get("recommendation_score"))));
        }
        if (map.get("goods_slogan") != null) {
            dto.setGoodsSlogan((String) map.get("goods_slogan"));
        }
        if (map.get("recommendation_type") != null) {
            dto.setRecommendationType((String) map.get("recommendation_type"));
        }
        
        return dto;
    }
}