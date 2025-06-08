package com.example.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 商品核心信息实体
 * 匹配优化后的product表结构
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private Long categoryId;
    private Long shopId;
    private Long brandId;
    private Integer status;       // 0: 草稿, 1: 在线, 2: 下线
    private Integer auditStatus;  // 0: 待审核, 1: 已通过, 2: 未通过
    private BigDecimal price;     // 基础销售价格
    private BigDecimal originalPrice; // 原价/市场价
    private Integer totalStock;   // 商品总库存
    private Integer totalSales;   // 商品总销量
    private Integer weight;       // 排序权重
    private Date createTime;
    private Date updateTime;
    private Boolean deleted;
    
    // 非持久化字段，用于API展示
    private ProductContent content;
    private ProductSpecification specifications;
    private List<ProductSku> skus;
    private ProductStatistics statistics;
    private ProductMarketing marketing;
    private List<ProductCategoryRelation> categoryRelations;
    private List<ProductPromotion> promotions;
    
    // 扩展字段，用于推荐系统和前端展示
    @JsonProperty("matchScore")
    private Integer matchScore;   // 匹配度分数
    
    @JsonProperty("shop")
    private Shop shop;            // 店铺信息
    
    @JsonProperty("shopName")
    private String shopName;      // 店铺名称
    
    @JsonProperty("shopAvatarUrl")
    private String shopAvatarUrl; // 店铺头像
    
    // 审核备注信息（非持久化）
    private String auditRemark;
    
    // 兼容前端展示，用soldCount替代totalSales
    @JsonProperty("soldCount")
    public Integer getSoldCount() {
        return totalSales;
    }
    
    @JsonIgnore
    public void setSoldCount(Integer soldCount) {
        this.totalSales = soldCount;
    }
    
    // 兼容前端展示，用stock替代totalStock
    @JsonProperty("stock")
    public Integer getStock() {
        return totalStock;
    }
    
    @JsonIgnore
    public void setStock(Integer stock) {
        this.totalStock = stock;
    }
}