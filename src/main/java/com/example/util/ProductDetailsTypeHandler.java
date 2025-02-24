package com.example.util;

import org.apache.ibatis.type.MappedTypes;
import com.example.model.ProductDetail;
import java.util.List;

@MappedTypes(List.class)
public class ProductDetailsTypeHandler extends JsonTypeHandler<List<ProductDetail>> {

    public ProductDetailsTypeHandler() {
        super(List.class, ProductDetail.class);
    }
}