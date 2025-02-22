package com.example.util;

import java.util.List;

import org.apache.ibatis.type.MappedTypes;

import com.example.model.ProductDetail;

@MappedTypes(List.class)
public class ProductDetailsTypeHandler extends ListTypeHandler<ProductDetail> {
    public ProductDetailsTypeHandler() {
        super(ProductDetail.class);
    }
} 