package com.dailydiscover.recommendation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.recommendation.mapper.ProductRecommendationMapper;
import com.dailydiscover.recommendation.model.ProductRecommendation;
import com.dailydiscover.recommendation.dto.RelatedProductDTO;
import com.dailydiscover.recommendation.dto.DailyDiscoveryResponseDTO;
import com.dailydiscover.recommendation.dto.LifeScenarioResponseDTO;
import com.dailydiscover.recommendation.dto.CommunityHotListResponseDTO;
import com.dailydiscover.recommendation.dto.PersonalizedDiscoveryResponseDTO;
import com.dailydiscover.recommendation.dto.RecommendationProductDTO;
import com.dailydiscover.recommendation.dto.GuidedOptionDTO;
import com.dailydiscover.recommendation.dto.GuidedProductDTO;
import com.dailydiscover.common.util.DateUtils;
import com.dailydiscover.recommendation.service.ProductRecommendationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@Slf4j
public class ProductRecommendationServiceImpl extends ServiceImpl<ProductRecommendationMapper, ProductRecommendation> implements ProductRecommendationService {
    
    @Autowired
    private ProductRecommendationMapper productRecommendationMapper;
    
    @Override
    public List<ProductRecommendation> getRecommendationsByProductId(Long productId) {
        try {
            return productRecommendationMapper.findRelatedProductsByProductId(productId, 10);
        } catch (Exception e) {
            log.error("获取商品推荐失败，productId: {}", productId, e);
            return List.of();
        }
    }
    
    @Override
    public List<ProductRecommendation> getRecommendationsByType(String recommendationType) {
        try {
            return productRecommendationMapper.findRecommendationsByType(recommendationType, 20);
        } catch (Exception e) {
            log.error("获取特定类型推荐失败，recommendationType: {}", recommendationType, e);
            return List.of();
        }
    }
    
    @Override
    public List<ProductRecommendation> getGeneralRecommendations(int limit) {
        try {
            return productRecommendationMapper.findGeneralRecommendations(limit);
        } catch (Exception e) {
            log.error("获取通用推荐失败，limit: {}", limit, e);
            return List.of();
        }
    }
    
    @Override
    public List<ProductRecommendation> getComplementaryRecommendations(Long productId) {
        try {
            return productRecommendationMapper.findComplementaryRecommendations(productId, 10);
        } catch (Exception e) {
            log.error("获取搭配推荐失败，productId: {}", productId, e);
            return List.of();
        }
    }
    
    @Override
    public List<RelatedProductDTO> getProductDetailRecommendations(Long productId, Double currentPrice, Integer limit) {
        try {
            int finalLimit = limit != null ? limit : 10;
            
            // 获取相似商品推荐
            List<Map<String, Object>> similarProducts = productRecommendationMapper.findSimilarProducts(productId, finalLimit);
            
            // 获取搭配商品推荐
            List<Map<String, Object>> complementaryProducts = productRecommendationMapper.findComplementaryProducts(productId, finalLimit);
            
            // 如果当前价格不为空，获取价格敏感推荐
            List<Map<String, Object>> priceSensitiveProducts = new ArrayList<>();
            if (currentPrice != null) {
                priceSensitiveProducts = productRecommendationMapper.findPriceSensitiveProducts(productId, currentPrice, finalLimit);
            }
            
            // 合并所有推荐结果，去重
            Set<Long> processedIds = new HashSet<>();
            List<RelatedProductDTO> result = new ArrayList<>();
            
            // 处理相似商品
            for (Map<String, Object> product : similarProducts) {
                Long id = ((Number) product.get("id")).longValue();
                if (!processedIds.contains(id)) {
                    result.add(convertToRelatedProductDTO(product, "similar"));
                    processedIds.add(id);
                }
            }
            
            // 处理搭配商品
            for (Map<String, Object> product : complementaryProducts) {
                Long id = ((Number) product.get("id")).longValue();
                if (!processedIds.contains(id)) {
                    result.add(convertToRelatedProductDTO(product, "complementary"));
                    processedIds.add(id);
                }
            }
            
            // 处理价格敏感商品
            for (Map<String, Object> product : priceSensitiveProducts) {
                Long id = ((Number) product.get("id")).longValue();
                if (!processedIds.contains(id)) {
                    result.add(convertToRelatedProductDTO(product, "price_sensitive"));
                    processedIds.add(id);
                }
            }
            
            return result;
        } catch (Exception e) {
            log.error("获取商品详情页推荐失败，productId: {}, currentPrice: {}", productId, currentPrice, e);
            return List.of();
        }
    }
    
    // ==================== 首页推荐四模块 ====================
    
    @Override
    public List<DailyDiscoveryResponseDTO> getDailyDiscoveryRecommendations(Long userId, Integer limit, Integer page) {
        try {
            int finalLimit = limit != null ? limit : 20;
            
            // 基于场景的商品推荐
            List<Map<String, Object>> productMaps = productRecommendationMapper.findDailyDiscoveryProducts(finalLimit);
            
            List<DailyDiscoveryResponseDTO> result = new ArrayList<>();
            for (Map<String, Object> productMap : productMaps) {
                DailyDiscoveryResponseDTO dto = new DailyDiscoveryResponseDTO();
                dto.setItemId(String.valueOf(productMap.get("item_id")));
                dto.setTitle((String) productMap.get("title"));
                dto.setImageUrl((String) productMap.get("image_url"));
                dto.setPrice(convertToDouble(productMap.get("price")));
                dto.setOriginalPrice(convertToDouble(productMap.get("original_price")));
                dto.setGoodsSlogan((String) productMap.get("goods_slogan"));
                dto.setDescription((String) productMap.get("scene_description"));
                result.add(dto);
            }
            
            return result;
        } catch (Exception e) {
            log.error("获取今日发现推荐失败，userId: {}, limit: {}, page: {}", userId, limit, page, e);
            return List.of();
        }
    }
    
    @Override
    public List<LifeScenarioResponseDTO> getLifeScenarioRecommendations(Long userId, String dateTime) {
        try {
            // 解析时间参数
            String targetDate = parseTargetDate(dateTime);
            String timePeriod = parseTimePeriod(dateTime);
            
            // 获取生活场景推荐
            List<Map<String, Object>> scenarioMaps = productRecommendationMapper.findLifeScenarioRecommendations(targetDate, timePeriod);
            
            List<LifeScenarioResponseDTO> result = new ArrayList<>();
            for (Map<String, Object> scenarioMap : scenarioMaps) {
                LifeScenarioResponseDTO dto = new LifeScenarioResponseDTO();
                dto.setTitle((String) scenarioMap.get("title"));
                dto.setDescription((String) scenarioMap.get("description"));
                dto.setTimePeriod((String) scenarioMap.get("time_period"));
                dto.setTargetDate(DateUtils.convertDateToString(scenarioMap.get("target_date")));
                dto.setCoverImage((String) scenarioMap.get("cover_image"));
                dto.setProductCount(getIntegerValue(scenarioMap.get("product_count")));
                
                // 获取场景关联的商品
                Long sceneId = ((Number) scenarioMap.get("scene_id")).longValue();
                List<Map<String, Object>> productMaps = productRecommendationMapper.findProductsBySceneId(sceneId);
                
                List<RecommendationProductDTO> productList = new ArrayList<>();
                for (Map<String, Object> productMap : productMaps) {
                    RecommendationProductDTO productDTO = new RecommendationProductDTO();
                    productDTO.setId(((Number) productMap.get("product_id")).longValue());
                    productDTO.setTitle((String) productMap.get("title"));
                    productDTO.setImageUrl((String) productMap.get("main_image_url"));
                    
                    Object price = productMap.get("max_price");
                    if (price != null) {
                        productDTO.setPrice(convertToDouble(price));
                    }
                    
                    Object originalPrice = productMap.get("min_price");
                    if (originalPrice != null) {
                        productDTO.setOriginalPrice(convertToDouble(originalPrice));
                    }
                    
                    // 使用商品适配理由作为描述
                    productDTO.setDescription((String) productMap.get("product_reason"));
                    
                    productList.add(productDTO);
                }
                dto.setRecommendedProducts(productList);
                
                result.add(dto);
            }
            
            return result;
        } catch (Exception e) {
            log.error("获取生活场景推荐失败，userId: {}, dateTime: {}", userId, dateTime, e);
            return List.of();
        }
    }
    
    @Override
    public List<CommunityHotListResponseDTO> getCommunityHotList() {
        try {
            List<Map<String, Object>> hotProducts = productRecommendationMapper.findCommunityHotList();
            
            List<CommunityHotListResponseDTO> result = new ArrayList<>();
            for (Map<String, Object> product : hotProducts) {
                CommunityHotListResponseDTO dto = new CommunityHotListResponseDTO();
                dto.setItemId(String.valueOf(product.get("item_id")));
                dto.setTitle((String) product.get("title"));
                dto.setImageUrl((String) product.get("image_url"));
                dto.setSalesCount(getIntegerValue(product.get("sales_count")));
                dto.setViewCount(getIntegerValue(product.get("view_count")));
                dto.setRating(convertToDouble(product.get("avg_rating")));
                dto.setGoodsSlogan((String) product.get("goods_slogan"));
                dto.setPrice(convertToDouble(product.get("price")));
                dto.setOriginalPrice(convertToDouble(product.get("original_price")));
                result.add(dto);
            }
            
            return result;
        } catch (Exception e) {
            log.error("获取社区热榜推荐失败", e);
            return List.of();
        }
    }
    
    @Override
    public List<PersonalizedDiscoveryResponseDTO> getPersonalizedDiscoveryStream(Long userId) {
        try {
            List<Map<String, Object>> personalizedProducts = productRecommendationMapper.findPersonalizedDiscoveryStream(userId);
            
            List<PersonalizedDiscoveryResponseDTO> result = new ArrayList<>();
            for (Map<String, Object> product : personalizedProducts) {
                PersonalizedDiscoveryResponseDTO dto = new PersonalizedDiscoveryResponseDTO();
                dto.setItemId(String.valueOf(product.get("item_id")));
                dto.setTitle((String) product.get("title"));
                dto.setImageUrl((String) product.get("image_url"));
                dto.setRecommendationScore(convertToDouble(product.get("recommendation_score")));
                dto.setGoodsSlogan((String) product.get("goods_slogan"));
                dto.setRecommendationType((String) product.get("recommendation_type"));
                dto.setPrice(convertToDouble(product.get("price")));
                dto.setOriginalPrice(convertToDouble(product.get("original_price")));
                result.add(dto);
            }
            
            return result;
        } catch (Exception e) {
            log.error("获取个性化发现流推荐失败，userId: {}", userId, e);
            return List.of();
        }
    }
    
    // ==================== 引导推荐接口 ====================
    
    @Override
    public List<GuidedOptionDTO> getGuidedOptions() {
        try {
            List<GuidedOptionDTO> options = new ArrayList<>();
            
            // 第一轮引导选项
            options.add(new GuidedOptionDTO("cheap", "便宜"));
            options.add(new GuidedOptionDTO("quality", "高质量"));
            options.add(new GuidedOptionDTO("new", "新品"));
            options.add(new GuidedOptionDTO("popular", "热门"));
            options.add(new GuidedOptionDTO("personalized", "个性化"));
            options.add(new GuidedOptionDTO("seasonal", "季节"));
            
            return options;
        } catch (Exception e) {
            log.error("获取引导推荐选项失败", e);
            return List.of();
        }
    }
    
    @Override
    public List<GuidedProductDTO> getGuidedProducts(String sessionId, String intentLabel, Integer limit, Long userId) {
        try {
            int finalLimit = limit != null ? limit : 6;
            
            // 性能优化：根据userId是否为null选择不同的查询方法
            List<Map<String, Object>> productMaps;
            if (userId != null) {
                // 个性化推荐
                productMaps = productRecommendationMapper.findPersonalizedGuidedProducts(userId, finalLimit);
            } else {
                // 通用推荐
                productMaps = productRecommendationMapper.findGeneralGuidedProducts(finalLimit);
            }
            
            // 转换为GuidedProductDTO
            List<GuidedProductDTO> result = new ArrayList<>();
            for (Map<String, Object> productMap : productMaps) {
                GuidedProductDTO dto = new GuidedProductDTO();
                dto.setId(String.valueOf(productMap.get("id")));
                dto.setTitle((String) productMap.get("title"));
                dto.setImageUrl((String) productMap.get("image_url"));
                dto.setPrice(String.valueOf(productMap.get("price")));
                dto.setCurrentPrice(String.valueOf(productMap.get("current_price")));
                dto.setOriginalPrice(String.valueOf(productMap.get("original_price")));
                
                // 安全转换数值类型
                Object rating = productMap.get("rating");
                if (rating != null) {
                    dto.setRating(convertToDouble(rating));
                }
                
                Object reviews = productMap.get("reviews");
                if (reviews != null) {
                    dto.setReviews(getIntegerValue(reviews));
                }
                
                dto.setCategory(String.valueOf(productMap.get("category")));
                result.add(dto);
            }
            
            return result;
        } catch (Exception e) {
            log.error("获取引导推荐商品失败，sessionId: {}, intentLabel: {}, userId: {}", sessionId, intentLabel, userId, e);
            return List.of();
        }
    }
    
    @Override
    public List<GuidedOptionDTO> generateNextOptions(List<GuidedProductDTO> products, String intentLabel, Integer round) {
        try {
            List<GuidedOptionDTO> nextOptions = new ArrayList<>();
            
            // 基于当前意图和商品特征生成下一级选项
            switch (intentLabel) {
                case "cheap":
                    nextOptions.add(new GuidedOptionDTO("budget", "预算内"));
                    nextOptions.add(new GuidedOptionDTO("discount", "折扣"));
                    nextOptions.add(new GuidedOptionDTO("value", "性价比"));
                    break;
                case "quality":
                    nextOptions.add(new GuidedOptionDTO("premium", "高端"));
                    nextOptions.add(new GuidedOptionDTO("durable", "耐用"));
                    nextOptions.add(new GuidedOptionDTO("brand", "品牌"));
                    break;
                case "new":
                    nextOptions.add(new GuidedOptionDTO("trending", "流行"));
                    nextOptions.add(new GuidedOptionDTO("innovative", "创新"));
                    nextOptions.add(new GuidedOptionDTO("limited", "限量"));
                    break;
                case "popular":
                    nextOptions.add(new GuidedOptionDTO("best_seller", "畅销"));
                    nextOptions.add(new GuidedOptionDTO("trending", "趋势"));
                    nextOptions.add(new GuidedOptionDTO("viral", "热门"));
                    break;
                case "personalized":
                    nextOptions.add(new GuidedOptionDTO("preferences", "偏好"));
                    nextOptions.add(new GuidedOptionDTO("history", "历史"));
                    nextOptions.add(new GuidedOptionDTO("similar", "相似"));
                    break;
                case "seasonal":
                    nextOptions.add(new GuidedOptionDTO("spring", "春季"));
                    nextOptions.add(new GuidedOptionDTO("summer", "夏季"));
                    nextOptions.add(new GuidedOptionDTO("autumn", "秋季"));
                    nextOptions.add(new GuidedOptionDTO("winter", "冬季"));
                    break;
                default:
                    // 默认选项
                    nextOptions.add(new GuidedOptionDTO("related", "相关"));
                    nextOptions.add(new GuidedOptionDTO("trending", "趋势"));
                    nextOptions.add(new GuidedOptionDTO("popular", "热门"));
            }
            
            return nextOptions;
        } catch (Exception e) {
            log.error("生成下一级推荐选项失败，intentLabel: {}, round: {}", intentLabel, round, e);
            return List.of();
        }
    }
    
    // ==================== 辅助方法 ====================
    
    private RelatedProductDTO convertToRelatedProductDTO(Map<String, Object> product, String recommendationType) {
        RelatedProductDTO dto = new RelatedProductDTO();
        dto.setId(String.valueOf(product.get("id")));
        dto.setName((String) product.get("name"));
        dto.setImageUrl((String) product.get("main_image_url"));
        dto.setRecommendationType(recommendationType);
        
        Object price = product.get("price");
        if (price != null) {
            dto.setPrice(convertToDouble(price));
        }
        
        Object score = product.get("similarity_score");
        if (score != null) {
            dto.setRecommendationScore(convertToDouble(score));
        }
        
        return dto;
    }
    
    private String parseTargetDate(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            // 默认使用当前日期
            return new java.sql.Date(System.currentTimeMillis()).toString();
        }
        
        try {
            // 简单的日期解析，假设格式为 yyyy-MM-dd
            return dateTime.split("T")[0];
        } catch (Exception e) {
            return new java.sql.Date(System.currentTimeMillis()).toString();
        }
    }
    
    private String parseTimePeriod(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            // 根据当前时间判断
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            
            if (hour >= 6 && hour < 12) {
                return "morning";
            } else if (hour >= 12 && hour < 18) {
                return "afternoon";
            } else if (hour >= 18 && hour < 22) {
                return "evening";
            } else {
                return "night";
            }
        }
        
        try {
            // 从日期时间字符串中提取小时
            String[] parts = dateTime.split("T");
            if (parts.length > 1) {
                String timePart = parts[1].split(":")[0];
                int hour = Integer.parseInt(timePart);
                
                if (hour >= 6 && hour < 12) {
                    return "morning";
                } else if (hour >= 12 && hour < 18) {
                    return "afternoon";
                } else if (hour >= 18 && hour < 22) {
                    return "evening";
                } else {
                    return "night";
                }
            }
        } catch (Exception e) {
            // 解析失败，返回默认值
        }
        
        return "morning"; // 默认值
    }
    
    private Double convertToDouble(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    private Integer getIntegerValue(Object value) {
        if (value == null) return 0;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
}