package com.dailydiscover.dto;

import lombok.Data;
import java.util.List;

/**
 * 引导推荐选项DTO
 */
@Data
public class GuidedOptionDTO {
    private String id;
    private String label;
    private List<GuidedOptionDTO> children;
}