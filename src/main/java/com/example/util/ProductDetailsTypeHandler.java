package com.example.util;

import org.apache.ibatis.type.MappedTypes;
import com.example.common.config.ListTypeHandler;
import com.example.model.ProductDetail;
import java.util.List;

@MappedTypes(List.class)
public class ProductDetailsTypeHandler extends ListTypeHandler<ProductDetail> {

    public ProductDetailsTypeHandler() {
        super(ProductDetail.class);
    }
}