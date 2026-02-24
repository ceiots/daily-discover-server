package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ScenarioRecommendationMapper;
import com.dailydiscover.model.ScenarioRecommendation;
import com.dailydiscover.service.ScenarioRecommendationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ScenarioRecommendationServiceImpl extends ServiceImpl<ScenarioRecommendationMapper, ScenarioRecommendation> implements ScenarioRecommendationService {
    
    @Autowired
    private ScenarioRecommendationMapper scenarioRecommendationMapper;
    
    @Override
    public List<ScenarioRecommendation> getRecommendationsByScenarioType(String scenarioType) {
        return scenarioRecommendationMapper.findByScenarioType(scenarioType);
    }
    
    @Override
    public List<ScenarioRecommendation> getPersonalizedScenarioRecommendations(Long userId) {
        return scenarioRecommendationMapper.findByUserId(userId);
    }
    
    @Override
    public List<ScenarioRecommendation> getRecommendationsByProductId(Long productId) {
        // 由于表结构中没有 product_id 字段，此方法无法实现
        log.warn("表结构中没有 product_id 字段，无法根据商品ID查询场景推荐");
        return java.util.Collections.emptyList();
    }
    
    @Override
    public ScenarioRecommendation createScenarioRecommendation(String scenarioType, String timeSlot, 
                                                               java.util.List<Long> recommendedProducts, 
                                                               String scenarioStory) {
        ScenarioRecommendation recommendation = new ScenarioRecommendation();
        recommendation.setScenarioType(scenarioType);
        recommendation.setTimeSlot(timeSlot);
        recommendation.setScenarioStory(scenarioStory);
        
        // 将推荐商品列表转换为JSON字符串
        if (recommendedProducts != null && !recommendedProducts.isEmpty()) {
            try {
                recommendation.setRecommendedProducts(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(recommendedProducts));
            } catch (JsonProcessingException e) {
                log.error("JSON序列化失败: {}", e.getMessage());
                // 如果序列化失败，可以设置为空字符串或记录错误
                recommendation.setRecommendedProducts("");
            }
        }
        
        save(recommendation);
        return recommendation;
    }
    
    @Override
    public List<ScenarioRecommendation> getByScenarioType(String scenarioType) {
        return getRecommendationsByScenarioType(scenarioType);
    }
    
    @Override
    public List<ScenarioRecommendation> getByUserId(Long userId) {
        return getPersonalizedScenarioRecommendations(userId);
    }
    
    @Override
    public List<ScenarioRecommendation> getActiveRecommendations() {
        // 由于表结构中没有 status 字段，返回所有推荐
        return list();
    }
    
    @Override
    public com.dailydiscover.model.dto.RecommendationPhraseResponseDTO generateRecommendationPhrase(
            com.dailydiscover.model.dto.RecommendationPhraseRequestDTO request) {
        
        ScenarioRecommendation recommendation = new ScenarioRecommendation();
        recommendation.setScenarioType(request.getScenarioType());
        recommendation.setRecommendationTitle(request.getRecommendationTitle());
        recommendation.setRecommendationDescription(request.getRecommendationDescription());
        recommendation.setRecommendationMetadata(request.getRecommendationMetadata());
        
        // 设置默认的推荐商品（取第一个商品ID）
        if (request.getProductIds() != null && !request.getProductIds().isEmpty()) {
            try {
                String recommendedProducts = new com.fasterxml.jackson.databind.ObjectMapper()
                        .writeValueAsString(request.getProductIds());
                recommendation.setRecommendedProducts(recommendedProducts);
            } catch (JsonProcessingException e) {
                log.error("JSON序列化失败: {}", e.getMessage());
                recommendation.setRecommendedProducts("");
            }
        }
        
        save(recommendation);
        
        // 转换为响应DTO
        com.dailydiscover.model.dto.RecommendationPhraseResponseDTO response = new com.dailydiscover.model.dto.RecommendationPhraseResponseDTO();
        response.setId(recommendation.getId());
        response.setScenarioType(recommendation.getScenarioType());
        response.setRecommendationTitle(recommendation.getRecommendationTitle());
        response.setRecommendationDescription(recommendation.getRecommendationDescription());
        response.setRecommendationMetadata(recommendation.getRecommendationMetadata());
        response.setCreatedAt(recommendation.getCreatedAt());
        
        return response;
    }
    
    @Override
    public java.util.List<com.dailydiscover.model.dto.RecommendationPhraseResponseDTO> getRecommendationPhrases(
            String scenarioType, String approvalStatus) {
        
        // 构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ScenarioRecommendation> queryWrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        
        if (scenarioType != null) {
            queryWrapper.eq("scenario_type", scenarioType);
        }
        
        if (approvalStatus != null) {
            queryWrapper.apply("recommendation_metadata->'$.approval_status' = {0}", approvalStatus);
        }
        
        List<ScenarioRecommendation> recommendations = list(queryWrapper);
        
        // 转换为响应DTO列表
        return recommendations.stream().map(recommendation -> {
            com.dailydiscover.model.dto.RecommendationPhraseResponseDTO response = new com.dailydiscover.model.dto.RecommendationPhraseResponseDTO();
            response.setId(recommendation.getId());
            response.setScenarioType(recommendation.getScenarioType());
            response.setRecommendationTitle(recommendation.getRecommendationTitle());
            response.setRecommendationDescription(recommendation.getRecommendationDescription());
            response.setRecommendationMetadata(recommendation.getRecommendationMetadata());
            response.setCreatedAt(recommendation.getCreatedAt());
            return response;
        }).collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public boolean updateRecommendationMetadata(Long id, String metadataJson) {
        ScenarioRecommendation recommendation = getById(id);
        if (recommendation == null) {
            log.warn("推荐语不存在，ID: {}", id);
            return false;
        }
        
        recommendation.setRecommendationMetadata(metadataJson);
        return updateById(recommendation);
    }
    
    @Override
    public boolean recordRecommendationUsage(Long id) {
        ScenarioRecommendation recommendation = getById(id);
        if (recommendation == null) {
            log.warn("推荐语不存在，ID: {}", id);
            return false;
        }
        
        try {
            // 解析当前元数据
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode metadata = mapper.readTree(
                    recommendation.getRecommendationMetadata() != null ? 
                    recommendation.getRecommendationMetadata() : "{}");
            
            // 更新使用次数
            int usageCount = metadata.has("usage_count") ? metadata.get("usage_count").asInt() : 0;
            usageCount++;
            
            // 更新最后使用时间
            String currentTime = java.time.LocalDateTime.now().toString();
            
            // 创建新的元数据
            com.fasterxml.jackson.databind.node.ObjectNode newMetadata = mapper.createObjectNode();
            if (metadata.isObject()) {
                newMetadata.setAll((com.fasterxml.jackson.databind.node.ObjectNode) metadata);
            }
            newMetadata.put("usage_count", usageCount);
            newMetadata.put("last_used_at", currentTime);
            
            recommendation.setRecommendationMetadata(newMetadata.toString());
            return updateById(recommendation);
            
        } catch (Exception e) {
            log.error("更新推荐语使用记录失败: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public com.dailydiscover.model.dto.RecommendationPhraseResponseDTO getProductRecommendation(
            Long productId, String scenarioType) {
        
        ScenarioRecommendation recommendation = scenarioRecommendationMapper
                .findRecommendationByProductAndScenario(productId, scenarioType);
        
        if (recommendation == null) {
            log.debug("未找到商品 {} 在场景 {} 下的推荐语", productId, scenarioType);
            return null;
        }
        
        // 转换为响应DTO
        com.dailydiscover.model.dto.RecommendationPhraseResponseDTO response = 
                new com.dailydiscover.model.dto.RecommendationPhraseResponseDTO();
        response.setId(recommendation.getId());
        response.setScenarioType(recommendation.getScenarioType());
        response.setRecommendationTitle(recommendation.getRecommendationTitle());
        response.setRecommendationDescription(recommendation.getRecommendationDescription());
        response.setRecommendationMetadata(recommendation.getRecommendationMetadata());
        response.setCreatedAt(recommendation.getCreatedAt());
        
        // 记录使用次数
        recordRecommendationUsage(recommendation.getId());
        
        return response;
    }
}