package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ProductMapper;
import com.dailydiscover.mapper.ProductRecommendationMapper;
import com.dailydiscover.model.ProductRecommendation;
import com.dailydiscover.model.dto.ProductBasicInfoDTO;
import com.dailydiscover.model.dto.RelatedProductDTO;
import com.dailydiscover.model.dto.DailyDiscoveryResponseDTO;
import com.dailydiscover.model.dto.LifeScenarioResponseDTO;
import com.dailydiscover.model.dto.CommunityHotListResponseDTO;
import com.dailydiscover.model.dto.PersonalizedDiscoveryResponseDTO;
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
            
            List<Map<String, Object>> recommendations = productRecommendationMapper.findDailyDiscoverProducts(userId, finalLimit, offset);
            
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
                .map(this::convertToDailyDiscoveryResponseDTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取今日发现推荐失败，userId: {}, page: {}", userId, page, e);
            return List.of();
        }
    }

    // 获取场景类型颜色 - 优化为浅灰/浅棕底色
    private String getScenarioColor(String title) {
        if (title == null) return "#f8fafc";
        if (title.contains("早餐") || title.contains("早晨") || title.contains("morning")) return "#f5f5f4";
        if (title.contains("午餐") || title.contains("中午") || title.contains("afternoon")) return "#f8fafc";
        if (title.contains("晚餐") || title.contains("晚上") || title.contains("evening")) return "#f3f4f6";
        if (title.contains("运动") || title.contains("健身")) return "#f0fdf4";
        if (title.contains("购物")) return "#fefce8";
        if (title.contains("工作") || title.contains("学习")) return "#eff6ff";
        return "#f8fafc";
    }

    // 生成推荐理由
    private String generateRecommendationReason(Map<String, Object> productData) {
        Double avgRating = getDoubleValue(productData.get("avg_rating"));
        Integer viewCount = getIntegerValue(productData.get("view_count"));
        Double recommendationScore = getDoubleValue(productData.get("recommendation_score"));
        String title = (String) productData.get("title");
        
        // 基于商品标题关键词生成特性推荐语
        String featureReason = generateFeatureReason(title);
        
        // 基于评分和浏览量的质量推荐语
        String qualityReason = generateQualityReason(avgRating, viewCount, recommendationScore);
        
        // 组合推荐语：特性 + 质量
        if (!featureReason.isEmpty() && !qualityReason.isEmpty()) {
            return featureReason + "，" + qualityReason;
        } else if (!featureReason.isEmpty()) {
            return featureReason;
        } else if (!qualityReason.isEmpty()) {
            return qualityReason;
        } else {
            return "为您精心挑选";
        }
    }
    
    // 基于商品特性生成推荐语
    private String generateFeatureReason(String title) {
        if (title == null) return "";
        
        // 智能设备类
        if (title.contains("智能") || title.contains("手表") || title.contains("耳机")) {
            if (title.contains("降噪")) {
                return "主动降噪技术，沉浸式体验";
            } else if (title.contains("运动")) {
                return "防水防汗设计，运动更自由";
            } else if (title.contains("无线")) {
                return "无线连接，摆脱束缚";
            } else {
                return "智能科技，便捷生活";
            }
        }
        
        // 电子设备类
        if (title.contains("手机") || title.contains("电脑") || title.contains("平板")) {
            if (title.contains("轻薄")) {
                return "轻薄便携，随时随地办公";
            } else if (title.contains("旗舰")) {
                return "旗舰配置，性能强劲";
            } else {
                return "高性能设备，效率倍增";
            }
        }
        
        // 家居生活类
        if (title.contains("家居") || title.contains("生活") || title.contains("装饰")) {
            return "提升生活品质，营造温馨氛围";
        }
        
        // 运动健身类
        if (title.contains("运动") || title.contains("健身") || title.contains("跑步")) {
            return "专业运动装备，助力健康生活";
        }
        
        // 美妆护肤类
        if (title.contains("美妆") || title.contains("护肤") || title.contains("化妆品")) {
            return "呵护肌肤，展现自信美丽";
        }
        
        // 服饰鞋包类
        if (title.contains("服装") || title.contains("鞋子") || title.contains("包包")) {
            return "时尚设计，彰显个性品味";
        }
        
        return "";
    }
    
    // 基于质量指标生成推荐语
    private String generateQualityReason(Double avgRating, Integer viewCount, Double recommendationScore) {
        // 评分优先
        if (avgRating != null && avgRating >= 4.8) {
            return "用户评分4.8+，品质有保障";
        } else if (avgRating != null && avgRating >= 4.5) {
            return "口碑优秀，值得信赖";
        } else if (avgRating != null && avgRating >= 4.0) {
            return "品质可靠，性价比高";
        }
        
        // 热度其次
        if (viewCount != null && viewCount > 500) {
            return "热门爆款，大家都在买";
        } else if (viewCount != null && viewCount > 200) {
            return "人气商品，广受好评";
        }
        
        // 推荐分数
        if (recommendationScore != null && recommendationScore >= 0.9) {
            return "高匹配度推荐，精准满足需求";
        } else if (recommendationScore != null && recommendationScore >= 0.8) {
            return "个性化推荐，符合您的偏好";
        }
        
        return "精心挑选，品质保证";
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

    // 获取场景类型图标文字 - 优化场景名，避免'生活'标签
    private String getScenarioType(String title) {
        if (title == null) return "场景推荐";
        if (title.contains("早餐") || title.contains("早晨") || title.contains("morning")) return "早餐推荐";
        if (title.contains("午餐") || title.contains("中午") || title.contains("afternoon")) return "午间推荐";
        if (title.contains("晚餐") || title.contains("晚上") || title.contains("evening")) return "晚间推荐";
        if (title.contains("运动") || title.contains("健身")) return "运动推荐";
        if (title.contains("购物")) return "购物推荐";
        if (title.contains("工作")) return "工作推荐";
        if (title.contains("学习")) return "学习推荐";
        if (title.contains("娱乐")) return "娱乐推荐";
        if (title.contains("社交")) return "社交推荐";
        return "场景推荐";
    }

    // 文案优化配置 - 情绪价值优先，画面感强，电商转化导向
    private static final Map<String, String[]> SCENARIO_TEXT_MAP = Map.of(
        // 早餐：突出元气、治愈，唤醒晨间情绪
        "早餐推荐", new String[]{"元气早餐", "一口热乎，治愈整个清晨"},
        // 午间：突出放松、犒劳，贴合午休场景
        "午间推荐", new String[]{"午间小憩", "犒劳努力的自己"},
        // 晚间：突出慵懒、治愈，营造放松氛围
        "晚间推荐", new String[]{"晚间治愈", "卸下疲惫，享受慢时光"},
        // 运动：突出活力、畅快，强化运动爽感
        "运动推荐", new String[]{"活力运动", "汗水淋漓，畅快出发"},
        // 购物：突出划算、心动，激发消费欲
        "购物推荐", new String[]{"心动好物", "划算入手，不花冤枉钱"},
        // 工作：突出高效、省心，贴合职场痛点
        "工作推荐", new String[]{"高效办公", "效率翻倍，告别加班"},
        // 学习：突出专注、高效，贴合学习场景
        "学习推荐", new String[]{"专注学习", "沉浸式学习，效率拉满"},
        // 娱乐：突出解压、快乐，强化休闲价值
        "娱乐推荐", new String[]{"解压娱乐", "快乐充电，治愈无聊"},
        // 社交：突出精致、体面，贴合社交需求
        "社交推荐", new String[]{"精致社交", "体面出场，氛围感拉满"}
    );



    // 优化主标题文案 - 简短更有吸引力
    private String getOptimizedTitle(String originalTitle, String scenarioType) {
        if (originalTitle == null) return "精选好物";
        String[] texts = SCENARIO_TEXT_MAP.get(scenarioType);
        return texts != null ? texts[0] : "精选好物";
    }

    // 优化副标题文案 - 简短更有吸引力
    private String getOptimizedSubtitle(String scenarioType) {
        String[] texts = SCENARIO_TEXT_MAP.get(scenarioType);
        return texts != null ? texts[1] : "为您精心挑选";
    }



    @Override
    public List<LifeScenarioResponseDTO> getLifeScenarioRecommendations(Long userId, String timeContext, String locationContext, String activityContext) {
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            
            // 解析locationContext获取locationKey
            String locationKey = extractLocationKey(locationContext);
            
            // 解析activityContext：如果前端传入则使用，否则智能推断
            String finalActivityContext = activityContext;
            if (finalActivityContext == null || finalActivityContext.trim().isEmpty()) {
                finalActivityContext = extractActivityContext(locationContext);
            }
            
            // 1. 先查询用户专属推荐（最多4条）
            if (userId != null) {
                List<Map<String, Object>> userRecommendations = productRecommendationMapper.findUserLifeScenarioRecommendations(userId, timeContext, finalActivityContext, locationKey);
                result.addAll(userRecommendations);
            }
            
            // 2. 如果用户专属推荐不足4条，补充通用推荐
            if (result.size() < 4) {
                List<Map<String, Object>> generalRecommendations = productRecommendationMapper.findGeneralLifeScenarioRecommendations(timeContext, finalActivityContext, locationKey);
                
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
                dto.setScenarioTimeType(timeContext);
                dto.setScenarioActivityType(finalActivityContext);
                dto.setScenarioLocationType(locationContext);
                
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
            log.error("获取生活场景推荐失败，userId: {}, timeContext: {}, locationContext: {}", userId, timeContext, locationContext, e);
            return List.of();
        }
    }
    
    /**
     * 将ProductBasicInfoDTO转换为Map格式
     */
    private Map<String, Object> convertProductBasicInfoToMap(com.dailydiscover.model.dto.ProductBasicInfoDTO product) {
        Map<String, Object> productMap = new HashMap<>();
        productMap.put("id", product.getId());
        productMap.put("title", product.getTitle());
        productMap.put("description", product.getBrand() + " " + product.getModel());
        productMap.put("mainImageUrl", product.getMainImageUrl());
        productMap.put("category", product.getCategoryId());
        productMap.put("price", product.getMaxPrice());
        productMap.put("originalPrice", product.getMinPrice());
        productMap.put("discount", product.getDiscount());
        productMap.put("rating", product.getAverageRating());
        productMap.put("reviewCount", product.getTotalReviews());
        productMap.put("sales", product.getSalesCount());
        productMap.put("isNew", true); // 默认值
        productMap.put("isHot", false); // 默认值
        return productMap;
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

    /**
     * 获取activityContext（基于位置和时间智能推断）
     */
    private String extractActivityContext(String locationContext) {
        if (locationContext == null || locationContext.trim().isEmpty()) {
            return "relax"; // 默认休闲活动
        }
        
        String location = locationContext.trim().toLowerCase();
        
        // 基于位置智能推断活动类型
        switch (location) {
            case "home":
                return "relax"; // 家庭场景通常是休闲活动
            case "office":
                return "work"; // 办公室场景通常是工作活动
            case "commute":
            case "subway":
                return "commute"; // 通勤场景
            case "gym":
                return "fitness"; // 健身房场景
            case "outdoor":
                return "travel"; // 户外场景可能是旅行
            default:
                return "relax"; // 默认休闲活动
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
     * 将Map转换为DailyDiscoveryResponseDTO
     */
    private DailyDiscoveryResponseDTO convertToDailyDiscoveryResponseDTO(Map<String, Object> map) {
        DailyDiscoveryResponseDTO dto = new DailyDiscoveryResponseDTO();
        
        if (map.get("item_id") != null) {
            dto.setItemId(((Number) map.get("item_id")).longValue());
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
        if (map.get("confidence_score") != null) {
            dto.setConfidenceScore(BigDecimal.valueOf(getDoubleValue(map.get("confidence_score"))));
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