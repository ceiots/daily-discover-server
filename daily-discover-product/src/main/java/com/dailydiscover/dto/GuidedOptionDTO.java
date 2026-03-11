package com.dailydiscover.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 引导推荐选项DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuidedOptionDTO {
    private String id;
    private String label;
    private List<GuidedOptionDTO> children;
    
    /**
     * 简化构造函数（用于快速创建选项）
     */
    public GuidedOptionDTO(String id, String label) {
        this.id = id;
        this.label = label;
    }
}