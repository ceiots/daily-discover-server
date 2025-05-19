package com.example.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Specification {
    private Long id;     // 添加id字段
    private String name;  // 规格名称，如"颜色"、"尺寸"
    private List<String> values; // 规格值，如["红色","蓝色"]
}