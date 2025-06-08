package com.example.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductAttribute {
    private String name;
    private String value;
    private Integer sort;
} 