package com.example.model;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class Specification {
    private String name;  // 规格名称，如"颜色"、"尺寸"
    private List<String> values; // 规格值，如["红色","蓝色"]
    private BigDecimal price;    // 该规格对应价格
    private Integer stock;       // 该规格库存
} 