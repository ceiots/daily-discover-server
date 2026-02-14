package com.dailydiscover.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductDetail {
    private Long id;
    private Long productId;
    private String specifications;
    private String features;
    private String usageInstructions;
    private String precautions;
    private String packageContents;
    private String warrantyInfo;
    private String shippingInfo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public List<String> getFeaturesList() {
        if (features == null || features.isEmpty()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(features, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
    
    public List<String> getUsageInstructionsList() {
        if (usageInstructions == null || usageInstructions.isEmpty()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(usageInstructions, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
    
    public List<String> getPrecautionsList() {
        if (precautions == null || precautions.isEmpty()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(precautions, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
    
    public List<String> getPackageContentsList() {
        if (packageContents == null || packageContents.isEmpty()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(packageContents, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}