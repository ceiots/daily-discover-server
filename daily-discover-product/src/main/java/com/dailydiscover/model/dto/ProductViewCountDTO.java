package com.dailydiscover.model.dto;

import lombok.Data;

@Data
public class ProductViewCountDTO {
    private Long product_id;
    private Long view_count;
    
    // Getter方法用于兼容原有代码
    public Long getProductId() {
        return product_id;
    }
    
    public Long getViewCount() {
        return view_count;
    }
}